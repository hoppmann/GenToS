package de.gentos.gwas.initialize.options;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import de.gentos.general.files.ConfigFile;
import de.gentos.general.files.HandleFiles;
import de.gentos.gwas.initialize.ExtracSpecFile;
import de.gentos.gwas.initialize.data.GwasDbInfo;

public class GetGwasOptions {

	// handed in variables and general variables
	private ConfigFile config;
	CommandLine cmd = null;
	private String[] args;
	private SetGwasOptions setGwasOptions;
	private Options options;

	// define variables to be set by options
	//mandatory
	private LinkedList<String> listOfQueries = new LinkedList<>();
	private String singleGene;
	
	private String spec;
	private String dbGene;
	private String tableGene;
	private Map<Integer, GwasDbInfo> gwasDbs;
	private boolean bedFile = false;

	// other run specific
	private int[] flank = new int[2];
	private String pop;
	private String popDir;
	private String indepDB;

	// threshold
	private double alpha = 0.05;
	private Double fixThresh;
	private String method;

	// column specifications
	private String colrsID;
	private String colChr;
	private String colPos;
	private String colPval;

	// validation options
	private int numberOfIterations = 2000;
	private boolean randomRepeats = false;
	private boolean binomial = false;
	private long seed;
	private boolean getProp = false;
	private String referenceFile;
	private String randListFile;
	
	// general settings
	private String log;
	private String dir;
	private String csvDir;

	// plotting
	private String graphSuffix;
	private String scaling;
	private String graphTitel;
	

	// path of program 
	private String progPath;

	
	
	
	
	
	
	
	////////////////////////////////
	/////// Constructor to init args

	public GetGwasOptions(String[] args, ConfigFile conifg) {
		// get arguments
		this.args = args;
		this.config = conifg;

		// run set options
		setGwasOptions = new SetGwasOptions();
		this.options = setGwasOptions.getOptions();
		
		// extract path of the program
		progPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();

		// run getOptions
		getOptions();



	}

	////////////////
	//////// methods

	private void getOptions() {
		// print out current job
		System.out.println("Getting command line options.");

		// parse command line options
		CommandLineParser cmdParser = new PosixParser();

		try {
			cmd = cmdParser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getLocalizedMessage());
			setGwasOptions.callHelp();
			System.exit(1);
		}

		// check if help or getSpec requested

		if (cmd.hasOption("help")) {
			new SetGwasOptions().callHelp();
			System.exit(0);
		}


		if (cmd.hasOption("getSpec")) {
			new PrintSpecifications();

		}

		////// Check for not combinable options
		// dbGene or dbSNP not combinable with specFile
		if ((cmd.hasOption("dbGene") || cmd.hasOption("dbSNP")) && cmd.hasOption("specFile")) {

			System.out.println("#### ERROR:\nOption specFile not combinable with dbGene and dbSNP!");
			System.exit(1);

		}  else if (!cmd.hasOption("specFile") && (!cmd.hasOption("dbSNP") || !cmd.hasOption("tableSNP") ) ) {

			System.out.println("#### ERROR:\nEither \"specFile\" or \"dbSNP\" and \"tableSNP\" is required.");
			System.exit(1);
		}


		/////////////////////////////////////////////////////////////////////////
		//////// write options in variables, check variables and setting defaults

		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/////////////
		//// general settings
		
		//define name of log file
		log = cmd.getOptionValue("log");
		if (log == null || "".equals(log.trim())){
			log = "logfile.txt";
		}

		//get name of output directory
		dir = cmd.getOptionValue("outDir");
		if (dir == null || "".equals(dir.trim())) {
			dir = "out";
		}

		if (cmd.hasOption("csvDir")) {
			csvDir = cmd.getOptionValue("csvDir");	
		} else {
			csvDir = "csv";
		}

		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		

		////////////////
		//////// mandatory options

		// check if bed file option is chosen
		if (cmd.hasOption("bedFile")){
			bedFile = true;
		}

		
		
		
		
		// check for singularity of options gene name, gene list and  listCollection
		// check if option chosen, if so increment check variable and test if more then one is chosen
		
		int check = 0;
		
		if (cmd.hasOption("gene")) {
			// save single gene if gene is chosen
			singleGene = cmd.getOptionValue("gene");
			
			check++;
			
			// check if bedFile option if chosen since if so make statement that not combinable
			if (cmd.hasOption("bedFile")) {
				System.out.println("\n#### WARNING: Option \"gene\" and \"bedFile\" are not combineable. Option \"bedFile\" will be ignored.\n");
			}
		}
		
		
		
