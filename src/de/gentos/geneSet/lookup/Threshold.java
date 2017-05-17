package de.gentos.geneSet.lookup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import de.gentos.geneSet.initialize.InitializeGeneSetMain;

public class Threshold {
	///////////////////////////
	//////// variables ////////
	///////////////////////////

	Double [] allPval;
	InitializeGeneSetMain init;
	
	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public Threshold(InitializeGeneSetMain init) {

		this.init = init;
	
	}
	

	
	
	
	/////////////////////////
	//////// methods ////////
	/////////////////////////

	// define threshold by benjamini-hochberg
	public double benjaminiHochberg(List<Double> allPval) {
		
		
		
		/* generate list containing entries for each gene on resource lists
		 * if there are more genes on resource lists then on enriched lists fill rest
		 * up with 1
		 */
		// get all genes in all resources
		Set<String> allGenesInResources = new TreeSet<>();
		for (String curResource : init.getResources().keySet()) {
			for (String curGene : init.getResources().get(curResource).getGenes().keySet()){
				allGenesInResources.add(curGene);
			}
		}
		
		// fill missing amount of genes up with ones
		int difference = allGenesInResources.size() - allPval.size();
		List<Double> tempList = new ArrayList<>(Collections.nCopies(difference, 1.0));
		tempList.addAll(allPval);
		allPval = tempList;
		Collections.sort(allPval);
		int totalLength = allPval.size();

		
		// calculate threshold using benjamini hochberg
		double alpha = init.getOptions().getAlpha();
		double threshold = 0.0;

		for (int counter = 0; counter < totalLength; counter++) {

			// calculate p
			Double compare = (double) (counter +1) / totalLength * alpha;

			// check if BH-condition satisfied if, remember as possible threshold,
			// -> last accepted p-value is threshold
			if ((allPval.get(counter) <= compare)) {
				threshold = allPval.get(counter);
			}
		}
		
		// send back result
		return threshold;
	}
	
	
	
	
	
	
	
	// define threshold by bonferroni
	public double bonferroni (List<Double> allPval) {

			
		// calculate bonferroni threshold
		double alpha = init.getOptions().getAlpha();
		
		double threshold =  alpha / allPval.size();
		
		// send back result
		return threshold;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////



















}
