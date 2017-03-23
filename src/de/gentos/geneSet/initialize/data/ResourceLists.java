package de.gentos.geneSet.initialize.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ResourceLists {
	///////////////////////////
	//////// variables ////////
	///////////////////////////

	//////// data produced in init step
	private boolean sorted;
	private LinkedList<GeneData> header;
	private Map<String, String[]> genes; // line coresponing 
	
	
	//////// collection of data produced during enrichment
	private double enrichmentPval;
	private boolean enriched;
	
	
	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	
	public ResourceLists() {
		
		// initialize lists
		header = new LinkedList<>();
		sorted = false;
		genes = new HashMap<>();
		
		
		
	}




	
	
	
	
	/////////////////////////
	//////// methods ////////
	/////////////////////////

	// add line to header information
	public void addHeader(String[] splitLine) {
		
		header.add(new GeneData(splitLine));
	}

	
	// add genes to map
	public void addGene(String gene, String[] geneInfo ){
		genes.put(gene, geneInfo);
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

	public LinkedList<GeneData> getHeader() {
		return header;
	}

	public void setHeader(LinkedList<GeneData> header) {
		this.header = header;
	}
	
	public Map<String, String[]> getGenes() {
		return genes;
	}

	public void setGenes(Map<String, String[]> genes) {
		this.genes = genes;
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
