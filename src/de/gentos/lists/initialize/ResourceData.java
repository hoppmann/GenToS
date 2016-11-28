package de.gentos.lists.initialize;

import java.util.LinkedList;

public class ResourceData {
	///////////////////////////
	//////// variables ////////
	///////////////////////////

	String geneName;
	LinkedList<String> line;
	Boolean sorted;


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


	/////////////////////////
	//////// methods ////////
	/////////////////////////
	public void addInfo(String text) {
		line.add(text);
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


}
