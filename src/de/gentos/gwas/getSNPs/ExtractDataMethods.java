package de.gentos.gwas.getSNPs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.gentos.general.files.Database;
import de.gentos.general.files.HandleFiles;
import de.gentos.general.files.ReadInGeneDB;
import de.gentos.gwas.initialize.InitializeGwasMain;
import de.gentos.gwas.initialize.ReadInGwasData;
import de.gentos.gwas.initialize.data.GeneInfo;
import de.gentos.gwas.initialize.data.GeneInfo.ROI;
import de.gentos.gwas.initialize.data.SnpLine;
import de.gentos.gwas.initialize.options.GetGwasOptions;
import de.gentos.gwas.threshold.CreateThresh;


/* in this class several methods are written to extract the SNPs from the GWAS file
 * methods
 * extract independent SNPs
 * check that current gene is supported
 * extract SNPs by threshold
 * identify lowest pval SNPs
*/

public class ExtractDataMethods {



	//////////////////////
	//////// set variables
	ReadInGeneDB geneDB; 
	ReadInGwasData data;
	InitializeGwasMain init;
	HandleFiles log;
	GetGwasOptions options;
	String colPVal;
	String colChr;
	String colPos;
	String colrsID;
	boolean verbose = true;


	/////////////
	//////// setter

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}





	////////////////////
	//////// constructor


	public ExtractDataMethods(InitializeGwasMain init) {

		// initialize variables
		this.init = init;
		this.log = init.getLog();
		this.options = init.getGwasOptions();
		colPVal = options.getColPval();
		colPos = options.getColPos();
		colChr = options.getColChr();
		colrsID = options.getColrsID();
		data = init.getGwasData();
		geneDB = init.getReadGenes();

	}







	/////////////////////////
	//////// methods ////////
	/////////////////////////


	///////////////
	//// get gene positions 
	
	public void getGenePositions (Map<String, GeneInfo> geneList, Map<String, GeneInfo> checkedGeneList) {
		
		
		// For each gene in gene list extract positions depending on the gene DB
		
		for (String curGene : geneList.keySet()) {
			
			// check that gene is supported, meaning there is position data available for the gene,
			// if not print out warning and go to next iteration
			if ( checkGene(curGene) == true) {
				
				// Instantiate a new entry for the gene with start stop information
				checkedGeneList.put(curGene, new GeneInfo());
				
				// add the chr and regions of interest to the gene information map of checked genes
				checkedGeneList.get(curGene).setRois(geneDB.getGeneListMap().get(curGene).getRois());
			} 
		}
	}
	
	
	
	
	
		
	//////////////////////
	//////// check that current gene is supported
	
	public boolean checkGene (String currentGene) {
	
	// init check variable
	boolean check = true;
	
	// check that gene is supported if not print out warning and go to next iteration
	if (!geneDB.getGeneListMap().containsKey(currentGene)) {
		if (geneDB.getNonGoodGenes().containsKey(currentGene)){

			// if gene is located on not supported chr write log that not supported
			String chr = geneDB.getNonGoodGenes().get(currentGene).toString();

			if (verbose == true ){ 
				log.writeOutFile("## WARNING: " + chr + " not supported. " +
						currentGene +" scipped in further calculation.");
			}
		} else {

			// if no reason why no gene information write out in log 
			log.writeOutFile("## WARNING: No information found for " + currentGene + 
					", scipped in further calculation.");
		}
		// jump to next iteration step
		check = false;
	}

	return check;
	}
	
	
	
	
	
	///////////////////////////
	//////// extract number of independent SNPs and add to hash

	public void extractIndep(Map<String, GeneInfo> geneList) {

		// connect to indepDB
		String indepDBPath = options.getIndepDB();
		Database dbIndep = new Database(indepDBPath, log);


		//for each gene in query list extract the number of independent SNPs according to the ROIs available

		for (String currentGene : geneList.keySet()) {

			// for each roi in current gene extract all independent SNPs
			for (ROI curRoi : geneList.get(currentGene).getRois()) {

				Integer start = curRoi.getStart();
				Integer stop = curRoi.getStop();
				Integer chr = curRoi.getChr();

				
				// create query to get independent SNPs and execute
				// add up found indep SNPs for all ROIs
				
				String query = "select count(rsid) from chr" + chr + " where pos > " + start + " and pos < " + stop;
				ResultSet result = dbIndep.select(query);
				
				try {
					// retrieve number of independent SNPs
					Integer indepSnps = result.getInt(1);
					geneList.get(currentGene).sumUpIndepSnps(indepSnps);
				} catch (SQLException e1) {
					log.writeError("An error occured while getting gene informations from the indep database.");
					System.exit(1);
				}
			}
		}
	}


	
	
	
	
	
	
	
	
	
	////////////////////
	//// calculate threshold depending on the number of independent SNPs 
	// found in the region of interest
	
	public void calculateThresh (ExtractDataMethods extract, ReadInGwasData gwasData, Map<String, GeneInfo> queryGenesChecked, int currentDbSnp, String curentListName){

		// create thresh according to method chosen
		
		CreateThresh correction = new CreateThresh(init, gwasData);
		correction.choose(extract, queryGenesChecked);
		
		// save threshold for db and corresponding list
		options.getGwasDbs().get(currentDbSnp).addToMap(curentListName, correction.getThresh());
		
	}

	
	
	
	
	
	
	
	
	

	///////////////////
	//////// extract SNPs by threshold
	public void extractSNPs(ReadInGwasData gwasData, Map<String, GeneInfo> curQueryGeneList) {


		// for each gene in query list check if GWAS file has pval < thresh if so remember gene
		for (String curGene : curQueryGeneList.keySet()) {

			// set / define variables
			Double thresh = curQueryGeneList.get(curGene).getThreshold();
			double oldPval = 1;
			SnpLine lowestPvalSNP = null;
			boolean hasHits = false;

			
			
			

			//////// for each SNP: if pval < threshold save data to hash
			
			// check that informations are available for the gene of interest
			if (!(gwasData.getGwasSnps().get(curGene) == null)) {

				
				for (SnpLine currentSNP : gwasData.getGwasSnps().get(curGene)) {
					Double pval = currentSNP.getpValue();
					// if pval smaller thresh save GeneInfo object
					if ( pval < thresh) {
						// save snp to geneListInfo object
						curQueryGeneList.get(curGene).addSnpLine(currentSNP);

						// mark that gene has hits
						hasHits = true;


						// if pval not smaller thresh save thresh to determine smallest pval in region
					} else if (pval < oldPval) {
						oldPval = pval;
						lowestPvalSNP = currentSNP;
					}
				}

				// if no SNP found save rsid and pval from SNP with lowest pval
				if (hasHits == false) {
					curQueryGeneList.get(curGene).setHasHit(hasHits);
					curQueryGeneList.get(curGene).setLowPvalSNP(lowestPvalSNP);
				} else {
					curQueryGeneList.get(curGene).setHasHit(hasHits);
				}
			}
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	///////////////////
	//// get lowest p-value within a gene
	public List<Double> extractLowestPvalPerGene(ReadInGwasData gwasDataReference) {

		
		// set variables
		List<Double> lowestPvalPerGene = new LinkedList<>();
		List<Double> allPval;

		// for each Human gene if gene is present in gwas
		// extract lowest pval from gwas file and save to list


		for (String gene : init.getReadGenes().getAllGeneNames()){

			// reset list of collection of all pval in gene
			allPval = new ArrayList<>();
			
			// check that gene is in gwas file (key is available)
			if(gwasDataReference.getGwasSnps().containsKey(gene)){
				List<SnpLine> snps = gwasDataReference.getGwasSnps().get(gene);
				for (SnpLine snp : snps){
					allPval.add(snp.getpValue());
				}
			}
			
			

			// sort list of pval and save smallest is List
			Collections.sort(allPval);
			if (!allPval.isEmpty()){
				lowestPvalPerGene.add(allPval.get(0));
			}
		}
		
		return lowestPvalPerGene;
	}




}
