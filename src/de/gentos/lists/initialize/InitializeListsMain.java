package de.gentos.lists.initialize;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

import de.gentos.general.files.ConfigFile;
import de.gentos.general.files.HandleFiles;
import de.gentos.general.options.lists.GetListOptions;

public class InitializeListsMain {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	private GetListOptions options;
	private ConfigFile config;
	private HandleFiles log;
	private String[] args;
	private Map<String, GeneLists> resources;
	private LinkedList<String> queryGenes;



	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public InitializeListsMain(String[] args) {

		// retrieve variables
		this.args = args;

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
		
		// make log entry 
		
		log.writeOutFile("\n## Finished Initializing");
		
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
	// get command line options

	private void initOptions() {

		options = new GetListOptions(args);

	}


	// open log file
	private void initLog() {

		log = new HandleFiles();

		log.openWriter(options.getOutDir() + System.getProperty("file.separator") + options.getLog());
	}



	// read in resource lists
	private void initResources(){

		// make log entry
		log.writeOutFile("\n## Reading in resource gene lists!");

		ReadInResourceLists resourceList = new ReadInResourceLists(options, log);
		resources = resourceList.readInLists();

	}



	// read in query gene list
	private void initQueryGenes(){

		// make log entry
		log.writeOutFile("\n## Reading in query gene list");
		
		// get file path
		String queryGeneFile = options.getListIn();

		// init class to work with file
		HandleFiles file = new HandleFiles();

		// check if input list exists		
		file.exist(queryGeneFile);


		//// open file
		// open file and save query list
		queryGenes = file.openFile(queryGeneFile, false);


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

	public Map<String, GeneLists> getResources() {
		return resources;
	}

	public LinkedList<String> getQueryGenes() {
		return queryGenes;
	}

	




}
