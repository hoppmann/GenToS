package de.gentos.general.options.lists;

import org.apache.commons.cli.Options;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import de.gentos.general.options.OptionValue;

public class SetListOptions {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	private Options options;
	Multimap<String, OptionValue> opts;

	
	
	//// names for grouping
	String main = "main options";
	
	
	
	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	
	
	public SetListOptions() {
		
		makeOptions();
		
		
	}
	
	
	
	
	
	
	/////////////////////////
	//////// methods ////////
	/////////////////////////

	private void makeOptions() {
		
		options = new Options();
		opts = LinkedListMultimap.create();
		
		
		//////// Main options
		opts.put(main, new OptionValue(options, "inputList", true, "List of genes to check for"));
		opts.put(main, new OptionValue(options, "outDir", true, "Name of the directory for the output"));
		opts.put(main, new OptionValue(options, "log", true, "Name of the logfile. (default = \"logfile.txt\""));
		opts.put(main, new OptionValue(options, "listDir", true, "Directory where the resource lists are saved. (default \"geneLists\")"));
		
		
		
		
		
		
		
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
