package de.gentos.gwas.threshold;

import java.util.Map;

import org.apache.commons.cli.CommandLine;

import de.gentos.general.files.HandleFiles;
import de.gentos.general.files.ReadInGeneDB;
import de.gentos.gwas.getSNPs.ExtractDataMethods;
import de.gentos.gwas.initialize.InitializeGwasMain;
import de.gentos.gwas.initialize.ReadInGwasData;
import de.gentos.gwas.initialize.data.GeneInfo;

public class CreateThresh {

	//////////////////////
	//////// Set variables
	InitializeGwasMain init;
	HandleFiles log;
	CommandLine cmd;
	Double thresh;
	String plenty;
	ReadInGwasData gwasData;
	ReadInGeneDB geneDB;
	GeneInfo geneInfo;




	/////////////////////
	//////// Constructor

	public CreateThresh(InitializeGwasMain init, ReadInGwasData gwasData) {
		this.init = init;
		this.log = init.getLog();
		this.cmd = init.getGwasOptions().getCmd();
		this.gwasData = gwasData;
		this.geneDB = init.getReadGenes();
	}




	////////////////
	//////// Methods

	// possible combinations
	//	plenty
	//	plenty & lenient
	//	lenient


	//	FDR


	// maximumEnrichment



	//	fixThresh










	public void choose(ExtractDataMethods extract, Map<String, GeneInfo> curQueryList) {

		////////////
		//////// bonferroni
		if (init.getGwasOptions().getMethod().equals("bonferroni")) {

			// remember method used to calculate threshold
			for (String curGene : curQueryList.keySet()) {
				
				//TODO check naming 
				curQueryList.get(curGene).setMethod("Bonferoni");
				
			}
			// Instantiate bonferroni correction
			Bonferroni bonfe = new Bonferroni(init, gwasData);

			////// run without further option
			if (!cmd.hasOption("plenty")) {

				// run bonferoni with not special correction
				bonfe.correctOnly(curQueryList);

				////// run plenty only
			} else if (cmd.hasOption("plenty")) {

				// run bonferoni with plenty option
				bonfe.plentyOnly(curQueryList);
			}

			// get threshold
			thresh = bonfe.getThresh();
			
		}

		
		


		////////////
		//////// FDR correction
		//////// by Benjamini-Hochberg
		if (init.getGwasOptions().getMethod().equals("FDR")) {

			// calculate FDR
			FalseDiscoveryRate FDR = new FalseDiscoveryRate(init);
			FDR.runFDR(curQueryList, gwasData);

			thresh = FDR.getThreshold(); 
			
		}
			
			
		
		
		
		
		////////////
		/////// Fix Thresh
		if (init.getGwasOptions().getMethod().equals("fixThresh")) {

			// use fix thresh
			// add threshold to each gene in hash
			thresh = init.getGwasOptions().getFixThresh();
			for (String gene : curQueryList.keySet()) {
				curQueryList.get(gene).setThreshold(thresh);
				curQueryList.get(gene).setMethod("fixThresh");
			}
		}

















		//		//////////////////
		//		//////// maxEnrichtment
		//		if ( init.getOptions().getMethod().equals("maxEnrichment")) {
		//
		//			// check that genes in querylist are supported
		//			for (String currentGene : queryGenes){
		//				extract.checkGene(currentGene, queryGenesChecked);
		//			}
		//			
		//			// estimate maximum enrichment threshold
		//			MaxEnrichment enrichment = new MaxEnrichment(init, readGenes, gwasData);
		//			enrichment.maximize(queryGenesChecked);
		//			
		//			
		//			
		//			System.exit(3234);
		//			
		//		}

















	}





	///////////////
	//////// getter



	public Double getThresh() {
		return thresh;
	}




}
