package de.gentos.lists.lookup;

import java.util.List;
import java.util.Map;

public class GetWeights {
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

	public void rankedList(List<String> list, Map<String, Double> allWeights) {

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
			if (! allWeights.containsKey(gene)) {
				allWeights.put(gene, (double) 0);
			}

			// get weight of current gene 
			double weightCurGene = (double) invRank / sumOfRank;
			invRank--;

			// store weight in array
			double newWeight = allWeights.get(gene) + weightCurGene;
			allWeights.put(gene, newWeight);
		}

	}


	//////////////////////
	//////// calculate weight for unranked list
	//// weight of gene = 1 / length of list

	public void unranked(List<String> list, Map<String, Double> allWeights){

		int lengthList = list.size();
		// for each gene get weight and save in hash
		for (String gene : list) {

			// check if gene key is available else initialize
			if (! allWeights.containsKey(gene)) {
				allWeights.put(gene, (double) 0);
			}

			// get weight of current gene 
			double weightCurGene = (double) 1 / lengthList;


			// store weight in array
			double newWeight = allWeights.get(gene) + weightCurGene;
			allWeights.put(gene, newWeight);

		}
	}



	/////////////////////////////////
	////////getter / setter ////////
	/////////////////////////////////





}