		//// get list of genes
		if (cmd.hasOption("list")) {
			listOfQueries.add(cmd.getOptionValue("list"));
			check++;
		}
		
		if (cmd.hasOption("listCollection")) {
			// check if list Collection exist then extract lists
			String listCollection =cmd.getOptionValue("listCollection");
			new HandleFiles().exist(listCollection);
			listOfQueries.addAll(new HandleFiles().openFile(listCollection, true));
			check++;
		}

		
		
		
		// if more than one option chosen abort and pass out error message
		if (check > 1){
			System.out.println("ERROR:\nOptions \"gene\", \"list\" and \"listCollecion\" are not combinable.");
			System.exit(1);
		} else if (check < 1) {
			System.out.println("ERROR:\nEither option \"gene\", \"list\" or \"listCollection\" mandatory!");
			System.exit(1);

			// if list or list collection option chosen read in genes for each list
		} else if (!listOfQueries.isEmpty()) {
			for (String file : listOfQueries){

				// check if file exists
				new HandleFiles().exist(file);
			}
		}

		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		////////////////////
		//////// dbSNP and dbGene

		// get spec file if option chosen else get single dbSNP and dbTable option
		if (cmd.hasOption("specFile")){

			// get spec file values and check that option actual has value
			spec = cmd.getOptionValue("specFile");
			if (spec == null || "".equals(spec.trim())) {
			} else {

				// get values for dbGene tableGene dbSNP and tableSNP from spec file
				ExtracSpecFile specFile = new ExtracSpecFile(spec);
				dbGene = specFile.getDbGene();
				tableGene = specFile.getTableGene();
				gwasDbs = specFile.getDbSNP();

			}

		} else {
			/////// if NOT spec
			// get single dbGene and table gene or set default if not specFile option chosen 
			dbGene = cmd.getOptionValue("dbGene");

			// get single gene table or set default
			tableGene = cmd.getOptionValue("tableGene");

			// get single dbSNP and single tableSNP
			gwasDbs =  new HashMap<>();
			String dbPath = cmd.getOptionValue("dbSNP");
			String tableName = cmd.getOptionValue("tableSNP");
			GwasDbInfo dbInfo = new GwasDbInfo(dbPath, tableName);
			gwasDbs.put(0, dbInfo);
		}



		// if tableGene or dbGenen empty extract from config
		if (dbGene == null || "".equals(dbGene.trim())) {

			dbGene = progPath + config.getDbGene();
		}


		// get single gene table or set default
		tableGene = cmd.getOptionValue("tableGene");
		if (tableGene == null || "".equals(tableGene.trim())) {
			tableGene = config.getTableGene();
		}


		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		

		/////////////
		/////// other run specific

		//// flanking and up/downstream
		if (cmd.hasOption("flanking")){
			String flanking = cmd.getOptionValue("flanking");

			// check that only numbers where used
			if (!flanking.matches("[0-9]+")) {
				System.out.println("Flanking  must contain only positive and whole numbers!");
				System.exit(1);
			}
			// convert string to int
			flank[0] = Integer.valueOf(flanking);
			flank[1] = Integer.valueOf(flanking);
		} else {
			flank[0] = 10000;
			flank[1] = 10000;
		}

		if (cmd.hasOption("downstream")) {

			// check that downstream has only numbers
			String downstream = cmd.getOptionValue("downstream");
			if (!downstream.matches("[0-9]+")){
				System.out.println("Downstream must contain only positive and whole numbers!");
				System.exit(1);
			}

			// overwrite flanking
			flank[1] = Integer.valueOf(downstream);
		}


		if (cmd.hasOption("upstream")) {

			// check that upstream only has numbers
			String upstream = cmd.getOptionValue("upstream");
			if (!upstream.matches("[0-9]+")) {
				System.out.println("Upstream must contain only positive and whole numbers!");
				System.exit(1);
			}
			// overwrite flanking 
			flank[0] = Integer.valueOf(upstream);
		}




		
		
		
		
		
		
		
		
		
		
		
		
		

