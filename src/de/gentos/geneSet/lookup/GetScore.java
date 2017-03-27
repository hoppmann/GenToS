package de.gentos.geneSet.lookup;

import java.util.List;
import java.util.Map;

import de.gentos.geneSet.initialize.data.GeneData;

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

	public void rankedList(List<String> list, Map<String, GeneData> geneData, String currentResourceList) {

		
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
		int rank = 1;
		for (String gene : list) {

			// check if gene key is available else initialize
			if (! geneData.containsKey(gene)) {
				geneData.put(gene, new GeneData(gene));
			}

			// get weight of current gene 
			double weightCurGene = (double) invRank / sumOfRank;
			invRank--;
			
			// save rank of gene in this list
			String position = Integer.toString(rank) + "/" + Integer.toString(list.size());
			geneData.get(gene).putEnrichedList(currentResourceList, position);
			rank++;
			
			// increment that gene is listed in another resource
			geneData.get(gene).incrementNumberAllFoundResources();
			
			// store weight in array
			geneData.get(gene).sumScore(weightCurGene);
		}

	}

	
	
	
	
	

	//////////////////////
	//////// calculate weight for unranked list
	//// weight of gene = 1 / length of list

	public void unranked(List<String> list, Map<String, GeneData> geneData, String currentResourceList){

		int lengthList = list.size();
		// for each gene get weight and save in hash
		for (String gene : list) {

			// check if gene key is available else initialize
			if (! geneData.containsKey(gene)) {
				geneData.put(gene, new GeneData(gene));
			}

			// get weight of current gene 
			double scoreCurGene = (double) 1 / lengthList;

			// save rank of gene in this list
			String position = "-/" + Integer.toString(list.size());
			geneData.get(gene).putEnrichedList(currentResourceList, position);;

			// increment that gene is listed in another resource
			geneData.get(gene).incrementNumberAllFoundResources();

			
			// store weight in array
			geneData.get(gene).sumScore(scoreCurGene);

		}
	}



	/////////////////////////////////
	////////getter / setter ////////
	/////////////////////////////////





}








