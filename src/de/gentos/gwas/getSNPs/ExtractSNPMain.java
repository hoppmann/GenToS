package de.gentos.gwas.getSNPs;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import de.gentos.general.files.HandleFiles;
import de.gentos.gwas.initialize.InitializeGwasMain;
import de.gentos.gwas.initialize.ReadInGwasData;
import de.gentos.gwas.initialize.data.GeneListInfo;
import de.gentos.gwas.initialize.options.GetGwasOptions;
import de.gentos.gwas.threshold.CreateThresh;


/*
 * In this class the extraction of SNPs according to calculated threshold
 * is guided
 */


public class ExtractSNPMain {

	//////////////////////
	//////// set variables

	InitializeGwasMain init;
	HandleFiles log;
	GetGwasOptions options;
	String colPVal;
	String colChr;
	String colPos;
	String colrsID;
	String csvDir;
	Map<String, Integer> hitsPerList = new HashMap<>(); 



	////////////////////
	//////// Constructor

	public ExtractSNPMain(InitializeGwasMain init) {

		// set Variables
		this.init = init;
		log = init.getLog();
		options = init.getGwasOptions();
		colPVal = options.getColPval();
		colPos = options.getColPos();
		colChr = options.getColChr();
		colrsID = options.getColrsID();


		// make csv directory
		csvDir = options.getDir() + System.getProperty("file.separator") + options.getCsvDir();
		new File(csvDir).mkdir();

		// initialize Extract class
		ExtractData extract = new ExtractData(init);

		// make log entry that snps will be extracted
		log.writeOutFile("\n######## Start extracting SNPs ########\n");

		////////////////
		////// Iterate over each dbSNP entry
		
		
		for (int currentDbSNP : init.getDbSNPInfo().keySet()) {
			
			// initialize result file
			String dbName = init.getDbSNPInfo().get(currentDbSNP).getDbName(); 
			String tableName = init.getDbSNPInfo().get(currentDbSNP).getTableName();

			HandleFiles resultFile = new HandleFiles();
			resultFile.openWriter( options.getDir() + System.getProperty("file.separator") + dbName + "_" + tableName + ".txt");

			
			// make log entry to current db
			log.writeOutFile("######## " + dbName + " " + tableName );
			
			
			// run over each gene list
			for (String currentListName : init.getGwasOptions().getGeneLists().keySet()) {

				
				// log entry for current gene list
				log.writeOutFile("#### Running on " + currentListName);

				// set variables
				ReadInGwasData gwasData = init.getDbSNPInfo().get(currentDbSNP).getGwasData();
				String pathSNP = init.getDbSNPInfo().get(currentDbSNP).getDbPath();  
				String tableSNP = init.getDbSNPInfo().get(currentDbSNP).getTableName();

				// write to log file 
				log.writeOutFile("Calculating threshold");


				// calculate threshold
				Map<String, GeneListInfo> queryGenesChecked = calculateThresh(extract, gwasData, currentListName, currentDbSNP);

				// extract snps according to threshold and get lowest pval among snps
				// Make log entry
				log.writeOutFile("Extracting SNPs with pVal lower than threshold.");
				extractSNPs(gwasData, queryGenesChecked, extract);
				
				
				// save hits per list for later use in validation
				int counter = 0;
				for (String gene : queryGenesChecked.keySet()){
					if (queryGenesChecked.get(gene).isHasHit()){
						counter++;
					}
				}
				init.getDbSNPInfo().get(currentDbSNP).putHitsPerList(currentListName, counter);
				hitsPerList.put(currentListName, counter);

				// write out results in files
				log.writeOutFile("Writing results in file\n");
				new WriteResult(queryGenesChecked, tableSNP, pathSNP, resultFile, options, csvDir, init, currentListName).write();
			}
			
			// close result file
			resultFile.closeFile();
		}


	}



	////////////////
	//////// Methods


	private Map<String, GeneListInfo> calculateThresh (ExtractData extract, ReadInGwasData gwasData, String curentListName, int currentDbSnp){

		// copy multimap gene to new multimap (new object each iteration)
		Map<String, GeneListInfo> queryGenesChecked = new HashMap<>();
		LinkedList<String> queryGenes = init.getGwasOptions().getGeneLists().get(curentListName); 

		// create thresh according to method chosen
		CreateThresh correction = new CreateThresh(init, gwasData);
		correction.choose(extract, queryGenes, queryGenesChecked);
		
		// save threshold for db and corresponding list
		init.getDbSNPInfo().get(currentDbSnp).addToMap(curentListName, correction.getThresh());
		
		
		return queryGenesChecked;
	}




	private void extractSNPs(ReadInGwasData gwasData, Map<String, GeneListInfo> currentGenes, ExtractData extract) {
		// execute extract SNPs from gwas database

		extract.extractSNPs(gwasData, currentGenes);

	}

	////////////
	//////// Getter

	public Map<String, Integer> getHitsPerList() {
		return hitsPerList;
	}



	
	
	
	
	
}