		////////////////
		//////// threshold options

		
		//// fixThresh
		if (cmd.hasOption("fixThresh")) {
			String fix = cmd.getOptionValue("fixThresh");
			try
			{
				fixThresh = Double.parseDouble(fix);
			}
			catch(NumberFormatException e)
			{
				System.out.println("######## ERROR: value for fixThresh needs to be of type double [e.g. 0.001, 1e-2, 1E-02]");
				System.exit(1);
			}
		}

		// check for singularity of chosen thresh calculation
		check = 0;
		
		// check if fix thresh is chosen
		if (cmd.hasOption("fixThresh")) {
			method = "fixThresh";
			// check if also plenty genes is chosen if so make warning
			if (cmd.hasOption("plenty")){
				System.out.println("#ERROR: fixThresh is not combinable with \"plenty\"");
				System.exit(1);
			}

			check++;
		}
		
		// check if bonferroni is chosen and according sub mode
		if (cmd.hasOption("bonferroni") || cmd.hasOption("plenty")) {
			method = "bonferroni";
			check++;
		}
		
		// check if FDR is wrongly combined
		if (cmd.hasOption("FDR")) {
			method = "FDR";
			// check if also plenty genes is chosen if so make warning
			if (cmd.hasOption("plenty")){
				System.out.println("#ERROR: FDR is not combinable with \"plenty\"");
				System.exit(1);
			}
			check++;
		}

		if (cmd.hasOption("maxEnrichment")) {
			method = "maxEnrichment";
			check++;
		}
		
		
		// check that not more then one threshold options chosen.
		if (check > 1) {
			System.out.println("######## ERROR: methods for threshold calculation are not combinable (bonferroni, FDR, fixThres)!");
			System.exit(1);
		}


		if (check == 0) {
			method = "bonferroni";
		}

		// check that alpha is double and set default if needed
		if (cmd.hasOption("alpha")) {
			try {
				alpha = Double.valueOf(cmd.getOptionValue("alpha"));
			} catch (NumberFormatException e) {
				System.out.println("\"alpha\" must be number of type double.");
				System.exit(1);
			}
		} 

		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		//////////////
		/////// population options
		
		if (cmd.hasOption("pop")) {
			// retrieve option value
			pop = cmd.getOptionValue("pop");
		} else {
			pop = "EUR";
		}

		if (cmd.hasOption("popDir")) {
			// retrieve option value
			popDir = cmd.getOptionValue("popDir");
		} else {
			popDir = progPath + config.getIndepDir();
		}

		// combine to indepDB and check for existence
		indepDB = popDir + "/" + pop + ".db";

		if (! new File(indepDB).isFile()) {
			System.out.println("No database for independent SNPs found under " + indepDB);
			System.exit(1);
		}


		
		
		
		
		
		
		
		
		
		
		
		
		
		

		////////////////
		//////// validation options

		// validation via random draw iteration
		if (cmd.hasOption("iterations")) {
			// check if randomRepeat is also chosen else give out warning
			if (!cmd.hasOption("enrichment")) {
				System.out.println("## Warning \"numberIteration\" only valid combined with \"enrichment\" ignored in further run" );
			}

			try {
				numberOfIterations = Integer.valueOf(cmd.getOptionValue("iterations"));
			} catch (NumberFormatException e) {
				System.out.println("## ERROR: \"iterations\" no valid integer.");
				System.exit(1);
			}
			if (numberOfIterations < 1){
				System.out.println("## ERROR: \"iterations\" has to be a positve integer > 0");
				System.exit(1);
			}

		}		
		
		
		// check if no validation method chosen, then take random repeats
		if (cmd.hasOption("enrichment")) {
			if (!cmd.hasOption("randomRepeat") && !cmd.hasOption("binomial")) {
				binomial = true;
			} else if (cmd.hasOption("randomRepeat") && cmd.hasOption("binomial")) {
				System.out.println("## ERROR: \"randomRepeats and \"binomial\" not combinable.");
				System.exit(1);
			}else if (cmd.hasOption("randomRepeat")) {
				randomRepeats = true;
			} else if (cmd.hasOption("binomial")) {
				binomial = true;
			}
		}
		
		
		// check if binomial and BED-file option is chosen, if so abort with ERROR
		if (this.isBedFile() && this.isBinomial()) {
			System.out.println("## ERROR: Binomial validation is not supported for BED files.");
			System.exit(1);
		}

		
		
		
		// check for seed option and save as long
		if (cmd.hasOption("seed")) {
			seed = Long.valueOf(cmd.getOptionValue("seed"));
		}

		
		
