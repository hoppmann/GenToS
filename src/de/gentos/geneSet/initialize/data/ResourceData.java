package de.gentos.geneSet.initialize.data;

import java.util.LinkedList;

public class ResourceData {
	///////////////////////////
	//////// variables ////////
	///////////////////////////

	private String geneName;
	private LinkedList<String> line;
	private double cumScore = 0;
	private int scoreHits = 0;
	private double empiricalPval;


	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public ResourceData(String[] splitLine) {

		// init LinkedList
		line = new LinkedList<>();
		
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
	public ResourceData(String geneName) {
		
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




	
	
	
}
