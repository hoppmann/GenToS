package de.gentos.gwas.initialize;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import de.gentos.general.files.ConfigFile;
import de.gentos.general.files.HandleFiles;
import de.gentos.general.files.ReadInGeneDB;
import de.gentos.gwas.initialize.data.GeneInfo;
import de.gentos.gwas.initialize.data.GwasDbInfo;
import de.gentos.gwas.initialize.options.GetGwasOptions;

/* 
 * This class initializes all primary steps, like opening a file for logging;
 * reading in the options;
 * checking the config file
 * checking and reading in databases
 * */

public class InitializeGwasMain {


	// define variables
	GetGwasOptions options;
	ConfigFile config;
	HandleFiles log;
	ReadInGwasData gwasData;
	ReadInGeneDB readGenes;
	Map<String, Map<String, GeneInfo>> geneLists = new HashMap<>();


	// //////////////////////////////////////////////
	// ////// constructor to sequentially init steps

	public InitializeGwasMain(String[] args) {

		// read in config file
		InitConfig();

		// get options
		InitOptions(args);


		// open output directory
		new File(options.getDir()).mkdir();

		// open log file
		initLog();

		// write command line options to log file
		log.writeOutFile("########Starting initializing ########\n");
		log.writeFile("Options chosen:\n" + Arrays.toString(args) + "\n");

		
		// check databases for correctness
		checkDatabases();
		
		// read in data from databases
		readGeneDatabase();

		// read in gene lists
		readGeneLists();
		
		
		// write to log file init done
		log.writeOutFile("Finished initialization\n");

	}







	// /////////////////////////////////
	// ////// Methods for initialization

	// open log file
	private void initLog() {

		log = new HandleFiles();
		log.openWriter(options.getDir() + System.getProperty("file.separator") + options.getLog());
	}






	// read in config file and save to variables
	private void InitConfig() {
		// catch errors caused by config file
		try {
			config = new ConfigFile();

		} catch (IOException e) {
			System.out.println("\n FAILURE: Couldn't find GenToS.config file.\n");
			System.exit(1);
		}
	}







	// init command line options and write them to log file and check basic options for correctness

	private void InitOptions(String[] args) {
		options = new GetGwasOptions(args, config);

	}






	//////////////////////
	//// check databases for correctness

	private void checkDatabases() {
		// start database
		//		-> check for tables
		//		-> check for columns

		// dbGene

		String dbGenePath = options.getDbGene();
		String dbGeneTable = options.getTableGene();
		String[] columnNamesGenes = {"gene", "chr", "start", "stop"};
		InitDatabase dbGene = new InitDatabase(dbGenePath, log, true);
		dbGene.checkDatabases(dbGeneTable, columnNamesGenes);

		// SNP db
		// iterate over all given databases and tables
		Map<Integer, GwasDbInfo> allGwasDbs = options.getGwasDbs();

		for (Integer currentDbKey : allGwasDbs.keySet()) {

			// get dbSNP and tableSNP values from current DB to check
			String dbPath = allGwasDbs.get(currentDbKey).getDbPath();
			String tableName = allGwasDbs.get(currentDbKey).getTableName();
			String[] columnNamesSNPs = {options.getColrsID(), options.getColChr(), options.getColPos(), options.getColPval()};

			// check current db
			InitDatabase dbSNP = new InitDatabase(dbPath, log, true);
			dbSNP.checkDatabases(tableName, columnNamesSNPs);
		}
		
		
		// independent SNPs
		String dbIndepPath = options.getIndepDB();
		String[] columnNamesIndep = {"rsid", "pos"};
		for ( int chr = 1; chr <= 22; chr++) {
			InitDatabase dbIndep = new InitDatabase(dbIndepPath, log, false);
			dbIndep.checkDatabases("chr"+chr, columnNamesIndep);
		}
		
		// make log entry, that indep DB is ok
		log.writeOutFile("Reference DB successfully checked.");
		
	}






