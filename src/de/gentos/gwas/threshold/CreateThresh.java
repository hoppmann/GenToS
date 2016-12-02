package de.gentos.gwas.threshold;

import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.cli.CommandLine;

import de.gentos.general.files.HandleFiles;
import de.gentos.general.files.ReadInGenes;
import de.gentos.gwas.getSNPs.ExtractData;
import de.gentos.gwas.initialize.InitializeGwasMain;
import de.gentos.gwas.initialize.ReadInGwasData;
import de.gentos.gwas.initialize.data.GeneInfo;
import de.gentos.gwas.initialize.data.GeneListInfo;

public class CreateThresh {

	//////////////////////
	//////// Set variables
	InitializeGwasMain init;
	HandleFiles log;
	CommandLine cmd;
	Double thresh;
	String plenty;
	ReadInGwasData gwasData;
	ReadInGenes readGenes;
	GeneInfo geneInfo;




	/////////////////////
	//////// Constructor

	public CreateThresh(InitializeGwasMain init, ReadInGwasData gwasData) {
		this.init = init;
		this.log = init.getLog();
		this.cmd = init.getGwasOptions().getCmd();
		this.gwasData = gwasData;
		this.readGenes = init.getReadGenes();
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










	public void choose(ExtractData extract, LinkedList<String> queryGenes, Map<String, GeneListInfo> queryGenesChecked) {

		////////////
		//////// bonferoni
		if (init.getGwasOptions().getMethod().equals("bonferroni")) {


			// for each Gene get number of independent SNPs
			log.writeOutFile("For each gene extract independent SNPs.");
			try {
				extract.extractIndep(queryGenesChecked, queryGenes);
			} catch (Exception e) {
				log.writeError("An error occured while getting gene informations from the indep database.");
				System.exit(1);
			}

			// set remember method used to calculate threshold
			for (String gene : queryGenesChecked.keySet()) {
				queryGenesChecked.get(gene).setMethod("Bonferoni");
			}

			// instanciate bonferoni correction
			Bonferroni bonfe = new Bonferroni(init, gwasData);

			////// run without further option
			if (!cmd.hasOption("plenty")) {


				// run bonferoni with not special correction
				bonfe.correctOnly(queryGenesChecked);

				////// run plenty only
			} else if (cmd.hasOption("plenty")) {

				// run bonferoni with plenty option
				bonfe.plentyOnly(queryGenesChecked);
			}

			// get threshold
			thresh = bonfe.getThresh();
		}





		////////////
		//////// FDR correction
		//////// by Benjamini-Hochberg
		if (init.getGwasOptions().getMethod().equals("FDR")) {
			// for each gene in query list check if gene is supported if so save in corrected hash

			for (String currentGene : queryGenes){
				extract.checkGene(currentGene, queryGenesChecked);
			}

			// calculate FDR
			FalseDiscoveryRate FDR = new FalseDiscoveryRate(init);
			FDR.runFDR(queryGenesChecked, queryGenes, gwasData);

			thresh = FDR.getThreshold(); 

		}




		////////////
		/////// Fix Thresh
		if (init.getGwasOptions().getMethod().equals("fixThresh")) {

			// for each gene in query list check if gene is supported if so save in corrected hash
			for (String currentGene : queryGenes){
				extract.checkGene(currentGene, queryGenesChecked);
			}
			// use fix thresh
			// add threshold to each gene in hash
			thresh = init.getGwasOptions().getFixThresh();
			for (String gene : queryGenesChecked.keySet()) {
				queryGenesChecked.get(gene).setThreshold(thresh);
				queryGenesChecked.get(gene).setMethod("fixThresh");
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
