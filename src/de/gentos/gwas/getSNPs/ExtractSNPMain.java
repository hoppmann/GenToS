package de.gentos.gwas.getSNPs;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.gentos.general.files.HandleFiles;
import de.gentos.gwas.initialize.InitializeGwasMain;
import de.gentos.gwas.initialize.ReadInGwasData;
import de.gentos.gwas.initialize.data.GeneInfo;
import de.gentos.gwas.initialize.data.GwasDbInfo;
import de.gentos.gwas.initialize.options.GetGwasOptions;


/*
 * In this class the extraction of SNPs according to calculated
 * threshold is guided
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


	////////////////////
	//////// Constructor

	public ExtractSNPMain(InitializeGwasMain init, Integer curGwasDb) {

		// retrieve Variables
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
		ExtractDataMethods extract = new ExtractDataMethods(init);

		// make log entry that snps will be extracted
		log.writeOutFile("\n######## Start extracting SNPs ########\n");

		
		/*
		 * If ROIs are NOT based on BED file, read in current GWAS database
		 * in case of not BED based files a single read in is sufficient for all gene lists
		 * in case of BED based ROIs the read in needs to be done individually for each gene list
		 */
		
		if (! options.isBedFile()) {
			readInGwasDb(curGwasDb);
		}

		
		
		/* 
		 * extract all relevant SNPs
		 * 		get ROIs 
		 * 		get independent SNPs
		 */

		extractSnpInfo(curGwasDb, extract);
		
	}



	/////////////////////////
	//////// Methods //////// 
	/////////////////////////
	
	//////////////////////
	//////// read in current GWAS DB
	private void readInGwasDb(Integer curGwasDb) {
		
		init.readGwasDbStandard(curGwasDb);
		
	}
	
	
	
	
	// extract SNP informations
	private void extractSnpInfo(int currentGwasDb, ExtractDataMethods extract) {
		
		// initialize result file
		GwasDbInfo gwasDb = options.getGwasDbs().get(currentGwasDb);
		String dbName = gwasDb.getDbName(); 
		String tableName = gwasDb.getTableName();
		HandleFiles resultFile = new HandleFiles();
		resultFile.openWriter( options.getDir() + System.getProperty("file.separator") + dbName + "_" + tableName + ".txt");

		
		// make log entry for current DB
		log.writeOutFile("######## " + dbName + " " + tableName );
		
		
		
		// iterate over each query gene list
		for (String currentQueryGeneListName : init.getGeneLists().keySet()) {
			
			
			// log entry for current gene list
			log.writeOutFile("#### Running on " + currentQueryGeneListName);
			

			
			
			
			
			
			
			///////////////////
			//////// get the ROIs for each gene
			
			// make log entry
			log.writeOutFile("Extracting region of interest");
			// create new query gene map and copy information to it in order to avoid bugs during interation over several GWAS databases
			Map<String, GeneInfo> queryGenesChecked = new HashMap<>();
			
			if (options.isBedFile()) {
				
				// copy original hash to work on new hash each iteration step
				// else hash contains information of former runs
				queryGenesChecked.putAll(init.getGeneLists().get(currentQueryGeneListName));

			} else {

				// get current gene list but work on copy to avoid bugs during iteration
				Map<String, GeneInfo> curGeneList = init.getGeneLists().get(currentQueryGeneListName);

				// extract gene positions 
				extract.getGenePositions(curGeneList, queryGenesChecked);

			}


			
			
			
			
			
			//////////////////////
			//////// read GWAS file
			
			/* 
			 * in case of bad based ROIs read in GWAS summary data for the extracted ROIs 
			 * 	this is necessary since for each bed-file there is a different set of ROIs to 
			 * 	be extracted thus it needs to the implementations this late
			 */
			
			if (options.isBedFile()) {
				init.readGwasDbBed(currentGwasDb, currentQueryGeneListName);
			}

			
			

			////////////////////////
			//////// calculate get the number independent SNPs
			// make log entry
			log.writeOutFile("Getting independent SNPs");
			extract.extractIndep(queryGenesChecked);
			log.writeOutFile("Independent SNPs extracted from " + options.getIndepDB() +".");

			
			
			
			///////////////////////
			//////// calculate threshold
			
			// set variables
			ReadInGwasData gwasData = init.getGwasData();
			
			// write to log file 
			log.writeOutFile("Calculating threshold");

			// calculate threshold
			extract.calculateThresh(extract, gwasData, queryGenesChecked, currentGwasDb, currentQueryGeneListName);
			

			
			
			
			////////////////////////
			//////// extract SNPs from GWAS DB
			
			log.writeOutFile("Extracting SNPs with pVal lower than threshold.");
			extract.extractSNPs(gwasData, queryGenesChecked);

			
			// save hits per list for later use in validation
			int counter = 0;
			for (String gene : queryGenesChecked.keySet()){
				if (queryGenesChecked.get(gene).isHasHit()){
					counter++;
				}
			}
			options.getGwasDbs().get(currentGwasDb).putHitsPerList(currentQueryGeneListName, counter);

			// write out results in files
			log.writeOutFile("Writing results in file\n");
			String tableSNP = options.getGwasDbs().get(currentGwasDb).getTableName();
			String pathSNP = options.getGwasDbs().get(currentGwasDb).getDbPath();
			
			new WriteResult(queryGenesChecked, tableSNP, pathSNP, resultFile, options, csvDir, init, currentQueryGeneListName).write();

			
			
			
			
		}
		
		// close result file
		resultFile.closeFile();
	}
	
	
	
	
	
	
	


	/////////////////////////////////
	//////// Getter & Setter ////////
	/////////////////////////////////




	
	
	
	
	
}
