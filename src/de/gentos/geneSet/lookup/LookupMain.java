package de.gentos.geneSet.lookup;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.gentos.geneSet.initialize.InitializeGeneSetMain;
import de.gentos.geneSet.initialize.data.InputList;
import de.gentos.geneSet.initialize.data.ResourceLists;
import de.gentos.geneSet.initialize.data.RunData;
import de.gentos.geneSet.initialize.options.GetGeneSetOptions;
import de.gentos.general.files.HandleFiles;

public class LookupMain {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	private InitializeGeneSetMain init;
	private HandleFiles log;
	private LinkedList<String> inputList;
	private Map<String, ResourceLists> resources;
	private GetGeneSetOptions options;
	private RunData runData; 

	// basic variable
	private double threshold = 0.05;


	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public LookupMain(InitializeGeneSetMain init, InputList inputList, RunData runData) {

		
		
		// retrieve variables
		this.init = init;
		log = init.getLog();
		this.options = init.getOptions();
		this.resources = init.getResources();
		this.inputList = inputList.getQueryGenes();
		this.runData = runData;

		// run through different steps

		// make log entry if run on single core
		if (options.getThreads() == 1 || options.isVerbose()){
			log.writeOutFile("\n######## Running lookup ########");
		}
		// get enrichement for each resource list
		getEnrichment();

		// create weighted List
		createListOfScores();


	}





	/////////////////////////
	//////// methods ////////
	/////////////////////////



	/////////////////////////////
	//////// check for enrichment

	public void getEnrichment() {

		// make log entry
		if (options.getThreads() == 1 || options.isVerbose()){
			log.writeOutFile("\n#### Calculating enrichment in resource lists");
		}
		// init and gather variables
		Enrichment enrichment = new Enrichment(log);
		int totalGenes = init.getGeneDbGenes().getAllGeneNames().size();

		// define threshold as bonferroni correction for each list
		int numberResources = resources.keySet().size();
		int numberQueries = init.getInputLists().size();

		// if set stringent make bonferroni for resources AND queries, else resources only
		if (options.isStringent()){
			threshold = (double) 0.05 / ( numberResources * numberQueries );
		} else {
			threshold = (double) 0.05 / ( numberResources );

		}

		// for each resource list get enrichment p-val
		for (String curResourceList: resources.keySet()){


			// extract the number of query genes found in resource list
			int numberOfHits = enrichment.getHits(inputList, resources.get(curResourceList));


			// extract the length of the query gene list and the resource list
			int lengthQueryList = inputList.size();
			int lengthResourceList = resources.get(curResourceList).getGenes().size();


			// get enrichment p-value based on fisher exact test
			double enrichmentPval = enrichment.fisherEnrichment(numberOfHits, lengthResourceList, lengthQueryList, totalGenes);

			// check if list is has enrichment pVal < threshold, remember enriched lists
			if (enrichmentPval <= threshold){
				runData.getResources().get(curResourceList).setEnriched(true);
				runData.incrementEnrichment();
				runData.getEnrichedResources().add(curResourceList);
			}


			// store enrichment pVal
			runData.getResources().get(curResourceList).setEnrichmentPval(enrichmentPval);
		}
	}







	/////////////////////////////////
	//////// create final ranked list

	public void createListOfScores(){

		// make log entry
		if (options.getThreads() == 1 || options.isVerbose()){
			log.writeOutFile("\n#### Calculating gene scores");
		}
		// for each enriched list calculate the weight for each gene and save in hash
		for (String curResourceList : resources.keySet()) {

			// check if list is enriched if so check if it is sorted or not and score accordingly
			if (runData.getResources().get(curResourceList).isEnriched()) {

				if (runData.getResources().get(curResourceList).isSorted()) {

					List<String> resourceGeneList = new ArrayList<>(resources.get(curResourceList).getGenes().keySet());	

					new GetScore().rankedList(resourceGeneList, runData.getGeneData(), curResourceList);

				} else {

					List<String> resourceGeneList = new ArrayList<>(resources.get(curResourceList).getGenes().keySet());	
					new GetScore().unranked(resourceGeneList, runData.getGeneData(), curResourceList);
				}
			}
		}
	}


	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////


}
