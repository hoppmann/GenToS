package de.gentos.geneSet.initialize.options;

import org.apache.commons.cli.Options;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import de.gentos.general.misc.OptionValue;

public class SetGeneSetOptions {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	private Options options;
	Multimap<String, OptionValue> opts;

	
	
	//// names for grouping
	private String main = "main options";
	private String resample = "resampling";
	private String misc = "misc";
	private String lookup = "lookup";
	
	
	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	
	
	public SetGeneSetOptions() {
		
		makeOptions();
		
		
	}
	
	
	
	
	
	
	/////////////////////////
	//////// methods ////////
	/////////////////////////

	private void makeOptions() {
		
		options = new Options();
		opts = LinkedListMultimap.create();
		
		
		//////// main options
		opts.put(main, new OptionValue(options, "inputList", true, "List of genes to check for"));
		opts.put(main, new OptionValue(options, "outDir", true, "\tName of the directory for the output"));
		opts.put(main, new OptionValue(options, "resourceDir", true, "Directory where the resource lists are saved. (Default: \"geneLists\")"));
		opts.put(main, new OptionValue(options, "listOfQueries", true, "File containing path to all querylists."));
		
		//////// resampling
		opts.put(resample, new OptionValue(options, "iterations", true, "Number of iterations that should be used for resampling (Default: 1000)"));
		opts.put(resample, new OptionValue(options, "dbGene", true, "\tFile name of gene database to be used. Only needed if not internal databse desired. [tableGene option required]"));
		opts.put(resample, new OptionValue(options, "tableGene", true, "Name of gene table in dbGene (default = genes)"));
		opts.put(resample, new OptionValue(options, "seed", true, "\tSet seed for random generator."));
		opts.put(resample, new OptionValue(options, "minEnriched", true, "Minimum number of enriched resources to run resampling. (default = 1)"));
		opts.put(resample, new OptionValue(options, "alpha", true, "\tSets the alfa that should be used to calculate threshold for final list. (default = 0.05"));
		opts.put(resample, new OptionValue(options, "threshMethod", true, "Sets the method for the threshold calculation of the threshold for the final list. Possible choices [FDR/bonferroni]. (default = FDR)"));
		
		
		//////// misc
		opts.put(misc, new OptionValue(options, "infoFile", true, "Name of the infoFile"));
		opts.put(misc, new OptionValue(options, "help", false, "\tCalls this help."));
		opts.put(misc, new OptionValue(options, "log", true, "\tName of the logfile. (default = \"logfile.txt\")"));
		opts.put(misc, new OptionValue(options, "threads", true, "\tThe number of threads (input lists) that should be handled in parallel. NOTE: total number of CPU usage = threads * cpuPerThread"));
		opts.put(misc, new OptionValue(options, "cpuPerThread", true, "(Prefered option over \"threads\") Set how many CPUs should be available per data thread (inputlist). NOTE: total number of CPU usage = threads * cpuPerThread"));
		opts.put(misc, new OptionValue(options, "verbose", false, "\tAdd extra output for debugging"));
		
		//////// lookup
		opts.put(lookup, new OptionValue(options, "stringent", false, "If chosen make threshold correction by number of resource lists AND input lists."));
		
		
		
	}


	//display Help
	public void callHelp() {
		
		System.out.println("options");
		
		// for each group display help text
		for (String key : opts.keySet()) {

			// display group name
			System.out.println("\n\t" + key);
			
			// extract help text and display it
			for (OptionValue currentOption : opts.get(key)) {
				String shortcut = currentOption.getShortcut();
				String description = currentOption.getDescription();
				System.out.println("\t\t" + shortcut + "\t\t" + description);
			}
		}
	}





	
	
	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
	
		public Options getOptions() {
		return options;
	}
	

	
	
	
	
	
}
