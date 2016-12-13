package de.gentos.lists.lookup;

import java.util.List;
import java.util.Map;

import de.gentos.lists.initialize.data.ResourceData;

public class GetScore {
	///////////////////////////
	//////// variables ////////
	///////////////////////////






	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////






	/////////////////////////
	//////// methods ////////
	/////////////////////////



	////////////////////////
	//////// calculate weight for ranked list
	//// weight of gene = rank of gene / sum of all ranks in list

	public void rankedList(List<String> list, Map<String, ResourceData> allScores) {

		// get sum of ranks
		int lengthList = list.size();
		int sumOfRank = 0;
		int curRank = 1;
		for (int counter = 0; counter < lengthList; counter++){
			sumOfRank = sumOfRank + curRank;  
			curRank++;
		}


		// for each gene get weight and save in hash
		int invRank = list.size();
		for (String gene : list) {

			// check if gene key is available else initialize
			if (! allScores.containsKey(gene)) {
				allScores.put(gene, new ResourceData(gene));
			}

			// get weight of current gene 
			double weightCurGene = (double) invRank / sumOfRank;
			invRank--;

			// store weight in array
			allScores.get(gene).sumScore(weightCurGene);
		}

	}


	//////////////////////
	//////// calculate weight for unranked list
	//// weight of gene = 1 / length of list

	public void unranked(List<String> list, Map<String, ResourceData> allScores){

		int lengthList = list.size();
		// for each gene get weight and save in hash
		for (String gene : list) {

			// check if gene key is available else initialize
			if (! allScores.containsKey(gene)) {
				allScores.put(gene, new ResourceData(gene));
			}

			// get weight of current gene 
			double scoreCurGene = (double) 1 / lengthList;


			// store weight in array
			allScores.get(gene).sumScore(scoreCurGene);

		}
	}



	/////////////////////////////////
	////////getter / setter ////////
	/////////////////////////////////





}








