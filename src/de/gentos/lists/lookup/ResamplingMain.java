package de.gentos.lists.lookup;

import java.util.LinkedList;
import java.util.Map;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import de.gentos.general.files.HandleFiles;
import de.gentos.general.files.ReadInGenes;
import de.gentos.gwas.validation.RandomDraw;
import de.gentos.lists.initialize.InitializeListsMain;
import de.gentos.lists.initialize.data.QueryList;
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
	private double threshold = 0.05;



	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public ResamplingMain(GetListOptions options, InitializeListsMain init, QueryList queryList) {


		// gather and retriev variables
		this.options = options;
		this.init = init;
		log = init.getLog();
		this.queryList = queryList;
		this.resources = init.getResources();

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
		log.writeOutFile("Rerunning lookup step");
		
		
		// init and gather variables
		Enrichment enrichment = new Enrichment(log);
		int totalGenes = init.getGenes().getAllGeneNames().size();

		// define threshold as bonferroni correction for each list
		int numberResources = resources.keySet().size();
		int numberQueries = init.getQueryLists().size();
		threshold = (double) 0.05 / ( numberResources * numberQueries);


		
		// for each original list rerun enrichment for each randomly drawn list
		for (String origList : allRandomLists.keySet()){
			for (LinkedList<String> randQuery : allRandomLists.get(origList)){

				// for each resource list get enrichment p-val
				for ( String curList : resources.keySet()){

					// extract the number of query genes found in resource list
					int hits = enrichment.getHits(randQuery, resources.get(curList));

					// extract the length of the query gene list 
					int lengthList = randQuery.size();

					// get enrichment Pval for current list
					double enrichmentPval = enrichment.getEnrichment(hits, totalGenes, lengthList);


					// check if list is has enrichment pVal < threshold
					if (enrichmentPval <= threshold){
						
						resources.get(curList).setEnriched(true);
					}
				}
			}
			
			
			//TODO get weights of random genes
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
		}
	}

	//	// make log entry
	//			log.writeOutFile("\n#### Calculating enrichment in resource lists");
	//			
	//			// init and gather variables
	//			Enrichment enrichment = new Enrichment(log);
	//			int totalGenes = init.getGenes().getAllGeneNames().size();
	//
	//			
	//			// define threshold as bonferonie correction for each list
	//			int numberResources = resources.keySet().size();
	//			int numberQueries = init.getQueryLists().size();
	//			threshold = (double) 0.05 / ( numberResources * numberQueries);
	//
	//			// for each resource list get enrichment p-val
	//			for (String curList: resources.keySet()){
	//
	//				// extract the number of query genes found in resource list
	//				int hits = enrichment.getHits(queryList, resources.get(curList));
	//
	//				// extract the length of the query gene list and the total number of genes
	//				int lengthList = queryList.size();
	//
	//				double enrichmentPval = enrichment.getEnrichment(hits, totalGenes, lengthList);
	//
	//
	//				// check if list is has enrichment pVal < threshold
	//				if (enrichmentPval <= threshold){
	//					resources.get(curList).setEnriched(true);
	//				}
	//
	//				// store enrichment pVal
	//				resources.get(curList).setEnrichmentPval(enrichmentPval);
	//
	//
	//			}






	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////





}
