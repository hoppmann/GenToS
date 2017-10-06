package de.gentos.gwas.threshold;

import java.util.Map;

import org.apache.commons.cli.CommandLine;

import de.gentos.general.files.HandleFiles;
import de.gentos.general.files.ReadInGeneDB;
import de.gentos.gwas.initialize.InitializeGwasMain;
import de.gentos.gwas.initialize.ReadInGwasData;
import de.gentos.gwas.initialize.data.GeneInfo;

public class Bonferroni  {

	//////////////////////
	//////// Set variables
	InitializeGwasMain init;
	HandleFiles log;
	CommandLine cmd;
	double basePval = 0.05;
	ReadInGwasData gwasData;
	ReadInGeneDB readGenes;
	Double thresh; 







	////////////////////
	//////// Constructor


	public Bonferroni (InitializeGwasMain init, ReadInGwasData gwasData) {
		this.init = init;
		this.gwasData = gwasData;
		this.readGenes = init.getReadGenes();
	}









	////////////////
	//////// Methods

	//////// run without further options

	public void correctOnly (Map<String, GeneInfo> genes ){
		
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



	
	//////// run on plenty mode only
	public void plentyOnly (Map<String, GeneInfo> genes) {

		// set lenient to -9
		for (String gene : genes.keySet()) {
			genes.get(gene).setGwasSNPs(-9);
		}
		
		// get total number of SNPs for all genes summed up
		int totalSNPs = getSumOfSnps(genes);

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

	
	
	
	
	
	
	
	//////////////
	//////// extract total number of SNPs to correct for
	public int getSumOfSnps(Map<String, GeneInfo> genes) {

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


	



//	//////// run on plenty and lenient mode
//	public void plentyLenient ( Map<String, GeneInfo> genes) {
//
//		// extract number of SNPs in gwas file
//		lenient(genes);
//
//		// run plenty option
//		int totalSNPs = plenty(genes);
//
//		// to avoid infinity, if denominator = 0 set to 1
//		if (totalSNPs == 0) {
//			totalSNPs = 1;
//		}
//
//		// calculate threshold
//		thresh = basePval / totalSNPs;
//
//		// add threshold to geneInfo object
//		for (String gene : genes.keySet()){
//			genes.get(gene).setThreshold(thresh);
//		}
//
//	}















//	//////// run on lenient mode only
//	public void lenientOnly ( Map<String, GeneInfo> genes) {
//
//		// extract number of SNPs in gwas file
//		lenient(genes);
//
//		// correct threshold and add value to hash
//		for (String gene : genes.keySet()) {
//
//			// check whether gwas file or kgp has less SNPs
//			// and calculate threshold by lesser SNPs
//			Integer indep = genes.get(gene).getIndepSNPs(); 
//			Integer gwas = genes.get(gene).getGwasSNPs();
//
//			// to avoid infinity failure, if denominator = 0 set to 1
//			if (indep == 0) {
//				indep = 1;
//			}
//
//			if (gwas == 0) {
//				gwas = 1;
//			}
//
//			thresh = basePval / Math.min(indep, gwas);
//			genes.get(gene).setThreshold(thresh);
//		}
//	}







	////////////
	//////// lenient option

//	public void lenient(Map<String, GeneInfo> genes) {
//
//
//		// for each gene extract number of snps in gwas file and add to genes hash
//		for (String gene : genes.keySet()) {
//			int gwasSNPs = 0;
//			// check if there are entries for gwas snps else set 0
//			if (!(gwasData.getGwasSnps().get(gene) == null)) {
//				gwasSNPs = gwasData.getGwasSnps().get(gene).size();
//				}
//			genes.get(gene).setGwasSNPs(gwasSNPs);
//		}
//	}






	/////////////
	//////// getter
	



	public Double getThresh() {
		return thresh;
	}
	
	

	
	
	
}