	/////////////////
	//// read in genes from gene db and GWAS data from GWAS db's and save it in hash
	private void readGeneDatabase() {


		////////////////
		//////// gene db
		// read in gene database for later extraction of gene positions
		// in case of bed-files the gene database is not needed
		if (! options.isBedFile()) {
			readGenes = new ReadInGeneDB(this);
		}

	}

	
	
	
	
	
	
	///////////////////
	//////// read in GWAS database

	public void readGwasDbStandard(int curGwasDb) {


			// for each GWAS file and GWAS table read in data from database
			GwasDbInfo gwasDbInfo = options.getGwasDbs().get(curGwasDb);

			String gwasDbPath = gwasDbInfo.getDbPath();
			String tableGwasDb = gwasDbInfo.getTableName();

			
			// read in GWAS data from DB and save object to corresponding hash entry
			gwasData = new ReadInGwasData(this);
			gwasData.readGWASFileStandard(gwasDbPath, tableGwasDb, readGenes);
	}

	
	
	
	
	
	
	/////////////////////////
	/* 
	 * read in GWAS database with bed-file option
	 * in this mode the keys will be integers instead of gene names
	*/
	 public void readGwasDbBed(int curGwasDb, String curGeneList) {
		
		 // for each GWAS file and GWAS table read in data from database

		 GwasDbInfo gwasDbInfo = options.getGwasDbs().get(curGwasDb);
		 String gwasDbPath = gwasDbInfo.getDbPath();
		 String tableGwasDb = gwasDbInfo.getTableName();

		 // read in GWAS data from DB and save object to corresponding hash entry
		 gwasData = new ReadInGwasData(this);
		 gwasData.readGwasFileBed(gwasDbPath, tableGwasDb, curGeneList);
	 }
	
	
	 
	 
	 
	 
	 
	//////////////////////////////////////
	//////// read in GenList and check if pure list or if bed file

	private void readGeneLists() {

		//////// read in gene lists, single genes or bed files
		// if a single gene is chosen add this gene 
		if (options.getCmd().hasOption("gene")) {

			// retrieve gene name from command line input
			String geneName = options.getSingleGene();

			// prepare map with gene as key
			Map<String, GeneInfo> curGene = new HashMap<>();
			curGene.put(geneName,new GeneInfo());

			geneLists.put("singleGene", curGene);

		}


		// if a list or a list collection is chosen read in the genes and save in hash 
		// key = listName; value = Map of gene infos
		if (options.getCmd().hasOption("list") || options.getCmd().hasOption("listCollection")) {

			// check for bed file option; if so load bed file not plain list
			if (options.isBedFile()) {

				for (String listPath : options.getListOfQueries()) {

					// prepare hash for current List
					Map<String, GeneInfo> curInpuList = new HashMap<>();

					// check if file exists and read in
					new HandleFiles().exist(listPath);

					curInpuList = new HandleFiles().readBed(listPath);
	
					
					// save current List in Hash of list collection
					String listName = FilenameUtils.getBaseName(listPath);
					geneLists.put(listName, curInpuList);

					
					
				}

				// load plain list of genes
			} else {
				for (String listPath: options.getListOfQueries()) {
					// get key = name of current list
					String key = FilenameUtils.getBaseName(listPath);

					// for each entry in this list save to the gene list map and save
					Map<String, GeneInfo> curInputList = new HashMap<>();
					for (String curGene : new HandleFiles().openFile(listPath, true)) {
						curInputList.put(curGene.replaceAll("\\s", ""), new GeneInfo());
					}

					// save gene query list in geneList hash containing all lists
					geneLists.put(key, curInputList);

				}

			}
		}
	}


	// ////////////////////////
	// ////// Getter and Setter


	public ReadInGwasData getGwasData() {
		return gwasData;
	}

	public ReadInGeneDB getReadGenes() {
		return readGenes;
	}
	

	public GetGwasOptions getGwasOptions() {
		return options;
	}

	public ConfigFile getConfig() {
		return config;
	}

	public HandleFiles getLog() {
		return log;
	}

	public GetGwasOptions getOptions() {
		return options;
	}

	public Map<String, Map<String, GeneInfo>> getGeneLists() {
		return geneLists;
	}








}
