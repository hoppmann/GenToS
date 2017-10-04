package de.gentos.geneSet.initialize;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.gentos.geneSet.initialize.data.InputList;
import de.gentos.geneSet.initialize.data.ResourceLists;
import de.gentos.geneSet.initialize.options.GetGeneSetOptions;
import de.gentos.general.files.ConfigFile;
import de.gentos.general.files.HandleFiles;
import de.gentos.general.files.ReadInGeneDB;
import de.gentos.gwas.initialize.InitDatabase;

public class InitializeGeneSetMain {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	private GetGeneSetOptions options;
	private ConfigFile config;
	private HandleFiles log;
	private String[] args;
	private Map<String, ResourceLists> resources;
	private List<InputList> inputLists;

	
	
	// gene database
	private String[] columnNamesGenes = {"gene"};
	private ReadInGeneDB genes;





	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public InitializeGeneSetMain(String[] args) {

		// retrieve variables
		this.args = args;
		inputLists = new LinkedList<>();


		// init config file
		initConfig();

		// init options
		initOptions();

		// open output directory
		new File(options.getOutDir()).mkdirs();

		// init log file
		initLog();

		// write command line options to log file
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

		options = new GetGeneSetOptions(args, config);

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
		//////// get list of all resource files in resource directory
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

			// create file resource file object for information storage
			ResourceLists curListIn = new ResourceLists();

			// open file
			file.setLog(log);
			LinkedList<String> lines = file.openFile(curFile, false);

			// read in line wise and save correspondingly
			for (String line : lines ) {

				// check if line is empty or contains only white spaces then skip line
				if (line.trim().isEmpty() || line.trim().equals("")) {
					continue;
				}
				
				// split line 
				String[] splitLine = line.split("\t");

				// extract header and check if file is sorted or not.
				if (splitLine[0].startsWith("#")){

					// check if list is sorted if so store it
					if (splitLine[0].toUpperCase().startsWith("#SORTED")){
						curListIn.setSorted(true);
					}

				} else {

					// if not header section any more save as gene element
					// check if there is an entry for the gene name
					// gene column is empty if so skip
					if (!splitLine[0].isEmpty()){
						String curGene = splitLine[0];
						int lengthNewArray = splitLine.length -1;
						String[] geneInfo = new String[lengthNewArray]; 
						System.arraycopy(splitLine, 1, geneInfo, 0, lengthNewArray );
						
						// save gene with corresponding infos
						if (!curListIn.getGenes().containsKey(curGene)){
							curListIn.addGene(curGene, geneInfo);
						}
					}
				}
			}
			
			
			// save current file in map containing all resources
			resources.put(curFile, curListIn);
		}	
	
	}


	
	
	
	
	
	
	
	
	/////////////////
	// read in input gene list
	private void initQueryGenes(){

		// make log entry
		log.writeOutFile("\n#### Reading in input gene list");

		// init variables
		// init class to work with files
		HandleFiles file = new HandleFiles();

		// check if single query or list of queries is chosen
		if (options.getListOfQueries() != null && !options.getListOfQueries().isEmpty()){

			
			// open name of list of query file
			String listOfQuery = options.getListOfQueries();
			file.exist(listOfQuery);
			LinkedList<String> allLists = file.openFile(listOfQuery, true);
			
			// read in all query lists
			for (String curListPath : allLists){
			
				// check if file exists
				file.exist(curListPath);
				
				//// open single query file
				LinkedList<String> lines = file.openFile(curListPath, true);
				
				// split file and take first entry, for the case that information is stored in input list
				InputList curList = new InputList(curListPath);
				inputLists.add(curList);
				for (String line : lines){
					String[] splitString = line.split("\t");
					
					// check if input gene is already listed if so don't add it again.
					if (curList.getQueryGenes().contains(splitString[0])){
						log.writeWarning(splitString[0] + " is duplicated in input list. Only used once.");
					} else {
						curList.addGene(splitString[0]);

					}
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

			// split file and take first entry, for the case that information is stored in input list
			InputList curList = new InputList(queryGeneFile);
			inputLists.add(curList);
			for (String line : lines){
				String[] splitString = line.split("\t");
				curList.addGene(splitString[0]);
			}
		}
	}

	
	
	/////////////////
	//// initialize gene db
	public void readInGenes() {

		// make log entry
		log.writeOutFile("\n##Initializing gene database.");
		
		// gather variables
		String geneDB = getOptions().getDbGeneName();
		String geneTable = getOptions().getDbGeneTable();
		
		// connect to database
		InitDatabase db = new InitDatabase(geneDB, log, true);
		
		// check that needed table and column exists
		db.checkDatabases(geneTable, columnNamesGenes);

		// read in gene database
		genes = new ReadInGeneDB(this);

		
		
	}


	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////




	public ConfigFile getConfig() {
		return config;
	}

	public GetGeneSetOptions getOptions() {
		return options;
	}

	public HandleFiles getLog() {
		return log;
	}

	public Map<String, ResourceLists> getResources() {
		return resources;
	}

	public List<InputList> getInputLists() {
		return inputLists;
	}

	public ReadInGeneDB getGeneDbGenes() {
		return genes;
	}





}
