package de.gentos.lists.lookup;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import de.gentos.general.files.HandleFiles;
import de.gentos.general.files.ReadInGenes;
import de.gentos.gwas.validation.RandomDraw;
import de.gentos.lists.initialize.InitializeListsMain;
import de.gentos.lists.initialize.data.QueryList;
import de.gentos.lists.initialize.data.ResourceData;
import de.gentos.lists.initialize.data.ResourceLists;
import de.gentos.lists.initialize.options.GetListOptions;

public class ResamplingMain {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	private GetListOptions options;
	private InitializeListsMain init;
	private HandleFiles log; 
	private QueryList queryList;
	private Multimap<String, LinkedList<String>> allRandomLists;
	private Map<String, ResourceLists> resources;
	private Map<String, ResourceData> originalScores;
	private double threshold = 0.05;



	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public ResamplingMain(GetListOptions options, InitializeListsMain init, QueryList queryList, Map<String, ResourceData> map) {


		// gather and retriev variables
		this.options = options;
		this.init = init;
		log = init.getLog();
		this.queryList = queryList;
		this.resources = init.getResources();
		this.originalScores = map;
		
		
		// make log entry
		log.writeOutFile("\n#### Running resampling with random gene lists.");

		// draw random genes
		drawRandomGenes();
		

		// rerun enrichment and weighting with random list and store each time that original weight is reached
		rerunEnrichment();

		
	}



	/////////////////////////
	//////// methods ////////
	/////////////////////////


	//// draw random genes

	public void drawRandomGenes() {

		// make log entry
		log.writeOutFile("Drawing random genes");

		// init and gather variables
		allRandomLists = LinkedListMultimap.create();
		int length = queryList.getQueryGenes().size();
		int iterations = options.getIteration();
		String curListName = queryList.getListName();
		long seed = options.getSeed();
		ReadInGenes genes = init.getGenes();

		// draw random set of genes

		new RandomDraw(log).drawList(length, iterations, curListName, allRandomLists, seed, genes);

	}


	// rerun enrichment
	public void rerunEnrichment() {

		// make log entry
		log.writeOutFile("Iterating lookup step");
		
		
		// init and gather variables
		Enrichment enrichment = new Enrichment(log);
		int totalGenes = init.getGenes().getAllGeneNames().size();

		// define threshold as bonferroni correction for each list
		int numberResources = resources.keySet().size();
		int numberQueries = init.getQueryLists().size();
		threshold = (double) 0.05 / ( numberResources * numberQueries);

		// for each original list rerun lookup for each randomly drawn list
		// check for enrichment of random list in resource lists
		// calculate weight of all genes on that list and save the number of times that 
		// the random weight is greater or equal the original weight
		for (String origInputList : allRandomLists.keySet()){
			
			
			for (LinkedList<String> randQuery : allRandomLists.get(origInputList)){
				System.out.println(randQuery);

				
				// init variable to gather weights over all lists
				Map<String, ResourceData> allScores = new LinkedHashMap<>();
				
				
				// for each resource list get enrichment p-val
				for ( String curResource : resources.keySet()){


					// extract the number of query genes found in resource list
					int hits = enrichment.getHits(randQuery, resources.get(curResource));

					// extract the length of the query gene list 
					int lengthList = randQuery.size();

					// get enrichment Pval for current list
					double enrichmentPval = enrichment.getEnrichment(hits, totalGenes, lengthList);

					
					// check if list is has enrichment pVal < threshold
					// if it is enriched calculate weight
					if (enrichmentPval <= threshold){
						
						// check if list is sorted or unsorted
						if (resources.get(curResource).isSorted()){
							
							// calculate weights for current resource list in sorted case
							new GetScore().rankedList(resources.get(curResource).getGeneList(), allScores);

						} else if (!resources.get(curResource).isSorted()) {
							
							// calculate weights for current resource list in unsorted case
							new GetScore().unranked(resources.get(curResource).getGeneList(), allScores);
							
						}
					}
				}


				// for each gene found in original list check if it was found in random sampled list
				// if so check if the weight is greater or equal. Then save to calculate pVal  
				
				for (String curGene : originalScores.keySet()){
					if (allScores.containsKey(curGene)) {
						if (allScores.get(curGene).getCumScore() >= originalScores.get(curGene).getCumScore()) {
							
							// save number of hits in object from original list
							originalScores.get(curGene).incrementScoreHits();
							
						}
					}
				}
			}
			
			
			
			for (String key : originalScores.keySet()) {


				if (originalScores.get(key).getScoreHits() > 0){
					int numhits = originalScores.get(key).getScoreHits();
					int numiter = options.getIteration();

					System.out.println(key + " = " + (double) numhits / numiter);
//					System.out.println("Number of Hits = " + numhits);
//					System.out.println("Number of iterations = " + numiter);


				}
			}
			
			System.out.println(originalScores.keySet().size());
			
		}
	}



	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////





}
