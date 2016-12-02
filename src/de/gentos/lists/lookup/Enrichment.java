package de.gentos.lists.lookup;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.distribution.BinomialDistribution;

import de.gentos.general.files.HandleFiles;
import de.gentos.lists.initialize.data.ResourceLists;

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
	public int getHits(LinkedList<String> queryList, ResourceLists resourceList) {
		
		// for each gene in query gene list check if is in resource list, if
		int numberOfHits = 0;
		for (String gene : queryList) {
			
			// retrieve varialbe
			List<String> geneList = resourceList.getGeneList();

			// if gene is found in resource list increment hit count
			if (geneList.contains(gene)) {
				numberOfHits++;
			}
		}

		return numberOfHits;
	}
	

	
	
	
	// calculate the enrichment p-val 
	public double getEnrichment (int hits, int totalGenes, int lengthList) {
		
		// init variables
		double pVal = 0;
		
		double probHit = (double) lengthList / totalGenes;
		
		BinomialDistribution bino = new BinomialDistribution(lengthList, probHit);
		pVal  = 1 - bino.cumulativeProbability(hits - 1 );

		// return enrichment pval
		return pVal;
		
	}
	

	
	
	
	
	
	
	
	
	
	
	
	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////






}
