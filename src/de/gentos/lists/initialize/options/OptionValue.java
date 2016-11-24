package de.gentos.lists.initialize.options;

import org.apache.commons.cli.Options;

public class OptionValue {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	private String shortcut;
	private String description;
	private boolean argumentRequired;

	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	
	public OptionValue(Options ops, String shortcut, boolean argumentRequired, String description) {
		
		// retrieve values
		this.shortcut = shortcut;
		this.argumentRequired = argumentRequired;
		this.description = description;
		
		// add option
		ops.addOption(getShortcut(), isArgumentRequired(), getDescription());
		
	}

		
	
	/////////////////////////
	//////// methods ////////
	/////////////////////////

	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
	
	
	public String getShortcut() {
		return shortcut;
	}

	public String getDescription() {
		return description;
	}

	public boolean isArgumentRequired() {
		return argumentRequired;
	}

	
	
}
