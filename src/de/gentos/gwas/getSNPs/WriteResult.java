package de.gentos.gwas.getSNPs;

import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.io.FilenameUtils;

import de.gentos.general.files.HandleFiles;
import de.gentos.general.files.ReadInGeneDB;
import de.gentos.gwas.initialize.InitializeGwasMain;
import de.gentos.gwas.initialize.data.GeneInfo;
import de.gentos.gwas.initialize.data.GeneInfo.ROI;
import de.gentos.gwas.initialize.data.SnpLine;
import de.gentos.gwas.initialize.options.GetGwasOptions;

public class WriteResult {

	//////////////////////
	//////// set variables

	Map<String, GeneInfo> geneQueryList;
	HandleFiles result;
	GetGwasOptions options;
	HandleFiles csv;
	InitializeGwasMain init;
	CommandLine cmd;
	String currentQueryGeneListName;
	ReadInGeneDB readGenes;


	/////////////////////////////
	//////// Constructor ////////
	/////////////////////////////
	

	public WriteResult(Map<String, GeneInfo> geneQueryList, String tableName, String pathSNP, 
			HandleFiles result, GetGwasOptions options, String csvDir, InitializeGwasMain init, 
			String currentQueryGeneListName) {

		this.geneQueryList = geneQueryList;
		this.result = result;
		this.init = init;
		options = init.getGwasOptions();
		cmd = options.getCmd();
		this.currentQueryGeneListName = currentQueryGeneListName;
		this.readGenes = init.getReadGenes();

		// create Strings for file names
		String dbName = FilenameUtils.getBaseName(pathSNP);
		String filePath = csvDir + System.getProperty("file.separator") + dbName + "_"+ tableName + "_"  + currentQueryGeneListName + ".csv"; 

		// open csv file
		csv = new HandleFiles();
		csv.openWriter(filePath);
		
	}




	/////////////////////////
	//////// methods ////////
	/////////////////////////
	

	public void write() {

		/////////////////////
		//////// prepare header
		
		// write header for result file DBname and tableName
		String outLine = "######## " + currentQueryGeneListName + " ########";
		String outLine2 = null;
		for (int i = 0; i < outLine.length(); i++){
			if (outLine2 == null) {
				outLine2 = "#";
			} else {
				outLine2 = outLine2 + "#";
			}
		}
		result.writeFile(outLine2 + System.lineSeparator() + outLine + System.lineSeparator() + outLine2 + System.lineSeparator());

		
		
		///////////////////
		//////// prepare result file
		
		//////// retrieve plenty thresh info for later use in result file
		Double plentyThresh = null;
		if (!cmd.hasOption("FDR")) {
			if (!cmd.hasOption("plenty")) {
				if (!cmd.hasOption("fixThresh")){
					int totalSNPs = sumUp(geneQueryList);
					plentyThresh = (0.05 / totalSNPs);
				}
			}
		}
		

		
		
		// for each gene write results in resultFile
		for (String curGene : geneQueryList.keySet()) {

			// write in result file gene name as header and some info

			/* 
			 * check if lookup is gene based or different ROIs, not gene clustered,
			 * prepare header information accordingly
			 * 
			 */

			// if no gene name given mak header chr start-stop 
			if (! geneQueryList.get(curGene).isHasGeneName()) {

				// retrieve chr, start and stop from ROI
				String chr = "chr" + geneQueryList.get(curGene).getRois().getFirst().getChr();
				Integer start = geneQueryList.get(curGene).getRois().getFirst().getStart();
				Integer stop = geneQueryList.get(curGene).getRois().getFirst().getStop();
				result.writeFile("######## " + chr + " " + start + "-" + stop + " ########");


				// if gene name is given make header gene name
			} else {
				result.writeFile("######## " + curGene + " ########");
				// store chr, start and ending for each roi
				result.writeFile("chr\tstart-stop");
				for (ROI curRoi : geneQueryList.get(curGene).getRois()) {
					String line = curRoi.getChr() + "\t" + curRoi.getStart() + "-" + curRoi.getStop();
					result.writeFile(line);
				}
			}

			
			
			
			
			// if bonferroni option was chosen
			// write out the number of independent SNPs (depending if lenient option chosen or not with additional info)
			if (init.getGwasOptions().getMethod().equals("bonferroni")) {
				int indepSnps =  geneQueryList.get(curGene).getIndepSNPs(); 
				result.writeFile("Independet SNPs: " + indepSnps );
			}



			// write threshold information in result file
			// write out threshold
			Double thresh = geneQueryList.get(curGene).getThreshold(); 
			result.writeFile("Threshold: " + String.format("%6.2e", thresh));
			if (!cmd.hasOption("FDR") && !cmd.hasOption("fixThresh")) {


				// write out what threshold would be if plenty option would have been chosen
				if (!cmd.hasOption("plenty")) {
					result.writeFile("Threshold with plenty option: " + String.format("%6.2e", plentyThresh));
				} 
			}
			

			
			
			
			
			
			// check if there are results for gene. If so write in result AND csv
			// else write to result file low SNP and info only
			// if no information available at all make statement
			if (geneQueryList.get(curGene).isHasHit()) {

				// write gene name as header in file
				csv.writeFile("######## " + curGene + " ########");

				// write header information in csv
				String lineOut = createLine(init.getGwasData().getHeader());
				
				// write prepared string to output file
				csv.writeFile(lineOut);
				result.writeFile(lineOut);


				// for each SNP write information in csv
				for (SnpLine snp : geneQueryList.get(curGene).getSnpHits()) {


					// form string for out file
					lineOut = createLine(snp.formOutput());

					// write prepared string to output file
					csv.writeFile(lineOut);
					result.writeFile(lineOut);

				}

				
				// add emtpy line 
				csv.writeFile("");
				result.writeFile("");

			} else {

				// check if no information at all
				if ((geneQueryList.get(curGene).getLowPvalSNP() != null)) {

					// if no hit for gene save lowest pval in region
					String rsID = geneQueryList.get(curGene).getLowPvalSNP().getRsid(); 
					Double pval = geneQueryList.get(curGene).getLowPvalSNP().getpValue(); 
					result.writeFile(rsID + " has lowest pval in region with " + String.format("%6.2e", pval));
					result.writeFile("");


				}  else {

					result.writeFile("No SNPs in the region of " + curGene);
					result.writeFile("");
				}
			}

		}

		// close csv file
		csv.closeFile();

	}




	public String createLine (LinkedList<String> list) {

		// create String for result (saved linked list no nice layout)
		String lineOut = null;
		for (String entry : list) {
			if (lineOut == null ){
				lineOut = entry;
			} else {
				lineOut = lineOut + "\t" + entry;
			}
		}

		return lineOut;
	}


	private int sumUp(Map<String, GeneInfo> queryGenes) {

		// init integer for sum
		int totalGenes = 0;

		for (String gene : queryGenes.keySet()) {
			// sum up if leninent is chosen
			if (cmd.hasOption("lenient")) {
				int indep = queryGenes.get(gene).getIndepSNPs(); 
				int lenient = queryGenes.get(gene).getGwasSNPs(); 


				// choose smaller value
				if (indep < lenient ) {
					totalGenes += indep;
				} else {
					totalGenes += lenient;
				}

				// sum up if not leninent chosen
			} else {
				// sum up 
				totalGenes += Integer.valueOf(geneQueryList.get(gene).getIndepSNPs());
			}
		}


		return totalGenes;
	}
}
