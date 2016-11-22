package de.gentos.initialize;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import de.gentos.initialize.data.DbSnpInfo;
import de.gentos.initialize.options.GetOptions;
import de.gentos.main.HandleFile;

// This class initializes all primary steps, like opening a file for log; reading in the options; checking the config file ...

public class InitializeMain {


	// define variables
	GetOptions options;
	ConfigFile config;
	HandleFile log;
	ReadInGwasData gwasData;
	ReadInGenes readGenes;
	Map<Integer, DbSnpInfo> dbSNPInfo;


	// //////////////////////////////////////////////
	// ////// constructor to sequentially init steps

	public InitializeMain(String[] args) {

		// read in config file
		InitConfig();

		// get options
		InitOptions(args);

		
		// open output directory
		new File(options.getDir()).mkdir();

		// open log file
		initLog();

		// write commandline options to log file
		log.writeOutFile("########Starting initializing ########\n");
		log.writeFile("Options chosen:\n" + Arrays.toString(args) + "\n");

		// check databases for correctness
		checkDatabases();


		// read in data
		readData();
		
		// write to log file init done
		log.writeOutFile("Finished initialization\n");

	}







	// /////////////////////////////////
	// ////// Methods for initialization

	// open log file
	private void initLog() {

		log = new HandleFile();
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







	// init commandline options and write them to logfile and check basic options for correctness

	private void InitOptions(String[] args) {
		options = new GetOptions(args, config);

	}







	//// check databases for correctness

	private void checkDatabases() {
		// start database
		//		-> check for tables
		//		-> check for columns

		// dbGene

		String dbGenePath = options.getDbGene();
		String dbGeneTable = options.getTableGene();
		String[] columnNamesGenes = {"gene", "chr", "start", "stop"};
		InitDatabase dbGene = new InitDatabase(dbGenePath, log);
		dbGene.checkDatabases(dbGeneTable, columnNamesGenes);

		// dbSNP
		// iterate over all given databases and tables
		Map<Integer, DbSnpInfo> dbSNPInfo = options.getDbSNP();



		for (Integer currentDbKey : dbSNPInfo.keySet()) {

			// get dbSNP and tableSNP values from current db to check
			String dbPath = dbSNPInfo.get(currentDbKey).getDbPath();
			String tableName = dbSNPInfo.get(currentDbKey).getTableName();
			String[] columnNamesSNPs = {options.getColrsID(), options.getColChr(), options.getColPos(), options.getColPval()};

			// check current db
			InitDatabase dbSNP = new InitDatabase(dbPath, log);
			dbSNP.checkDatabases(tableName, columnNamesSNPs);
		}
	}

	
	
	
	
	
	
	// read in gene data from gene db and gwas data from gwas db's and save it in hash
	private void readData() {
		// get gene info
		readGenes = new ReadInGenes(this);



		// dbSNP
		// iterate over all given databases and tables
		dbSNPInfo = options.getDbSNP();

		
		// instantiate new data object
		
		for (Integer currentDbKey : dbSNPInfo.keySet()) {

			// for each gwas file and gwas table read in data from database
			String dbSnpPath = dbSNPInfo.get(currentDbKey).getDbPath();
			String tableSnpName = dbSNPInfo.get(currentDbKey).getTableName();
			
			// read in gwas data from db and save object to corresponding hash entry
			gwasData = new ReadInGwasData(this);
			gwasData.readGWASFile(dbSnpPath, tableSnpName, readGenes);
			
			dbSNPInfo.get(currentDbKey).setGwasData(gwasData);
			
		}
	}


	// ////////////////////////
	// ////// Getter and Setter

	
	public Map<Integer, DbSnpInfo> getDbSNPInfo() {
		return dbSNPInfo;
	}

	public ReadInGwasData getGwasData() {
		return gwasData;
	}

	public ReadInGenes getReadGenes() {
		return readGenes;
	}

	public GetOptions getOptions() {
		return options;
	}

	public ConfigFile getConfig() {
		return config;
	}

	public HandleFile getLog() {
		return log;
	}


}
