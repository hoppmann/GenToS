package de.gentos.geneSet.initialize.data;

import java.io.File;
import java.util.LinkedList;

public class InputList {
	///////////////////////////
	//////// variables ////////
	///////////////////////////

	private String listName;
	private LinkedList<String> inputqueryGenes;
	
	
	
	
	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	
	public InputList(String listName) {

		this.listName = listName;
		
		inputqueryGenes = new LinkedList<>();
	
	}
	
	
	
	
	/////////////////////////
	//////// methods ////////
	/////////////////////////

	
	public void addGene(String gene) {
		
		inputqueryGenes.add(gene);
		
	}




	
	
	
	
	
	
	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
	
	public String getListName() {
		return new File(listName).getName();
	}
	public String getListPath() {
		return listName;
	}

	public LinkedList<String> getQueryGenes() {
		return inputqueryGenes;
	}
	
	
	
}
