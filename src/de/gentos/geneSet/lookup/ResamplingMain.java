package de.gentos.geneSet.lookup;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import de.gentos.geneSet.initialize.InitializeGeneSetMain;
import de.gentos.geneSet.initialize.data.InputList;
import de.gentos.geneSet.initialize.data.ResourceData;
import de.gentos.geneSet.initialize.data.ResourceLists;
import de.gentos.geneSet.initialize.options.GetGeneSetOptions;
import de.gentos.general.files.HandleFiles;
import de.gentos.general.files.ReadInGenes;
import de.gentos.gwas.validation.RandomDraw;

public class ResamplingMain {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	private GetGeneSetOptions options;
	private InitializeGeneSetMain init;
	private HandleFiles log; 
	private InputList inputList;
	private Multimap<String, LinkedList<String>> allRandomLists;
	private Map<String, ResourceLists> resources;
	private Map<String, ResourceData> originalScores;
	private double threshold = 0.05;
	private String curInputList;



	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public ResamplingMain(GetGeneSetOptions options, InitializeGeneSetMain init, InputList inputList, Map<String, ResourceData> map) {


		// gather and retriev variables
		this.options = options;
		this.init = init;
		this.log = init.getLog();
		this.inputList = inputList;
		this.resources = init.getResources();
		this.originalScores = map;
		this.curInputList = inputList.getListName();
		
		
		// make log entry
		log.writeOutFile("\n######## Resampling with random gene lists. ########");

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
		int length = inputList.getQueryGenes().size();
		int iterations = options.getIteration();
		String curListName = inputList.getListPath();
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
		int numberQueries = init.getInputLists().size();
		threshold = (double) 0.05 / ( numberResources * numberQueries);

		// for each original list rerun lookup for each randomly drawn list
		// check for enrichment of random list in resource lists
		// calculate weight of all genes on that list and save the number of times that 
		// the random weight is greater or equal the original weight
		for (String origInputList : allRandomLists.keySet()){
			
			
			for (LinkedList<String> randQuery : allRandomLists.get(origInputList)){

				
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
			
			
			
			
			/////////////////////////////////////////////////////////////////////////
			//////// calculate resampling pval for each gene 
			//////// sort list according to pvals and save the results in sorted list

			// prepare results file
			String resultFileName = FilenameUtils.getBaseName(origInputList) + ".csv";
			HandleFiles resultOut = new HandleFiles();
			resultOut.openWriter(options.getOutDir() + File.separator + resultFileName);
			
			
			// init variables
			Map<String, Double> geneList = new LinkedHashMap<>();
			
			for (String curGene : originalScores.keySet()) {

				// init variables
				double pval = 0;
				
				// check if gene was found by any random draw then calculate pval else define pval as 0 
				if (originalScores.get(curGene).getScoreHits() > 0){
					int numhits = originalScores.get(curGene).getScoreHits();
					int numiter = options.getIteration();
					
					// calculate p-value
					pval = (double) numhits / numiter;
				}
				
				// store pval and create hash to get sorted
				originalScores.get(curGene).setEmpiricalPVal(pval);
				geneList.put(curGene, pval);
			}
			
			// Sort genes according to their pval
			Map<String, Double> sortedGeneList = sortByValue(geneList);
			
			
			
			//// prepare out file header
			// prepare summary of enriched Lists 
			
			int numberEnrichedLists = init.getDataMap().get(curInputList).getNumberEnrichedLists();
			LinkedList<String> enrichedLists = init.getDataMap().get(curInputList).getEnrichedLists();

			resultOut.writeFile("# Number of enriched lists: " + numberEnrichedLists);
			for (String curEnrichedList : enrichedLists){
				Boolean isSorted = init.getResources().get(curEnrichedList).isSorted();
				
				if (isSorted) {
					resultOut.writeFile("# " + curEnrichedList + "\tsorted");
				} else {
					resultOut.writeFile("# " + curEnrichedList + "\tnot sorted");
				}
				
			
			}
			
			// prepare values definitions
			resultOut.writeFile("#gene\tp-value\tscore [-log10]");

			// print out values
			for (String curGene : sortedGeneList.keySet()){
				double pval = originalScores.get(curGene).getEmpiricalPVal();
				double score = originalScores.get(curGene).getLogCumScore();
				
				resultOut.writeFile(curGene + "\t" + pval + "\t" + score);
				
			}
			
			// close result file
			resultOut.closeFile();
		}
	}

	
	
		////////////////////
		//////// sort hash according to the values
		public Map<String, Double> sortByValue(Map<String, Double> map) {
	        List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(map.entrySet());
	
	        
	        // sort by values
	        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {

	        	public int compare(Map.Entry<String, Double> m1, Map.Entry<String, Double> m2) {
	        		return (m2.getValue()).compareTo(m1.getValue());
	        	}
	        });

	        // reverse sort order to start with smallest
	        List<Map.Entry<String, Double>> reverseList = Lists.reverse(list);
	        
	        // transfer sorted list to linked hash map
	        Map<String, Double> result = new LinkedHashMap<String, Double>();
	        for (Map.Entry<String, Double> entry : reverseList) {
	            result.put(entry.getKey(), entry.getValue());
	        }
	        return result;
	    }

	
	


	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
		
		
		

}
