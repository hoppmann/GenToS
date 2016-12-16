package de.gentos.geneSet.lookup;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import de.gentos.geneSet.initialize.InitializeListsMain;
import de.gentos.geneSet.initialize.data.QueryList;
import de.gentos.geneSet.initialize.data.ResourceData;
import de.gentos.geneSet.initialize.data.ResourceLists;
import de.gentos.general.files.HandleFiles;

public class LookupMain {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	private InitializeListsMain init;
	private HandleFiles log;
	private LinkedList<String> queryList;
	private Map<String, ResourceLists> resources;
	private Map<String, ResourceData> allScores;

	// basic variable
	private double threshold = 0.05;


	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public LookupMain(InitializeListsMain init, QueryList queryList) {

		// retrieve variables
		this.init = init;
		log = init.getLog();
		this.resources = init.getResources();
		this.queryList = queryList.getQueryGenes();

		// run through different steps

		// make log entry
		log.writeOutFile("\n######## Running lookup ########");


		// get enrichement for each resource list
		getEnrichment();

		
		
		// create weighted List
		createListOfScores();


	}





	/////////////////////////
	//////// methods ////////
	/////////////////////////

	//// check for enrichment
	public void getEnrichment() {

		// make log entry
		log.writeOutFile("\n#### Calculating enrichment in resource lists");

		// init and gather variables
		Enrichment enrichment = new Enrichment(log);
		int totalGenes = init.getGenes().getAllGeneNames().size();

		// define threshold as bonferonie correction for each list
		int numberResources = resources.keySet().size();
		int numberQueries = init.getQueryLists().size();
		threshold = (double) 0.05 / ( numberResources * numberQueries);

		// for each resource list get enrichment p-val
		for (String curList: resources.keySet()){

			// extract the number of query genes found in resource list
			int numberOfHits = enrichment.getHits(queryList, resources.get(curList));

			
			// extract the length of the query gene list and the resource list
			int lengthQueryList = queryList.size();
			int lengthResourceList = resources.get(curList).getGeneList().size();
			
			// get enrichment p-value based on fisher exact test
			
			double enrichmentPval = enrichment.fisherEnrichment(numberOfHits, lengthResourceList, lengthQueryList, totalGenes);


			// check if list is has enrichment pVal < threshold
			if (enrichmentPval <= threshold){
				resources.get(curList).setEnriched(true);
			}

			// store enrichment pVal
			resources.get(curList).setEnrichmentPval(enrichmentPval);


		}
	}



	//// create final ranked list
	public void createListOfScores(){

		// make log entry
		log.writeOutFile("\n#### Calculating gene scores");

		allScores = new LinkedHashMap<>();



		// for each enriched list calculate the weight for each gene and save in hash
		for (String curList : resources.keySet()) {


			// check if list is enriched
			if (resources.get(curList).isEnriched()) {

				if (resources.get(curList).isSorted()){
					
					new GetScore().rankedList(resources.get(curList).getGeneList(), allScores);


				} else {

					new GetScore().unranked(resources.get(curList).getGeneList(), allScores);

				}
			}
		}
	}


	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////


	public Map<String, ResourceData> getAllScores() {
		return allScores;
	}





}
