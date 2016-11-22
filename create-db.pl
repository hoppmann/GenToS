#!/usr/bin/perl
use feature ':5.10';
use strict 'vars';
use warnings;
use Getopt::Long;
use Pod::Usage;
use DBI;


=head1 NAME

=head1 SYNOPSIS

	######## Options ########
	
	-help		brief help message
	-man		full documentation
	-dbname		prefix name of database (defalut is "database")
	-file		name of input file for database
	-delim		file delimiter "tab (default), space and comma"
	-names		file containing names and options for database (specifications see below)
	-table		name of table to use (defalut is "table")
	-log		name of the logfile to be used
	-vebose		gives extra information to console
	-command	gives out only commands, no execution


	######## specification for names-files ########
	
	Names file has to be a tab seperated column wise file containing (in following order):
	"name in database" 	-> the name entry that will be added in the databse
	"name in File" 		-> the name given in inputfile (all elements "#" in the beginning of names will be deleted by the program)
	"options"		-> options defining the entry (e.g. entrytype (varchar, int...), primary key, unique, not null...)
	
	headerlines and comments starting with "#" and empty lines will be scipped, names in DB MUSN'T contain "-"
	e.g:
	
	#nameInFile     nameInDB        options
	gene	gene	varchar(60)	not null
	chrom	chr	int	not null
	txStart	start	int	not null
	txEnd	stopr	int	not null

	
=head1 DESCRIPTION

=cut

# parse command line options
my $help;
my $man;
my $delim = "\\t";
my $dbname;
my $fileIN;
my $fileNames;
my $table;
my $verbose;
my $command_only;
my $index;
my $logfile;

my $result = GetOptions (	
			"help"		=> \$help,
			"man"		=> \$man,
			"delim=s"	=> \$delim,
			"dbname=s"	=> \$dbname,
			"file=s"	=> \$fileIN,
			"names=s"	=> \$fileNames,
			"table=s"	=> \$table,
			"verbose"	=> \$verbose,
			"command"	=> \$command_only,
			"index"		=> \$index,
			"log=s"		=> \$logfile,
			);
				
pod2usage(-exitstatus => 1, -verbose => 1) if $help;
pod2usage(-exitstatus => 0, -verbose => 2) if $man;
($result) or pod2usage(2);

#######################
#### open Log file ####
#######################
$logfile or $logfile = "logfile.log";
open (my $LOG, "> $logfile") or die "Can't open file $logfile.\n";

##################################################
######## Check parameters or use Defaults ########
##################################################

#easy ones :D
#database name and input file
$dbname or $dbname = "database";
$table or $table = "tab";
if ($table eq "table") {die ("Name of table musn't be \"table\"\n")};

#delimiter
if ($delim eq "tab" || $delim eq "\\t"){
	$delim = "\\t";
} elsif ($delim eq "space"){
	$delim = " ";
} elsif ($delim eq "comma"){
	$delim = ",";
} else { 
	say $LOG, "\n$delim not available for option delim. \n";
	pod2usage( -exitstatus => 2, -verbose => 0 );
}

#check input text file
if (!$fileIN){
	say "\nOption \"-file\" not given.\n";
	pod2usage (-exitstatus => 1, -verbose => 1)
} elsif (!-f $fileIN){
	die "File \"$fileIN\" not found.\n";
}

#check input name file
if (!$fileNames){
	say "\nOption \"-names\" not given.\n";
	pod2usage (-exitstatus => 1, -verbose => 1)
} elsif (!-f $fileNames){
	die "File \"$fileNames\" not found.\n"
}

#####################################
######## gather informations ########
#####################################

#column names
open (NAME, "< $fileNames") or die "Can't open file $fileNames. \n";

