package de.gentos.geneSet.initialize.data;

import java.util.LinkedList;

public class QueryList {
	///////////////////////////
	//////// variables ////////
	///////////////////////////

	private String listName;
	private LinkedList<String> queryGenes;
	
	
	
	
	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	
	public QueryList(String listName) {

		this.listName = listName;
		
		queryGenes = new LinkedList<>();
	
	}
	
	
	
	
	/////////////////////////
	//////// methods ////////
	/////////////////////////

	
	public void addGene(String gene) {
		
		queryGenes.add(gene);
		
	}




	
	
	
	
	
	
	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
	
	
	public String getListName() {
		return listName;
	}

	public LinkedList<String> getQueryGenes() {
		return queryGenes;
	}
	
	
	
}
