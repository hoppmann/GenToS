package de.gentos.lists.initialize.data;

import java.util.LinkedList;

public class ResourceData {
	///////////////////////////
	//////// variables ////////
	///////////////////////////

	String geneName;
	LinkedList<String> line;
	Boolean sorted;
	double cumScore = 0;
	int scoreHits = 0;


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
					geneName = entry;
					counter++;
				} else {

					line.add(entry);
				}
			}
		}
	}


	// constructor to initialize only name
	public ResourceData(String geneName) {
		
		this.geneName = geneName;
	
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


	public void setCumScore(double cumScore) {
		this.cumScore = cumScore;
	}


	public int getScoreHits() {
		return scoreHits;
	}


	public void setScoreHits(int scoreHits) {
		this.scoreHits = scoreHits;
	}


	
	
	
}
