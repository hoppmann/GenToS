package de.gentos.threshold;

import java.util.Map;

import org.apache.commons.cli.CommandLine;

import de.gentos.initialize.InitializeMain;
import de.gentos.initialize.ReadInGenes;
import de.gentos.initialize.ReadInGwasData;
import de.gentos.initialize.data.GeneListInfo;
import de.gentos.main.HandleFile;

public class Bonferroni  {

	//////////////////////
	//////// Set variables
	InitializeMain init;
	HandleFile log;
	CommandLine cmd;
	double basePval = 0.05;
	ReadInGwasData gwasData;
	ReadInGenes readGenes;
	Double thresh; 







	////////////////////
	//////// Constructor


	public Bonferroni (InitializeMain init, ReadInGwasData gwasData) {
		this.init = init;
		this.gwasData = gwasData;
		this.readGenes = init.getReadGenes();
	}









	////////////////
	//////// Methods

	//////// run without further options

	public void correctOnly (Map<String, GeneListInfo> genes ){

		
		// set lenient to -9
		for (String gene : genes.keySet()) {
			genes.get(gene).setGwasSNPs(-9);

			// get number of independent SNPs to correct for
			int indep = genes.get(gene).getIndepSNPs(); 
			
			// to avoid infinity, if denominator = 0 set to 1
			if (indep == 0) {
				indep = 1;
			}
			// calculate threshold and set in GeneListInfo-Object
			thresh = basePval/ indep;
			genes.get(gene).setThreshold(thresh);
		}
	}






	//////// run on plenty and lenient mode
	public void plentyLenient ( Map<String, GeneListInfo> genes) {

		// extract number of SNPs in gwas file
		lenient(genes);

		// run plenty option
		int totalSNPs = plenty(genes);

		// to avoid infinity, if denominator = 0 set to 1
		if (totalSNPs == 0) {
			totalSNPs = 1;
		}

		// calculate threshold
		thresh = basePval / totalSNPs;

		// add threshold to geneInfo object
		for (String gene : genes.keySet()){
			genes.get(gene).setThreshold(thresh);
		}

	}








	//////// run on plenty mode only
	public void plentyOnly (Map<String, GeneListInfo> genes) {

		// set lenient to -9
		for (String gene : genes.keySet()) {
			genes.get(gene).setGwasSNPs(-9);
		}
		
		// get total number of SNPs for all genes summed up
		int totalSNPs = plenty(genes);

		// to avoid infinity, if denominator = 0 set to 1
		if (totalSNPs == 0) {
			totalSNPs = 1;
		}

		// calculate threshold
		thresh = basePval / totalSNPs;

		// append thesh to hash
		for (String gene : genes.keySet()) {
			genes.get(gene).setThreshold(thresh);
		}

	}







	//////// run on lenient mode only
	public void lenientOnly ( Map<String, GeneListInfo> genes) {

		// extract number of SNPs in gwas file
		lenient(genes);

		// correct threshold and add value to hash
		for (String gene : genes.keySet()) {

			// check whether gwas file or kgp has less SNPs
			// and calculate threshold by lesser SNPs
			Integer indep = genes.get(gene).getIndepSNPs(); 
			Integer gwas = genes.get(gene).getGwasSNPs();

			// to avoid infinity failure, if denominator = 0 set to 1
			if (indep == 0) {
				indep = 1;
			}

			if (gwas == 0) {
				gwas = 1;
			}

			thresh = basePval / Math.min(indep, gwas);
			genes.get(gene).setThreshold(thresh);
		}
	}







	////////////
	//////// lenient option

	public void lenient(Map<String, GeneListInfo> genes) {


		// for each gene extract number of snps in gwas file and add to genes hash
		for (String gene : genes.keySet()) {
			int gwasSNPs = 0;
			// check if there are entries for gwas snps else set 0
			if (!(gwasData.getGeneSNP().get(gene) == null)) {
				gwasSNPs = gwasData.getGeneSNP().get(gene).size();
				}
			genes.get(gene).setGwasSNPs(gwasSNPs);
		}
	}


	//////////////
	//////// extract total number of SNPs to correct for
	public int plenty(Map<String, GeneListInfo> genes) {

		// get number of independent SNPs (sum up all SNPs)
		int totalNumberSNPs = 0;

		for (String gene : genes.keySet()){

			// extract int values of gwas and kgp indep
			Integer indep = genes.get(gene).getIndepSNPs();
			Integer gwas = genes.get(gene).getGwasSNPs();
			
			// check if lenient column has -9 (no lenient option)
			if (gwas == -9 || gwas > indep ) {
				totalNumberSNPs += indep;

				// else check if lenient < indep
			} else {
				if ( gwas < indep ) {
					totalNumberSNPs += gwas;
				}
			}
		}

		// return number of independent SNPs
		return totalNumberSNPs;
	}





	/////////////
	//////// getter
	



	public Double getThresh() {
		return thresh;
	}
	
	

	
	
	
}
