package de.gentos.geneSet.initialize.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import de.gentos.geneSet.initialize.InitializeGeneSetMain;

public class RunData {
	///////////////////////////
	//////// variables ////////
	///////////////////////////

	private int numberEnrichedLists = 0;
	private LinkedList<String> enrichedLists;
	private double cumScore = 0;
	private int scoreHits = 0;
	private double empiricalPval;
	private Map<String, ResourceLists> resources;
	private int lengthInput; 


	
	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////


	public RunData(InitializeGeneSetMain init, int lengthInputList) {
		
		enrichedLists = new LinkedList<>();
		resources = new HashMap<>();
		
		// init Resources map
		for (String curResource : init.getResources().keySet()){
			resources.put(curResource, new ResourceLists());
			resources.get(curResource).setSorted(init.getResources().get(curResource).isSorted());
		}
		
		// add length of input list
		this.lengthInput = lengthInputList;
		
		
		
	}
	
	
	
	
	
	
	
	/////////////////////////
	//////// methods ////////
	/////////////////////////

	public void incrementEnrichment() {
		
		numberEnrichedLists++;
		
	}


	
	
	
	
	
	
	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
	
	public LinkedList<String> getEnrichedLists() {
		return enrichedLists;
	}

	
	public void setEnrichedLists(LinkedList<String> enrichedLists) {
		this.enrichedLists = enrichedLists;
	}

	
	public int getNumberEnrichedLists() {
		return numberEnrichedLists;
	}

	
	public void setNumberEnrichedLists(int numberEnrichedLists) {
		this.numberEnrichedLists = numberEnrichedLists;
	}
	
	
	public double getCumScore() {
		return cumScore;
	}
	
	
	public double getLogCumScore() {
		return -1 * Math.log10(cumScore);
	}
	
	
	public void setCumScore(double cumScore) {
		this.cumScore = cumScore;
	}


	public int getScoreHits() {
		return scoreHits;
	}


	public void setScoreHits(int scoreHits) {
		this.scoreHits = scoreHits;
	}


	public Map<String, ResourceLists> getResources() {
		return resources;
	}

	public void setResources(Map<String, ResourceLists> resources) {
		this.resources = resources;
	}

	public double getEmpiricalPval() {
		return empiricalPval;
	}

	public void setEmpiricalPval(double empiricalPval) {
		this.empiricalPval = empiricalPval;
	}

	public int getLengthInput() {
		return lengthInput;
	}

	public void setLengthInput(int lengthInput) {
		this.lengthInput = lengthInput;
	}

	
	
	
	
	
}
