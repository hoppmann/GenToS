package de.gentos.lists.initialize;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.gentos.general.files.ConfigFile;
import de.gentos.general.files.HandleFiles;
import de.gentos.general.files.ReadInGenes;
import de.gentos.gwas.initialize.InitDatabase;
import de.gentos.lists.initialize.data.QueryList;
import de.gentos.lists.initialize.data.ResourceLists;
import de.gentos.lists.initialize.options.GetListOptions;

public class InitializeListsMain {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	private GetListOptions options;
	private ConfigFile config;
	private HandleFiles log;
	private String[] args;
	private Map<String, ResourceLists> resources;
	private List<QueryList> queryLists;

	
	
	// gene database
	private String[] columnNamesGenes = {"gene"};
	private ReadInGenes genes;





	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public InitializeListsMain(String[] args) {

		// retrieve variables
		this.args = args;
		queryLists = new LinkedList<>();


		// init config file
		initConfig();

		// init options
		initOptions();

		// open output directory
		new File(options.getOutDir()).mkdirs();

		// init log file
		initLog();

		// write commandline options to log file
		log.writeOutFile("\n######## Starting initializing ########");
		log.writeFile("Options chosen:\n" + Arrays.toString(args) + "\n");


		// read in resource lists
		initResources();

		// read in gene list
		initQueryGenes();

		// read in gene database
		readInGenes();
		
		// make log entry 
		log.writeOutFile("\n#### Finished Initializing");

	}






	/////////////////////////
	//////// methods ////////
	/////////////////////////


	/////////////////////
	//// read in config file and save to variables
	private void initConfig() {
		// catch errors caused by config file
		try {
			config = new ConfigFile();

		} catch (IOException e) {
			System.out.println("\n FAILURE: Couldn't find GenToS.config file.\n");
			System.exit(1);
		}
	}



	///////////////////
	//// get command line options

	private void initOptions() {

		options = new GetListOptions(args, config);

	}

	////////////
	///// open log file
	private void initLog() {

		log = new HandleFiles();

		log.openWriter(options.getOutDir() + System.getProperty("file.separator") + options.getLog());
	}


	//////////////
	// read in resource lists
	private void initResources(){

		// make log entry
		log.writeOutFile("\n#### Reading in resource gene lists!");


		// init resource map
		resources = new HashMap<>();




		///////////////////////////////////
		//////// get list of all resource files in resource direcotry
		List<String> folderContent = new ArrayList<String>();

		// get direcrory of resource files
		String resourceDir = options.getListDir();
		File dir = new File(resourceDir);

		// check if path is directory
		if (!dir.isDirectory()) {
			log.writeError("Directory " + resourceDir + " not found.");
			System.exit(1);
		}

		// check if directory is empty else read in names 
		if (new File(resourceDir).list().length > 0) {

			// get list of files in directory
			File[] listOfFiles = new File(resourceDir).listFiles();
			for (File file : listOfFiles) {
				if (file.isFile()) {
					folderContent.add(resourceDir + File.separator + file.getName());
				}
			}	
		} else {
			// if directory is empty make log error entry and exit 
			log.writeError(resourceDir + " does not contain any files!");
			System.exit(1);
		}






		///////////////////////////////
		//////// read in resource lists 
		HandleFiles file = new HandleFiles();
		file.setLog(log);
		for (String  curFile : folderContent) {

			// prepare list for collection of resource data for hash
			//			List<ResourceData> data = new LinkedList<>();

			// create file resource file object for information storage
			ResourceLists curListIn = new ResourceLists();

			// open file
			file.setLog(log);
			LinkedList<String> lines = file.openFile(curFile, false);

			// read in line wise and save correnspondingly
			for (String line : lines ) {

				// split line 
				String[] splitLine = line.split("\t");

				// extract header and check if file is sorted or not.
				if (splitLine[0].startsWith("#")){

					// save header
					curListIn.addHeader(splitLine);

					// check if list is sorted if so store it
					if (splitLine[0].toUpperCase().startsWith("#SORTED")){
						curListIn.setSorted(true);
					}

				} else {

					// if not header section any more save as gene element
					curListIn.addGeneLine(splitLine);
					curListIn.addGenes(splitLine[0]);
				}
			}

			// save current list to map containing all resource lists
			resources.put(curFile, curListIn);


		}	
	}


	/////////////////
	// read in query gene list
	private void initQueryGenes(){

		// make log entry
		log.writeOutFile("\n#### Reading in query gene list");

		// init variables
		// init class to work with file
		HandleFiles file = new HandleFiles();

		// check if query list or list of queries is chosen
		if (options.getListOfQueries() != null && !options.getListOfQueries().isEmpty()){

			
			// open name of list of query file
			String listOfQuery = options.getListOfQueries();
			file.exist(listOfQuery);
			LinkedList<String> allLists = file.openFile(listOfQuery, true);
			
			// read in all query lists
			for (String curListPath : allLists){
			
				// check if file exists
				file.exist(curListPath);
				
				//// open file
				LinkedList<String> lines = file.openFile(curListPath, true);
				
				
				// split file and take first entry, for the case that information ist stored in input list
				QueryList curList = new QueryList(curListPath);
				queryLists.add(curList);
				for (String line : lines){
					String[] splitString = line.split("\t");
					curList.addGene(splitString[0]);
				}
				
			}
			
		} else if (options.getQuery() != null && !options.getQuery().isEmpty()){

			// get file path
			String queryGeneFile = options.getQuery();

			// check if input list exists		
			file.exist(queryGeneFile);


			//// open file
			// open file and save query list
			LinkedList<String> lines  = file.openFile(queryGeneFile, true);

			// split file and take first entry, for the case that information ist stored in input list
			QueryList curList = new QueryList(queryGeneFile);
			queryLists.add(curList);
			for (String line : lines){
				String[] splitString = line.split("\t");
				curList.addGene(splitString[0]);
			}

		}
	}

	//// initialize gene db
	public void readInGenes() {

		// make log entry
		log.writeOutFile("\n##Initializing gene database.");
		
		// gather variables
		String geneDB = getOptions().getDbGeneName();
		String geneTable = getOptions().getDbGeneTable();

		
		
		// connect to databse
		InitDatabase db = new InitDatabase(geneDB, log);
		
		// check that needed table and column exists
		db.checkDatabases(geneTable, columnNamesGenes);

		// read in gene database
		genes = new ReadInGenes(this);

		
		
	}




	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////




	public ConfigFile getConfig() {
		return config;
	}

	public GetListOptions getOptions() {
		return options;
	}

	public HandleFiles getLog() {
		return log;
	}

	public Map<String, ResourceLists> getResources() {
		return resources;
	}

	public List<QueryList> getQueryLists() {
		return queryLists;
	}

	public ReadInGenes getGenes() {
		return genes;
	}






}
