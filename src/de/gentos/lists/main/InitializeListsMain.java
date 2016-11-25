package de.gentos.lists.main;

import java.io.IOException;

import org.apache.commons.cli.Options;

import de.gentos.general.files.HandleFiles;
import de.gentos.general.options.lists.GetListOptions;
import de.gentos.gwas.initialize.ConfigFile;

public class InitializeListsMain {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	private GetListOptions options;
	private ConfigFile config;
	private HandleFiles log;
	private String[] args;

	
	
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
		
		// init log file
		initLog();
		
	
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
	
	
	
	
	
}
