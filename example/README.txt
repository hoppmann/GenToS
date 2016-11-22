####################################
######## run GenToS example ########
####################################

In order to run the GenToS example the GEFOS database(GEFOS.db in the folder GEFOS) needs to be extracted using the followin command:

7z x GEFOS.db.7z

Also adapt the path to GenToS in the file "01-run_GenToS.sh" using a text editor. Then run the bash script 01-run_GenToS.sh using the command:

. 01-run_GenToS.sh


It might be neccessary to change the path to the local R installation in the GenToS config file. Open "GenToS/bin/GenToS.config" in your favorit text editor an modify the path to Rscript starter. 

In the spec file it is important not to remove the headings "#dbGene" and "dbSNP" since they are needed to identify the correct database in GenToS.




#######################################
######## run create_db example ########
#######################################

To create the example db first extract the GWAS summary files using the command

7z x FNBMD_with_position_hg19.txt.7z
7z x LSBMD_with_position_hg19.txt.7z

Then execute the shell script 01-create_db.sh using the command

. 01-create_db.sh



