package de.gentos.general.options.lists;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class GetListOptions {
	///////////////////////////
	//////// variables ////////
	///////////////////////////

	// general variables
	String[] args;
	SetListOptions setListOptions;
	String progPath;
	Options options;
	CommandLine cmd = null;

	
	
	/////////////////////////
	//////// option variables
	
	
	
	// main options
	
	private String listIn;
	private String outDir = "out";
	private String log = "logfile.txt";
	private String listDir = "geneLists";
	
	
	
	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public GetListOptions(String[] args) {
	
		
		this.args = args;
		
		setListOptions = new SetListOptions();
		this.options = setListOptions.getOptions();
		
		
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
			setListOptions.callHelp();
			System.exit(1);
		}
		
		

		/////////////////////////
		////// Check for not combinable options
		
		
		
		
		
		
		
		
		
		
		////////////////////////////////////////////////////
		//////// write options in variables, check variables

		/////////////////////
		//// general settings
		
		// get input gene list
		listIn = cmd.getOptionValue("inputList");
		if (!cmd.hasOption("inputList")){
			System.out.println("Option \"inputList\" is mandatory.");
			System.exit(1);
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
		if (cmd.hasOption("listDir")) {
			listDir = cmd.getOptionValue("listDir");
		}
		
		
	}





	
	
	
	
	
	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////

	
	
	//////// Stuff
	public String[] getArgs() {
		return args;
	}

	public SetListOptions getSetListOptions() {
		return setListOptions;
	}

	
	
	
	//////// main options
	
	public String getProgPath() {
		return progPath;
	}

	public CommandLine getCmd() {
		return cmd;
	}

	public String getListIn() {
		return listIn;
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


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	






}
