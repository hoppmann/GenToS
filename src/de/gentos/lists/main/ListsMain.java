package de.gentos.lists.main;

import de.gentos.general.options.lists.GetListOptions;

public class ListsMain {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	GetListOptions options;
	
	
	
	
	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	
	
	
	/////////////////////////
	//////// methods ////////
	/////////////////////////

	
	public void runLists(String[] args) {
	
		// getting command line options

		InitializeListsMain init = new InitializeListsMain(args);
		options = init.getOptions();
		
		System.out.println("HALLO");
		
		System.out.println(options.getListDir());
		
		
		
	}
	
	
	
	
	
	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
}
