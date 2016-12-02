package de.gentos.general.misc;

import org.apache.commons.cli.Options;

public class OptionValue {

	//////////////////////
	//////// set variables
	private String shortcut;
	private String description;
	private boolean argumentRequired;


	////////////////////
	//////// Constructor



	public OptionValue(Options opts, String shortcut, boolean argReq, String description) {

		// retrieve values
		this.shortcut = shortcut;
		this.description = description;
		this.argumentRequired = argReq;

		// add options
		opts.addOption(getShortcut(), isArgumentRequired(), getDescription());
	}









	///////////////
	//////// Getter

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
