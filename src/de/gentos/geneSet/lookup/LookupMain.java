package de.gentos.geneSet.lookup;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import de.gentos.geneSet.initialize.InitializeGeneSetMain;
import de.gentos.geneSet.initialize.data.InputList;
import de.gentos.geneSet.initialize.data.RunData;
import de.gentos.geneSet.initialize.data.ResourceData;
import de.gentos.geneSet.initialize.data.ResourceLists;
import de.gentos.general.files.HandleFiles;

public class LookupMain {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	private InitializeGeneSetMain init;
	private HandleFiles log;
	private LinkedList<String> inputList;
	private Map<String, ResourceLists> resources;
	private Map<String, ResourceData> allScores;
	private Map<String, RunData> data;
	private String curInputList;

	// basic variable
	private double threshold = 0.05;


	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public LookupMain(InitializeGeneSetMain init, InputList inputList) {

		// retrieve variables
		this.init = init;
		log = init.getLog();
		this.resources = init.getResources();
		this.inputList = inputList.getQueryGenes();
		this.data = init.getDataMap();
		this.curInputList = inputList.getListName();

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
		int numberQueries = init.getInputLists().size();
		threshold = (double) 0.05 / ( numberResources * numberQueries);

		
		// initialize variables needed for qc
//		int numberOfEnrichedLists = 0;
//		LinkedList<String> enrichedLists = new LinkedList<>();

		
		// for each resource list get enrichment p-val
		for (String curResourceList: resources.keySet()){
			
			
			// extract the number of query genes found in resource list
			int numberOfHits = enrichment.getHits(inputList, resources.get(curResourceList));

			
			// extract the length of the query gene list and the resource list
			int lengthQueryList = inputList.size();
			int lengthResourceList = resources.get(curResourceList).getGeneList().size();
			
			// get enrichment p-value based on fisher exact test
			
			double enrichmentPval = enrichment.fisherEnrichment(numberOfHits, lengthResourceList, lengthQueryList, totalGenes);

			// check if list is has enrichment pVal < threshold, remember enriched lists
			if (enrichmentPval <= threshold){
				data.get(curInputList).getResources().get(curResourceList).setEnriched(true);
				data.get(curInputList).incrementEnrichment();
				data.get(curInputList).getEnrichedLists().add(curResourceList);
//				resources.get(curResourceList).setEnriched(true);
//				numberOfEnrichedLists++;
//				enrichedLists.add(curResourceList);

			}

			// store enrichment pVal
			data.get(curInputList).getResources().get(curResourceList).setEnrichmentPval(enrichmentPval);
//			resources.get(curResourceList).setEnrichmentPval(enrichmentPval);


		}
		
		
//		// write qc elements to log file
//		log.writeOutFile("Number of enriched lists: " + numberOfEnrichedLists);
//		for (String curEnrichedList : enrichedLists) {
//			log.writeOutFile(curEnrichedList);
//		}

	}



	//// create final ranked list
	public void createListOfScores(){

		// make log entry
		log.writeOutFile("\n#### Calculating gene scores");

		allScores = new LinkedHashMap<>();

		// for each enriched list calculate the weight for each gene and save in hash
		for (String curResourceList : resources.keySet()) {

			// check if list is enriched if so check if it is sorted or not and score accordingly
//			if (resources.get(curResourceList).isEnriched()) {
			
			if (data.get(curInputList).getResources().get(curResourceList).isEnriched()) {

//				if (resources.get(curResourceList).isSorted()){
				if (data.get(curInputList).getResources().get(curResourceList).isSorted()) {
				
					new GetScore().rankedList(resources.get(curResourceList).getGeneList(), allScores);
					
				} else {

					new GetScore().unranked(resources.get(curResourceList).getGeneList(), allScores);

				}
				
				// reset enrichment flag for future checks
//				resources.get(curResourceList).setEnriched(false);
				
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