#read in file and save in hash
my @dbEntries;
while (<NAME>){
	chomp;
	next if ((/^#/) or ($_ eq ""));
	my @tmp = split (/\t/, $_);
	push (@dbEntries, \@tmp);
}
close (NAME);
!$verbose or say_out($LOG, "Gathering info from \"$fileNames\" done.");

#################################
######## Create Database ########
#################################
(!$verbose) or say_out($LOG, "Opening input file.");
# open file to add to database
open (IN, "< $fileIN") or die "Can't open file $fileIN. \n";
my @whole_file = <IN>;
close (IN);

!$verbose or say_out ($LOG, "Opening input file successful.");

##############################
#### create table entries ####

##### lookup headerposition
#prepare header
my $header = shift (@whole_file);
$header =~ s/#//g;
my @header = split(/$delim/,$header);
chomp (@header);

my $check = 0;
my $counter = 0;
foreach (@dbEntries){
	my $name = $$_[0];
	my $headerCounter = 0;
		foreach (@header){
			chomp;
			if ($_ eq $name){
			push (@{$dbEntries[$counter]}, $headerCounter);
			$check++;
			}
			$headerCounter++;
		}
	$counter++;
}

#save header names for error 
my @colNamesIN;
foreach (@dbEntries){
	push (@colNamesIN, $$_[0]);
}

#check if all header found
if ($#dbEntries+1 != $check) {
	say_out ($LOG, "Not all headers found.\nHeader chosen:\n@colNamesIN.\nHeader in file:\n@header.\nIs the delim option set correctly?\n");
	die "\n";
}
!$verbose or say_out ($LOG, "All header found.");

#opendatabase
my $driver   = "SQLite";
$dbname = $dbname.".db";
my $dsn = "DBI:$driver:dbname=$dbname";
my $userid = "";
my $password = "";
my $dbh = DBI->connect($dsn, $userid, $password, { RaiseError => 1 })
                      or die $DBI::errstr;
!$verbose or say_out ($LOG, "Successfully connected to databse.");



#set pragmas
!$verbose or say_out ($LOG, "Setting pragmas.");
$dbh->do("PRAGMA main.page_size = 4096; PRAGMA main.cache_size=10000; PRAGMA main.locking_mode=EXCLUSIVE; PRAGMA main.synchronous=NORMAL; PRAGMA main.journal_mode=WAL;");

#####create table

#remove table if already exists
(!$verbose) or say_out ($LOG, "Creating table.");
$dbh -> do ("drop table if exists $table");

my $command = create_table_command ($table, @dbEntries);

if ($command_only){
	say_out ($LOG, "\nCreate table command:\n".$command."\n");
}else {
	$dbh -> do ($command);
}
say_out ($LOG, "Adding values.");
(!$verbose) or say_out($LOG, "Creating table done.");



#prepare adding of values
(!$verbose) or say_out ($LOG, "Preparing to add values.");
my $sth;
$command = prepare_add_value_command ($table, @dbEntries);


if ($command_only){
	die "\nPrepare value command:\n". $command ."\n";
} else {
	$sth = $dbh -> prepare ($command);
}
(!$verbose) or say_out($LOG, "Preparing values done.");


#adding values
(!$verbose) or say_out ($LOG, "Adding values");
$counter = 0;
my $number_lines = @whole_file;
$dbh->do("begin transaction;");
foreach (@whole_file){
	chomp;
	
	#divide to single entries
	my @entries;
	my @tmp = split (/$delim/, $_);
	chomp (@tmp);

	#corresponding entries
	foreach (@dbEntries){
		push @entries, $tmp[${$_}[$#{$_}]];
	}
	$counter++;

	#add entries
	add_values(@entries);
	
	#write every 10k entries in db and make statement of advance
	if ($counter % 10000 == 0){
		my $percent = sprintf "%.2f", $counter / $number_lines * 100;
		$dbh -> do ("commit transaction");
		$dbh -> do ("begin transaction");
		say_out ($LOG, "$counter/$number_lines entries processed. $percent% done.");
	}
}

$dbh->do("commit transaction;");

$dbh -> disconnect();

(!$verbose) or say_out ($LOG, "\nAll commits done. Database closed.");

say "\nDatabase successuflly created. $counter elemets added to table \"$table\" in \"$dbname\".";
say "\nJob finished.\n\n";


#############################
######## Subroutines ########
#############################

#############################
#### print in file and screen

sub say_out {
	my $file = shift @_;
	say "@_";
	say $file "@_";
}

############################################
####create command for add value preparation
sub prepare_add_value_command{

	my $command = "insert into ";
	$command .= shift @_;
	$command .=" ( ";

	#add table entries
	my $counter = 0;
	foreach (@_){
		if ($counter == 0 ){
			$command .= $$_[1];
		} else {
			$command .= ", ". $$_[1];
		}
		$counter++;
	}
	$command .= " ) values ( ";
	
	#fill up with "?" for later value addition
	for (my $i = 0; $i < $counter; $i++){
		if ($i == 0){
			$command .= "?";
		} else {
			$command .= ", ?";
		}
	}
	$command .= " )";

	return $command;
}

##################################
#create command for "create table"
sub create_table_command{
	my $command = "create table ";
	$command .= shift @_;
	$command .= " ( ";
	my $count = 0;
	
	do {
		#extract needed values from @dbEntries
		my @tmp = @{$_[$count]};
		splice (@tmp, 0, 1);
		pop @tmp;

		#extend command
		$command .= join (" ",@tmp);
		$count++;
		if ($count < @_){
			$command .= ", ";
		}

	} while ($count < @_);
	$command .= " )";

	#give back command
	return $command;
}

####################
# add values to table
sub add_values {
	my $count = 1;
	foreach (@_){
	$sth-> bind_param ($count, $_);
	$count++;
	}
	$sth -> execute();
}
