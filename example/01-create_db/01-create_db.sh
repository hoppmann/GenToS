######## this shell command runs the perl script to create a database from the GEFOS summary data

# create table LSBMD in database GEFOS
../../create-db.pl \
-names names.file \
-file LSBMD_with_position_hg19.txt \
-dbname GEFOS \
-table LSBMD_hg19

#create table FNBMD in database GEFOS
../../create-db.pl \
-names names.file \
-file FNBMD_with_position_hg19.txt \
-dbname GEFOS \
-table FNBMD_hg19
