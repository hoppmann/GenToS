package de.gentos.gwas.getSNPs;

import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.io.FilenameUtils;

import de.gentos.gwas.initialize.InitializeMain;
import de.gentos.gwas.initialize.ReadInGenes;
import de.gentos.gwas.initialize.data.GeneListInfo;
import de.gentos.gwas.initialize.data.SnpLine;
import de.gentos.gwas.initialize.options.GetOptions;
import de.gentos.gwas.main.HandleFile;

public class WriteResult {

	//////////////////////
	//////// set variables

	Map<String, GeneListInfo> geneQueryList;
	HandleFile result;
	GetOptions options;
	HandleFile csv;
	InitializeMain init;
	CommandLine cmd;
	String currentListName;
	ReadInGenes readGenes;


	////////////////////
	//////// Constructor

	public WriteResult(Map<String, GeneListInfo> geneQueryList, String tableName, String pathSNP, 
			HandleFile result, GetOptions options, String csvDir, InitializeMain init, String currentListName) {

		this.geneQueryList = geneQueryList;
		this.result = result;
		this.init = init;
		options = init.getOptions();
		cmd = options.getCmd();
		this.currentListName = currentListName;
		this.readGenes = init.getReadGenes();

		// create Strings for file names
		String dbName = FilenameUtils.getBaseName(pathSNP);
		String filePath = csvDir + System.getProperty("file.separator") + dbName + "_"+ tableName + "_"  + currentListName + ".csv"; 

		// open csv file
		csv = new HandleFile();
		csv.openWriter(filePath);

	}




	////////////////
	//////// methods

	public void write() {

		// write header for result file DBname and tableName
		String outLine = "######## " + currentListName + " ########";
		String outLine2 = null;
		for (int i = 0; i < outLine.length(); i++){
			if (outLine2 == null) {
				outLine2 = "#";
			} else {
				outLine2 = outLine2 + "#";
			}
		}
		result.writeFile(outLine2 + System.lineSeparator() + outLine + System.lineSeparator() + outLine2 + System.lineSeparator());


		// retrieve plenty thresh info for later use in result file
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
		for (String gene : geneQueryList.keySet()) {

			// write in result file gene header and some info
			result.writeFile("######## " + gene + " ########");

			// chr, start and ending
			result.writeFile( "Chromosome: " +  readGenes.getGeneInfo().get(gene).getChr());
			result.writeFile("Gene start: " + readGenes.getGeneInfo().get(gene).getStart());
			result.writeFile("Gene end: " + readGenes.getGeneInfo().get(gene).getStop());




			// if bonferoni option chosen
			// write out number of independent SNPs (depending if lenient option chosen or not with additional info)
			if (init.getOptions().getMethod().equals("bonferoni")) {

				int indep = geneQueryList.get(gene).getIndepSNPs(); 
				int lenient = geneQueryList.get(gene).getGwasSNPs(); 

				if (cmd.hasOption("lenient")) {
					if (indep < lenient) {
						result.writeFile("Independent SNPs: " + indep + " (" +lenient + " SNPs in GWAS file)");
					} else {
						result.writeFile("SNPs in GWAS file: " + lenient + " (" + indep + " independent SNPs in 1kgp file.)");
					}
				} else {

					result.writeFile("Independet SNPs: " + indep );

				}
			}




			// write threshold information in result file
			// write out threshold
			Double thresh = geneQueryList.get(gene).getThreshold(); 
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
			if (geneQueryList.get(gene).isHasHit()) {

				// write gene name as header in file
				csv.writeFile("######## " + gene + " ########");

				// write header information in csv
				String lineOut = createLine(init.getGwasData().getHeader());
				// write prepared string to output file
				csv.writeFile(lineOut);
				result.writeFile(lineOut);



				// for each SNP write information in csv
				for (SnpLine snp : geneQueryList.get(gene).getSnpHits()) {


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
				if ((geneQueryList.get(gene).getLowPvalSNP() != null)) {

					// if no hit for gene save lowest pval in region
					String rsID = geneQueryList.get(gene).getLowPvalSNP().getRsid(); 
					Double pval = geneQueryList.get(gene).getLowPvalSNP().getpValue(); 
					result.writeFile(rsID + " has lowest pval in region with " + String.format("%6.2e", pval));
					result.writeFile("");


				}  else {

					result.writeFile("No SNPs in the region of " + gene);
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


	private int sumUp(Map<String, GeneListInfo> queryGenes) {

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
