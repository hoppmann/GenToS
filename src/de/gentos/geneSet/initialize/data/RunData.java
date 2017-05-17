package de.gentos.geneSet.initialize.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import de.gentos.geneSet.initialize.InitializeGeneSetMain;

public class RunData {
	///////////////////////////
	//////// variables ////////
	///////////////////////////

	// general data
	private String curListName;	
	private Map<String, ResourceLists> resources;

	
	// lookup data
	private int numberEnrichedLists = 0;
	private LinkedList<String> enrichedResources;
	private double cumScore = 0;
	private int scoreHits = 0;
	private int lengthInput;
	private Map<String, GeneData> geneData; // map with key resource list and value geneData object

	// resampling data
	private Map<String, Double> empiricalPval; // Sorted Map containing gene <=> empirical pval connection
	private double finalThresh;
	
	
	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////


	public RunData(InitializeGeneSetMain init, int lengthInputList) {
		
		enrichedResources = new LinkedList<>();
		resources = new HashMap<>();
		geneData = new HashMap<>();
		
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
	
	// inital data
	
	public String getCurListName() {
		return curListName;
	}


	public void setCurListName(String curListName) {
		this.curListName = curListName;
	}
	
	
	
	
	// lookup data
	
	public LinkedList<String> getEnrichedResources() {
		return enrichedResources;
	}

	
	public void setEnrichedResources(LinkedList<String> enrichedResources) {
		this.enrichedResources = enrichedResources;
	}

	
	public int getNumberEnrichedResources() {
		return numberEnrichedLists;
	}

	
	public void setNumberEnrichedResources(int numberEnrichedRsources) {
		this.numberEnrichedLists = numberEnrichedRsources;
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

	public int getLengthInput() {
		return lengthInput;
	}

	public void setLengthInput(int lengthInput) {
		this.lengthInput = lengthInput;
	}


	public Map<String, GeneData> getGeneData() {
		return geneData;
	}

	public void setGeneData(Map<String, GeneData> geneData) {
		this.geneData = geneData;
	}

	
	
	
	
	
	// resampling data

	public Map<String, Double> getEmpiricalPval() {
		return empiricalPval;
	}

	public void setEmpiricalPval(Map<String, Double> empiricalPval) {
		this.empiricalPval = empiricalPval;
	}

	public double getFinalThresh() {
		return finalThresh;
	}

	public void setFinalThresh(double finalThresh) {
		this.finalThresh = finalThresh;
	}

	























	

	
	
	
	
	
	
	
}
