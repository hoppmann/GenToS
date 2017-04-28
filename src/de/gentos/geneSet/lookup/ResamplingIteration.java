package de.gentos.geneSet.lookup;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.gentos.geneSet.initialize.data.GeneData;
import de.gentos.geneSet.initialize.data.ResourceLists;

public class ResamplingIteration implements Runnable{
	///////////////////////////
	//////// variables ////////
	///////////////////////////

	private LinkedList<String> curRandQuery;
	private Map<String, ResourceLists> resources;
	private double threshold;
	private Enrichment enrichment;
	private int totalGenes;
	private Map<String, GeneData> originalScores;

	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public ResamplingIteration(LinkedList<String> curRandQuery,	Map<String, ResourceLists> resources, Enrichment enrichment, int totalGenes, Map<String, GeneData> originalScores, double threshold) {
		
		this.curRandQuery = curRandQuery;
		this.resources = resources;
		this.enrichment = enrichment;
		this.totalGenes = totalGenes;
		this.originalScores = originalScores;
		this.threshold = threshold;
	}
	
	/////////////////////////
	//////// methods ////////
	/////////////////////////

	@Override
	public void run() {


		// init variable to gather weights over all lists
		Map<String, GeneData> allRandScores = new LinkedHashMap<>();


		// for each resource list get enrichment p-val
		for ( String curResource : resources.keySet()){


			// extract the number of query genes found in resource list
			int hits = enrichment.getHits(curRandQuery, resources.get(curResource));

			// extract the length of the query gene list 
			int lengthList = curRandQuery.size();

			// get enrichment Pval for current list
			double enrichmentPval = enrichment.getEnrichment(hits, totalGenes, lengthList);


			// check if list has enrichment pVal < threshold
			// if it is enriched calculate weight
			if (enrichmentPval <= threshold){

				// check if list is sorted or unsorted
				if (resources.get(curResource).isSorted()){

					// calculate weights for current resource list in sorted case
					List<String> resourceGeneList = new ArrayList<>(resources.get(curResource).getGenes().keySet());	
					new GetScore().rankedList(resourceGeneList, allRandScores, curResource);

				} else if (!resources.get(curResource).isSorted()) {

					// calculate weights for current resource list in unsorted case
					List<String> resourceGeneList = new ArrayList<>(resources.get(curResource).getGenes().keySet());	
					new GetScore().unranked(resourceGeneList, allRandScores, curResource);

				}
			}
		}

		


		// for each gene found in original list check if it was found in random sampled list
		// if so check if the weight is greater or equal. Then save to calculate pVal  

		for (String curGene : originalScores.keySet()){
			if (allRandScores.containsKey(curGene)) {

				if (allRandScores.get(curGene).getCumScore() >= originalScores.get(curGene).getCumScore()) {

					// save number of hits in object from original list
					originalScores.get(curGene).incrementScoreHits();
				}
			}
		}
	}
	
	
	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
}
