package de.gentos.lists.initialize.data;

import java.util.LinkedList;
import java.util.List;

public class ResourceLists {
	///////////////////////////
	//////// variables ////////
	///////////////////////////

	private boolean sorted;
	private LinkedList<ResourceData> header;
	private LinkedList<ResourceData> geneLine;
	private List<String> geneList;	
	private double enrichmentPval;
	private boolean enriched;
	
	
	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	
	public ResourceLists() {
		
		// initialize lists
		header = new LinkedList<>();
		geneLine = new LinkedList<>();
		sorted = false;
		geneList = new LinkedList<String>();
		
		
		
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
	
	// add gene name in array
	public void addGenes(String gene) {
		
		geneList.add(gene);
		
	}
	
	
	
	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
	
	
	
	public boolean isSorted() {
		return sorted;
	}

	public void setSorted(boolean sorted) {
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

	public LinkedList<ResourceData> getGeneLine() {
		return geneLine;
	}

	public List<String> getGeneList() {
		return geneList;
	}

	public double getEnrichmentPval() {
		return enrichmentPval;
	}

	public void setEnrichmentPval(double enrichmentPval) {
		this.enrichmentPval = enrichmentPval;
	}

	public boolean isEnriched() {
		return enriched;
	}

	public void setEnriched(boolean enriched) {
		this.enriched = enriched;
	}
	
	
	
	
	
}
