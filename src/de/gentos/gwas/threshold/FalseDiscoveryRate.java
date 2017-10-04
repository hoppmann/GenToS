package de.gentos.gwas.threshold;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.gentos.gwas.initialize.InitializeGwasMain;
import de.gentos.gwas.initialize.ReadInGwasData;
import de.gentos.gwas.initialize.data.GeneInfo;
import de.gentos.gwas.initialize.data.SnpLine;

public class FalseDiscoveryRate  {

	// set variables
	private InitializeGwasMain init;
	private Double threshold;


	////////////////
	//////// Constructor
	public FalseDiscoveryRate(InitializeGwasMain init) {

		this.init = init;
	}






	/////////////
	//////// methods
	
	public void runFDR(Map<String, GeneInfo> currentQueryGenes, ReadInGwasData gwasData) {

		// init varialbes
		ArrayList<Double> allPval = new ArrayList<>();

		// extract all pval of snps in query gene list
		// for each gene in queryList check if gene has gwas entries 
		// then for each gwas entry extract pval add to array allPval
		for (String gene : currentQueryGenes.keySet()){

			if (!(gwasData.getGwasSnps().get(gene) == null)){
				List<SnpLine> snpLines = gwasData.getGwasSnps().get(gene);

				for (SnpLine snp : snpLines) {
					allPval.add(snp.getpValue());

				}
			}
		}

		// sort list of pvals and get number of pvals
		Collections.sort(allPval);
		int totalLength = allPval.size();

		// calculate threshold using benjamini hochberg
		double alpha = init.getGwasOptions().getAlpha();
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
