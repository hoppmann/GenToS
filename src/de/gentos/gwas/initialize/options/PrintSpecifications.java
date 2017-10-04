package de.gentos.gwas.initialize.options;

public class PrintSpecifications {
	
	// constructor (no further call needed)
	
	public PrintSpecifications() {
		printSpecs();
	}
	
	private void printSpecs () {
		String specFile = "######## Spec file specification ######## \n\n" +
	"This file is used for the option specFile. " +
	"The spec-file contains a list of databases and corresponding tables to iterate over. " +
	"There are two lines in a spec file that are mandatory for a correct match.\n" + 
	"Each of those lines starts with a '#' followed by either 'dbGene' or 'dbSNP' (verbatim, case sensitive). \n" +
	"\n" +
	"e.g:\n" +
	"#dbGene TableGene\n" +
	"examples/dbGENE.db		genes (only needed if custom gene database used)\n" +
	"\n" +
	"#dbSNP TableSNP\n" +
	"examples/dbSNPs.db	table1\n" +
	"examples/dbSNPs2.db	table2\n";	
		
	String dataBase = "######## Database specifications ########\n\n" +
	"The gene database needs 5 columns named 'gene', 'chr', 'start', 'stop' and 'length' (exact names mandatory)\n" +
	"	-gene: 	name of genes: 'varchar', 'not null'\n" +
	"	-chr: 	chromosome number as integer (e.g. 1, 12): 'int', 'not null'\n" +
	"	-start:	starting position of gene: 'int', 'not null'\n" +
	"	-stop:	ending position of gene: 'int', 'not null'\n" +
	"	\n" +
	"The SNP database needs 4 columns named 'rsid', 'chr', 'pos' and 'pval' (column names can be specified)\n" +
	"	-rsid:	name of SNPs: 'varchar', 'not null'\n" +
	"	-chr:	chromosome number as integer (e.g. 5, 13): 'int', 'not null'\n" +
	"	-pos:	position of the SNP: 'int', 'not null'\n" +
	"	-pval:	association p-value of corresponding snp: 'int', 'not null'\n";
		

	String indepDB = "######## Independent SNPs database ########\n\n" +
	"Each population needs its own database, with a table for each chromosome.\n" +
	"Table names consist of \"chr\" + the chromosome number (e.g. chr2) in lower case.\n" +
	"The table consists of two columns: \n" +
	"\trsid\tvarchar\tnot null\n" +
	"\tpos\tint\tnot null\n";
	
	String bedFile = "######## BED file ########\n\n" +
	"The bed file is a tab seperated file containing ROIs and has a specific order as shown below. "
	+ "It has to consist of at least 3 columns (chr, star, stop) and can have an additional gene name column. "
	+ "If a gene name column is present, the given ROIs are clustered according to the corresponding gene names. Else each ROI is treated seperately. "
	+ "In the file it can be mixed, if column 4 exists or not."
	+ "\n\n#header informations (will be skipped if starting with \"#\")"
	+ "\n#additional columns will be ignored if present"
	+ "\n#the chr column can be written as \"chr1\", \"CHR1\" or \"1\""
	+ "\n#chr	start	stop	geneName"
	+ "\n\ne.g."
	+ "\n#chr	start		stop		geneName"
	+ "\n14	92432335	92432455	TRIP11"
	+ "\nchr14	92432535	92432712	TRIP11"
	+ "\nCHR14	92432835	92432994";
			
			
	
	
	System.out.println("\n\n" + dataBase);
	System.out.println("\n\n" + specFile);
	System.out.println("\n\n" + indepDB);
	System.out.println("\n\n" + bedFile);
	System.out.println("\n\n");
	System.exit(0);
	
	}

}
