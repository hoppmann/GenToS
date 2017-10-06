package de.gentos.geneSet.lookup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.collect.Lists;

import de.gentos.geneSet.initialize.InitializeGeneSetMain;
import de.gentos.geneSet.initialize.data.GeneData;
import de.gentos.geneSet.initialize.data.InputList;
import de.gentos.geneSet.initialize.data.ResourceLists;
import de.gentos.geneSet.initialize.data.RunData;
import de.gentos.geneSet.initialize.options.GetGeneSetOptions;
import de.gentos.general.files.HandleFiles;
import de.gentos.general.files.ReadInGeneDB;
import de.gentos.general.misc.generalMethods;
import de.gentos.gwas.validation.RandomDraw;

public  class ResamplingMain {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	private GetGeneSetOptions options;
	private InitializeGeneSetMain init;
	private HandleFiles log; 
	private InputList inputList;
	private ArrayList<LinkedList<String>> allRandomLists;
	private Map<String, ResourceLists> resources;
	private Map<String, GeneData> originalScores;
	private RunData runData;
	private double threshold = 0.05;



	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public ResamplingMain(GetGeneSetOptions options, InitializeGeneSetMain init, 
			InputList inputList, RunData runData) {


		// gather and retrieve variables
		this.options = options;
		this.init = init;
		this.log = init.getLog();
		this.inputList = inputList;
		this.resources = init.getResources();
		this.originalScores = runData.getGeneData();
		this.runData = runData;

		// make log entry
		if (options.getThreads() == 1 || options.isVerbose()){
			log.writeOutFile("\n######## Resampling with random gene lists. ########");
		}

		// draw random genes
		drawRandomGenes();

		// rerun enrichment and weighting with random list and store each time that original weight is reached
		rerunEnrichment();

		
		// calculate the threshold to be used for final output list
		finalThresh();

	}



	/////////////////////////
	//////// methods ////////
	/////////////////////////


	////////////////////////////
	//////// get final threshold
	
	private void finalThresh(){
		
		
		Map<String, Double> sortedEmpiricalPvalList = new generalMethods().sortMapByValue(runData.getEmpiricalPval(), true);		
		List<Double> allPval = new ArrayList<>();
		allPval = Arrays.asList(sortedEmpiricalPvalList.values().toArray(new Double[0]));
		
		
		Threshold threshold = new Threshold(init);
		
		if (options.getThreshMethod().matches("FDR")){
			runData.setFinalThresh(threshold.benjaminiHochberg(allPval));

		} else if (options.getThreshMethod().matches("bonferroni")){
			runData.setFinalThresh(threshold.bonferroni(allPval));

		}
	}
	
	
	
	
	
	
	
	
	

	//////////////////////
	//// draw random genes

	public void drawRandomGenes() {

		// make log entry
		if (options.getThreads() == 1 || options.isVerbose()){
			log.writeOutFile("Drawing random genes");
		}
		// init and gather variables
		allRandomLists = new ArrayList<>();
		int length = inputList.getQueryGenes().size();
		int iterations = options.getIteration();
		String curListName = inputList.getListPath();
		long seed = options.getSeed();
		ReadInGeneDB genes = init.getGeneDbGenes();

		// draw random set of genes
		// create map to be able to reuse GenToS GWAS random draw
		if (options.getThreads() == 1 || options.isVerbose()){
			new RandomDraw(log).drawSingleList(length, iterations, curListName, allRandomLists, seed, genes, true);
		} else {
			new RandomDraw(log).drawSingleList(length, iterations, curListName, allRandomLists, seed, genes, false );
		}
	}


	
	
	
	
	/////////////////////////
	//////// rerun enrichment
	public void rerunEnrichment() {

		// make log entry
		if (options.getThreads() == 1 || options.isVerbose()){
			log.writeOutFile("Iterating lookup step");
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








		/* for each original list rerun lookup for each randomly drawn list
		 * check for enrichment of random list in resource lists
		 * calculate weight of all genes on that list and save the number of times that 
		 * the random weight is greater or equal the original weight
		 */


		// define maximum number of threads to be used
		ExecutorService executor = Executors.newFixedThreadPool(options.getCpuPerThread());

		// for each random drawn gene list start one process
		for (LinkedList<String> curRandQuery : allRandomLists){

			Runnable task = new ResamplingIteration(curRandQuery, resources, enrichment, totalGenes, originalScores, threshold);
			executor.execute(task);
			
		}

		// wait for all jobs to finish
		executor.shutdown();
		while (! executor.isTerminated()){
		}


		/////////////////////////////////////////////////////////////////////////
		//////// calculate resampling pval for each gene 
		//////// sort list according to pvals and save the results in sorted list

		///////////////
		// for each originally found gene calculate empirical pVal
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

		// Sort genes according to their pval and save for later use
		Map<String, Double> sortedGeneList = sortByValue(geneList);
		runData.setEmpiricalPval(sortedGeneList);

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
