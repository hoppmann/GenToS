package de.gentos.geneSet.initialize.options;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import de.gentos.general.files.ConfigFile;

public class GetListOptions {
	///////////////////////////
	//////// variables ////////
	///////////////////////////

	// general variables
	String[] args;
	SetGeneSetOptions setGeneSetOptions;
	String progPath;
	Options options;
	CommandLine cmd = null;

	
	
	/////////////////////////
	//////// option variables
	
	// general
	ConfigFile config;
	
	// main options
	
	private String query;
	private String outDir = "out";
	private String log = "logfile.txt";
	private String listDir = "geneLists";
	private String listOfQueries;
	
	// resampling
	private int iteration = 1000;
	private String dbGeneName;
	private String dbGeneTable;
	private long seed = -1;
	
	
	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public GetListOptions(String[] args, ConfigFile config) {
	
		
		this.args = args;
		this.config = config;
		
		
		// set options
		setGeneSetOptions = new SetGeneSetOptions();
		this.options = setGeneSetOptions.getOptions();
		
		
		// extract path of the program
		progPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();

		// get options
		getOptions();
	}
	
	
	
	
	
	
	/////////////////////////
	//////// methods ////////
	/////////////////////////

	
	private void getOptions() {
		
		// print out current job
		System.out.println("Getting command line options.");

		
		///////////////////////
		//////// parse command line options or print help
		
		CommandLineParser cmdParser = new PosixParser();
		try {
			cmd = cmdParser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getLocalizedMessage());
			setGeneSetOptions.callHelp();
			System.exit(1);
		}
		
		

		/////////////////////////
		////// Check for not combinable options
		
		
		// either query or list of queries has and can be chosen 
		if (cmd.hasOption("inputList") && cmd.hasOption("listOfQueries")){
			System.out.println("\nERROR: Option \"inputList\" and \"listOfQueries\" may not be used together.\n");
			System.exit(3);
		} else if (!cmd.hasOption("inputList") && !cmd.hasOption("listOfQueries")){
			System.out.println("\nERROR: Either option \"inputList\" OR \"listOfQueries\" is mandatory.\n");
			System.exit(1);

		}			
		
		
		
		
		
		////////////////////////////////////////////////////
		//////// write options in variables, check variables

		/////////////////////
		//// general settings
		
		// get input gene list
		if (cmd.hasOption("inputList")){
			query = cmd.getOptionValue("inputList");
		}
		
		// output directory
		if (cmd.hasOption("outDir")){
			outDir = cmd.getOptionValue("outDir");
		}
		
		// get name of logfile
		if (cmd.hasOption("log")) {
			log = cmd.getOptionValue("log");
		}
		
		// get directory of resource gene lists
		if (cmd.hasOption("resourceDir")) {
			listDir = cmd.getOptionValue("resourceDir");
		}
		
		if (cmd.hasOption("listOfQueries")){
			listOfQueries = cmd.getOptionValue("listOfQueries");
		}
		
		
		
		
		///////////////////////
		//// Resampling options
		
		// get number of iterations
		if (cmd.hasOption("iterations")) {
			iteration = Integer.parseInt(cmd.getOptionValue("iterations"));
		}
		
		// get name of gene database
		if (cmd.hasOption("dbGene")){
			dbGeneName = cmd.getOptionValue("dbGene");
		} else {
			dbGeneName = config.getDbGene();
		}

		// get table of gene database
		if (cmd.hasOption("tableGene")){
			dbGeneTable = cmd.getOptionValue("tableGene");
		} else {
			dbGeneTable = config.getTableGene();
		}
		
		// get seed
		if (cmd.hasOption("seed")){
			seed = Long.parseLong(cmd.getOptionValue("seed"));
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	}





	
	
	
	
	
	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////

	
	
	//////// Stuff
	public String[] getArgs() {
		return args;
	}

	public SetGeneSetOptions getSetListOptions() {
		return setGeneSetOptions;
	}

	
	
	
	//////// main options
	
	public String getProgPath() {
		return progPath;
	}

	public CommandLine getCmd() {
		return cmd;
	}

	public String getQuery() {
		return query;
	}

	public String getOutDir() {
		return outDir;
	}

	public String getLog() {
		return log;
	}

	public String getListDir() {
		return listDir;
	}

	public int getIteration() {
		return iteration;
	}

	public ConfigFile getConfig() {
		return config;
	}

	public String getDbGeneName() {
		return dbGeneName;
	}

	public String getDbGeneTable() {
		return dbGeneTable;
	}

	public long getSeed() {
		return seed;
	}

	public String getListOfQueries() {
		return listOfQueries;
	}
	
	

	
	
	
	 
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


}