		// get prob Hit
		if (cmd.hasOption("getProbHit")) {
			getProp = true;
			
		}
		
		
		
		// get reference file
		if (cmd.hasOption("reference")) {
			referenceFile = cmd.getOptionValue("reference");
			new HandleFiles().exist(referenceFile);
		}

		
		// get randList
		if (cmd.hasOption("randList")) {
			randListFile = cmd.getOptionValue("randList");
			new HandleFiles().exist(randListFile);
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		////////////
		//////// plotting 
		
		if (!cmd.hasOption("format")) {
			graphSuffix = "png";
		} else {
			graphSuffix = cmd.getOptionValue("format");
		}
		
		
		// check that scaling is on 
		if (cmd.hasOption("scaling")){
			try {
				Double.parseDouble(cmd.getOptionValue("scaling"));
				scaling = cmd.getOptionValue("scaling");
				if (Double.valueOf(scaling) <= 0) {
					System.out.println("\"scaling\" needs to be a non negative number between 0 and inf.");
					System.exit(1);
				}
			} catch (NumberFormatException e) {
				System.out.println("\"scaling\" needs to be a non negative number between 0 and inf.");
				System.exit(1);
			}
		} else {
			scaling = "1.4";
		}
		
		// check if title == NONE
		if (cmd.hasOption("title")) {
			if (cmd.getOptionValue("title").equals("NONE")){
				graphTitel = "";
			} else {
				graphTitel = cmd.getOptionValue("title");
			}
		}

		
		
		
		
		
		

		//////////////
		//////// column specifications

		if (!cmd.hasOption("colRsID")) {
			colrsID = "rsid";
		} else {
			colrsID = cmd.getOptionValue("colRsID");
		}

		if (!cmd.hasOption("colChr")) {
			colChr = "chr";
		} else {
			colChr = cmd.getOptionValue("colChr");
		}

		if (!cmd.hasOption("colPos")) {
			colPos = "pos";
		} else {
			colPos = cmd.getOptionValue("colPos");
		}

		if (!cmd.hasOption("colpVal")) {
			colPval = "pval";
		} else {
			colPval = cmd.getOptionValue("colpVal");
		}



	}

	

	
	
	
	
	//////////////////////////////////////
	//////// getters for option parameters

	
	
	public ConfigFile getConfig() {
		return config;
	}

	public CommandLine getCmd() {
		return cmd;
	}

	public String[] getArgs() {
		return args;
	}

	public SetGwasOptions getSetGwasOptions() {
		return setGwasOptions;
	}

	public LinkedList<String> getListOfQueries() {
		return listOfQueries;
	}

	public String getSingleGene() {
		return singleGene;
	}

	public String getSpec() {
		return spec;
	}

	public String getDbGene() {
		return dbGene;
	}

	public String getTableGene() {
		return tableGene;
	}

	public Map<Integer, GwasDbInfo> getGwasDbs() {
		return gwasDbs;
	}

	public boolean isBedFile() {
		return bedFile;
	}

	public int[] getFlank() {
		return flank;
	}

	public String getPop() {
		return pop;
	}

	public String getPopDir() {
		return popDir;
	}

	public String getIndepDB() {
		return indepDB;
	}

	public double getAlpha() {
		return alpha;
	}

	public Double getFixThresh() {
		return fixThresh;
	}

	public String getMethod() {
		return method;
	}

	public String getColrsID() {
		return colrsID;
	}

	public String getColChr() {
		return colChr;
	}

	public String getColPos() {
		return colPos;
	}

	public String getColPval() {
		return colPval;
	}

	public int getNumberOfIterations() {
		return numberOfIterations;
	}

	public boolean isRandomRepeats() {
		return randomRepeats;
	}

	public boolean isBinomial() {
		return binomial;
	}

	public long getSeed() {
		return seed;
	}

	public boolean isGetProp() {
		return getProp;
	}

	public String getLog() {
		return log;
	}

	public String getDir() {
		return dir;
	}

	public String getCsvDir() {
		return csvDir;
	}

	public String getGraphSuffix() {
		return graphSuffix;
	}

	public String getScaling() {
		return scaling;
	}

	public String getGraphTitel() {
		return graphTitel;
	}

	public String getProgPath() {
		return progPath;
	}

	public String getReference() {
		return referenceFile;
	}

	public String getReferenceFile() {
		return referenceFile;
	}

	public String getRandListFile() {
		return randListFile;
	}
	
	


}
