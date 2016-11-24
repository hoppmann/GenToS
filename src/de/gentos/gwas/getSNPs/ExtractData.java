package de.gentos.gwas.getSNPs;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.gentos.general.options.gwas.GetGwasOptions;
import de.gentos.gwas.initialize.InitializeMain;
import de.gentos.gwas.initialize.ReadInGenes;
import de.gentos.gwas.initialize.ReadInGwasData;
import de.gentos.gwas.initialize.data.GeneListInfo;
import de.gentos.gwas.initialize.data.SnpLine;
import de.gentos.gwas.main.HandleFile;


/* in this class several methods are written to extract the SNPs from the GWAS file
 * methods
 * extract independent SNPs
 * check that current gene is supported
 * extract SNPs by threshold
 * identify lowest pval SNPs
*/

public class ExtractData {



	//////////////////////
	//////// set variables
	ReadInGenes readGenes; 
	ReadInGwasData data;
	InitializeMain init;
	HandleFile log;
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


	public ExtractData(InitializeMain init) {

		// initialize variables
		this.init = init;
		this.log = init.getLog();
		this.options = init.getGwasOptions();
		colPVal = options.getColPval();
		colPos = options.getColPos();
		colChr = options.getColChr();
		colrsID = options.getColrsID();
		data = init.getGwasData();
		readGenes = init.getReadGenes();

	}







	////////////////
	//////// methods


	///////
	//// extract number of independent SNPs and add to hash

	public void extractIndep(Map<String, GeneListInfo> genes, LinkedList<String> currentGeneList) throws Exception {

		// connect to indepDB
		String indepDBPath = options.getIndepDB();
		Database dbIndep = new Database(indepDBPath, init);


		// for each gene in query get gene position if no position available exclude from list
		// then query indepDB for # independent SNPs
		for (String currentGene : currentGeneList) {

			// check that gene is supported if not print out warning and go to next iteration
			if ( checkGene(currentGene, genes) == false) {
				continue;
			}

			// if gene supported query indepDB for independent SNPs
			Integer chr = readGenes.getGeneInfo().get(currentGene).getChr();
			Integer start = readGenes.getGeneInfo().get(currentGene).getStart();
			Integer stop = readGenes.getGeneInfo().get(currentGene).getStop();


			// create query and execute
			String query = "select count(rsid) from chr" + chr + " where pos > " + start + " and pos < " + stop;
			ResultSet result = dbIndep.select(query);
			
			
			// save number of independent SNPs to hash
			genes.get(currentGene).setIndepSNPs(result.getInt(1));

		}

		log.writeOutFile("Independent SNPs extracted from " + indepDBPath +".");

	}



	//////////////////////
	//////// check that current gene is supported
	
	public boolean checkGene (String currentGene, Map<String, GeneListInfo> genes) {

		// init check variable
		boolean check = true;

		// check that gene is supported if not print out warning and go to next iteration
		if (!readGenes.getGeneInfo().containsKey(currentGene)) {
			if (readGenes.getNonGoodGenes().containsKey(currentGene)){
				// if gene is located on not supported chr write log that not supported
				String chr = readGenes.getNonGoodGenes().get(currentGene).toString();

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

		if (check == true) {
			// remember gene as to continue useing in query list
			genes.put(currentGene, new GeneListInfo());
		}


		return check;
	}



	////////
	//// extract SNPs by threshold
	public void extractSNPs(ReadInGwasData gwasData, Map<String, GeneListInfo> currentGenes) {


		// for each gene in mouse list check if gwas file has pval < thresh if so remember gene
		for (String gene : currentGenes.keySet()) {

			// set / define variables
			Double thresh = currentGenes.get(gene).getThreshold(); 
			double oldPval = 1;
			SnpLine lowestPvalSNP = null;
			boolean hasHits = false;

			
			// for each SNP: if pval < threshold save data to hash
			if (!(gwasData.getGeneSNP().get(gene) == null)) {

				for (SnpLine currentSNP : gwasData.getGeneSNP().get(gene)) {
					Double pval = currentSNP.getpValue();

					// if pval smaller thresh save GeneInfo object
					if ( pval < thresh) {
						
						// save snp to geneListInfo object
						currentGenes.get(gene).addSnpLine(currentSNP);

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
					currentGenes.get(gene).setHasHit(hasHits);
					currentGenes.get(gene).setLowPvalSNP(lowestPvalSNP);
				} else {
					currentGenes.get(gene).setHasHit(hasHits);
				}
			}
		}
	}



	public List<Double> extractLowestPvalPerGene(ReadInGwasData gwasData) {

		// set variables
		List<Double> lowestPvalPerGene = new LinkedList<>();
		List<Double> allPval;

		// for each Human gene if gene is present in gwas
		//extract lowes pval from gwas file and save to list

		for (String gene : init.getReadGenes().getAllGeneNames()){
			// reset list of collection of all pval in gene
			allPval = new ArrayList<>();
			
			// check that gene is in gwas file (key is available)
			if(gwasData.getGeneSNP().containsKey(gene)){
				List<SnpLine> snps = gwasData.getGeneSNP().get(gene);
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
