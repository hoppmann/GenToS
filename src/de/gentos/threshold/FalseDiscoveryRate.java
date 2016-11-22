package de.gentos.threshold;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.gentos.initialize.InitializeMain;
import de.gentos.initialize.ReadInGwasData;
import de.gentos.initialize.data.GeneListInfo;
import de.gentos.initialize.data.SnpLine;

public class FalseDiscoveryRate  {

	// set variables
	private InitializeMain init;
	private Double threshold;


	////////////////
	//////// Constructor
	public FalseDiscoveryRate(InitializeMain init) {

		this.init = init;
	}






	/////////////
	//////// methods
	
	public void runFDR(Map<String, GeneListInfo> currentQueryGenes, LinkedList<String> currentList, ReadInGwasData gwasData) {

		// init varialbes
		ArrayList<Double> allPval = new ArrayList<>();

		// extract all pval of snps in query gene list
		// for each gene in queryList check if gene has gwas entries 
		// then for each gwas entry extract pval add to array allPval
		for (String gene : currentQueryGenes.keySet()){

			if (!(gwasData.getGeneSNP().get(gene) == null)){
				List<SnpLine> snpLines = gwasData.getGeneSNP().get(gene);

				for (SnpLine snp : snpLines) {
					allPval.add(snp.getpValue());

				}
			}
		}

		// sort list of pvals and get number of pvals
		Collections.sort(allPval);
		int totalLength = allPval.size();

		// calculate threshold using benjamini hochberg
		double alpha = init.getOptions().getAlpha();
		threshold = 0.0;

		for (int counter = 0; counter < totalLength; counter++) {

			// calculate p
			Double compare = (double) (counter +1) / totalLength * alpha;

			// check if BH-condition satisfied if, remember as possible threshold,
			// -> last accepted p-value is threshold
			if ((allPval.get(counter) <= compare)) {
				threshold = allPval.get(counter);
			}
		}


		// for each gene note threshold and method used for threshold detection in ReadGenes 
		for (String gene : currentQueryGenes.keySet()) {
			currentQueryGenes.get(gene).setThreshold(threshold);
			currentQueryGenes.get(gene).setMethod("FDR");
		}
	}


	///////////
	//////// Getter


	public Double getThreshold() {
		return threshold;
	}






}
