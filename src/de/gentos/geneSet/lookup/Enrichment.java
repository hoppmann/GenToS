package de.gentos.geneSet.lookup;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.distribution.BinomialDistribution;

import de.gentos.geneSet.initialize.data.ResourceLists;
import de.gentos.general.files.HandleFiles;

public class Enrichment {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	HandleFiles log;
	
	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public Enrichment(HandleFiles log) {
		
		// retrieve variables
		this.log = log;
	
	}
	
	
	
	
	
	/////////////////////////
	//////// methods ////////
	/////////////////////////

	// extract the number of hits found in resource list using the query list
	public int getHits(LinkedList<String> inputList, ResourceLists resourceList) {
		
		// for each gene in query gene list check if is in resource list, if
		int numberOfHits = 0;

		// get list of genes in resource list
		List<String> geneList = new ArrayList<>(resourceList.getGenes().keySet());

		
		for (String curGene : inputList) {
			
			// if gene is found in resource list increment hit count
			if (geneList.contains(curGene)) {
				numberOfHits++;
			}
		}

		return numberOfHits;
	}
	

	
	
	
	
	// calculate the enrichment p-val based on binomial distribution
	public double getEnrichment (int hits, int totalGenes, int lengthList) {
		
		// init variables
		double pVal = 0;
		
		double probHit = (double) lengthList / totalGenes;
		
		BinomialDistribution bino = new BinomialDistribution(lengthList, probHit);
		pVal  = 1 - bino.cumulativeProbability(hits - 1 );

		// return enrichment pval
		return pVal;
		
	}
	

	
	
	
	// calculate enrichment p-val based on fisher exact test
	public double fisherEnrichment(int inputResource, int lengthResource, int lengthInput, int totalGenes) {
		
		// init variables
		double pVal = 0;
		
		
		//////// prepare contingency table
		
		// collect table entries
		int inputNotResource = lengthInput - inputResource;
		int notInputResource = lengthResource - inputResource;
		int notInputNotResource = totalGenes - lengthResource - inputNotResource;

		
		
		// calculate fisher one-sided fisher test
		FisherTest fisher = new FisherTest(totalGenes, log);

		/* the entries of the contingency table are
						this.class								Fisher.class
			inputResources		inputNotResource		=>	a11		c12
			notInputResources	NotInputNotResource		=>	b21		d22
		
		*/
		
		
		pVal = fisher.getCumulativevP(inputResource, notInputResource, inputNotResource, notInputNotResource);
		
		// return enrichment pval
		return pVal;
		
	}
	
	
	
	
	
	
	
	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////






}
