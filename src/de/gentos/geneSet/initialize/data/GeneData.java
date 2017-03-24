package de.gentos.geneSet.initialize.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class GeneData {
	///////////////////////////
	//////// variables ////////
	///////////////////////////

	// initial gene informations
	private String geneName;
	private LinkedList<String> line;
	
	
	
	// gene data collection during lookup
	private double cumScore = 0;
	private Map<String, String> enrichedResources;
	
	private int scoreHits = 0;
	private double empiricalPval;
	private int numberEnricheResources = 0;
	private int numberAllFoundResources = 0;


	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	// general constructor
	public GeneData(String[] splitLine) {

		// init Lists
		line = new LinkedList<>();
		enrichedResources = new HashMap<>();
		
		// if line is header just save in line list
		if (splitLine[0].startsWith("#")){
			for (String entry: splitLine){
				line.add(entry);
			}
		} else {

			// if line is gene line sort it to geneName (first column) and rest (information field)

			int counter = 0;

			for (String entry : splitLine) {

				if (counter == 0) {
					geneName = entry.toUpperCase();
					counter++;
				} else {

					line.add(entry);
				}
			}
		}
	}


	// constructor to initialize only name
	public GeneData(String geneName) {
		
		// init lists
		enrichedResources = new HashMap<>();
		// set gene name
		this.geneName = geneName.toUpperCase();
	
	}
	
	
	/////////////////////////
	//////// methods ////////
	/////////////////////////
	public void addInfo(String text) {
		line.add(text);
	}
	
	
	public void sumScore(double curScore) {
		cumScore = cumScore + curScore;
	}
	
	public void incrementScoreHits () {
		scoreHits++;
	}
	
	public void incrementNumberEnricheResources (){
		numberEnricheResources++;
	}
	
	public void incrementNumberAllFoundResources(){
		numberAllFoundResources++;
	}
	
	public void putEnrichedList(String listName, String position) {
		enrichedResources.put(listName, position);
	}


	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////

	public String getGeneName() {
		return geneName;
	}

	public LinkedList<String> getLine() {
		return line;
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


	public double getEmpiricalPVal() {
		return empiricalPval;
	}


	public void setEmpiricalPVal(double validationPVal) {
		this.empiricalPval = validationPVal;
	}

	public int getNumberAllFoundResources() {
		return numberAllFoundResources;
	}

	public void setNumberAllFoundResources(int numberAllFoundResources) {
		this.numberAllFoundResources = numberAllFoundResources;
	}


	public int getNumberEnricheResources() {
		return numberEnricheResources;
	}

	public void setNumberEnricheResources(int numberEnricheResources) {
		this.numberEnricheResources = numberEnricheResources;
	}


	public Map<String, String> getEnrichedResources() {
		return enrichedResources;
	}


	public void setEnrichedResources(Map<String, String> enrichedLists) {
		this.enrichedResources = enrichedLists;
	}





	

	

	
	
	
}
