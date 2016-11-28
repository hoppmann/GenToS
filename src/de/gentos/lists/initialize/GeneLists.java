package de.gentos.lists.initialize;

import java.util.LinkedList;

public class GeneLists {
	///////////////////////////
	//////// variables ////////
	///////////////////////////

	Boolean sorted;
	LinkedList<ResourceData> header;
	LinkedList<ResourceData> geneLine;
	
	
	
	
	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	
	public GeneLists() {
		
		// initialize lists
		header = new LinkedList<>();
		geneLine = new LinkedList<>();
		sorted = false;
		
		
		
	}




	
	
	
	
	/////////////////////////
	//////// methods ////////
	/////////////////////////

	// add line to header information
	public void addHeader(String[] splitLine) {
		
		header.add(new ResourceData(splitLine));
	}

	
	// add line to gene informations
	public void addGeneLine(String[] splitLine) {
		
		geneLine.add(new ResourceData(splitLine));
		
	}
	
	
	
	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
	public Boolean getSorted() {
		return sorted;
	}

	public void setSorted(Boolean sorted) {
		this.sorted = sorted;
	}

	public LinkedList<ResourceData> getHeader() {
		return header;
	}

	public void setHeader(LinkedList<ResourceData> header) {
		this.header = header;
	}

	public LinkedList<ResourceData> getGeneLines() {
		return geneLine;
	}

	public void setGeneLines(LinkedList<ResourceData> geneLines) {
		this.geneLine = geneLines;
	}
	
	
	
	
	
}
