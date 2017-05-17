package de.gentos.geneSet.writeResults;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import de.gentos.geneSet.initialize.InitializeGeneSetMain;
import de.gentos.geneSet.initialize.data.InputList;
import de.gentos.geneSet.initialize.data.RunData;
import de.gentos.geneSet.initialize.options.GetGeneSetOptions;
import de.gentos.general.files.HandleFiles;
import de.gentos.general.misc.generalMethods;

public class WriteResults {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	private GetGeneSetOptions options;
	private InitializeGeneSetMain init;
	private RunData runData;
	private InputList curInputList;



	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////


	public WriteResults(InitializeGeneSetMain init, RunData runData, InputList curInputList) {

		// retrieve data
		this.init = init;
		this.options = init.getOptions();
		this.runData = runData;
		this.curInputList = curInputList;


		// create folder for result files
		String sep = File.separator;
		String outDir = options.getOutDir() + sep + "results";
		new File(outDir).mkdirs();


		// make log entry
		if (options.getThreads() == 1 || options.isVerbose()){
			init.getLog().writeOutFile("\n#### Writing result file!");
		}
		// write result
		write();

	}






	/////////////////////////
	//////// methods ////////
	/////////////////////////

	// write outfile

	private void write() {


		// prepare out file name
		String sep = File.separator;
		String outDir = options.getOutDir() + sep + "results";
		String outFileName = outDir + sep + runData.getCurListName();


		// out file for writing 
		HandleFiles resultOut = new HandleFiles();
		resultOut.openWriter(outFileName);


		///////////////////////
		//////// prepare header
		int numberEnrichedLists = runData.getNumberEnrichedResources();
		LinkedList<String> enrichedResources = runData.getEnrichedResources();


		//// write out number of enriched lists, name of enriched list and if sorted or not
		resultOut.writeFile("# Number of enriched lists: " + numberEnrichedLists);
		resultOut.writeFile("# Method for threshold definition: " + options.getThreshMethod());
		resultOut.writeFile("# Significant level alpha: " + options.getAlpha());
		resultOut.writeFile("# Threshold for significant genes: " + runData.getFinalThresh());
		resultOut.writeFile("");
		for (String curEnrichedResource : enrichedResources){

			// get length of current resource
			int lengthResource = init.getResources().get(curEnrichedResource).getGenes().size();

			// get info if resource is sorted
			Boolean isSorted = init.getResources().get(curEnrichedResource).isSorted();

			if (isSorted) {
				resultOut.writeFile("# " + curEnrichedResource + "\tsorted\t " + lengthResource + " genes");
			} else {
				resultOut.writeFile("# " + curEnrichedResource + "\tnot sorted\t " + lengthResource + " genes");
			}
		}


		// prepare final header line
		
		String finalHeader = "\ngene\tinInput\tsignificant\tpVal\t#enrichedResources\t#ofAllResources";

		// add name of enriched lists
		for (String curEnrichedResource : runData.getEnrichedResources()) {
			String resourceName = new File(curEnrichedResource).getName();
			finalHeader+="\t" + resourceName;
		}

		// write in out file
		resultOut.writeFile(finalHeader);







		//////////////////
		//////// main part




		///////////////
		//// get list informations

		// for each gene (in sorted empirical pval list) extract the postion of the gene in each enriched List
		Map<String, Double> sortedEmpiricalPvalList = new generalMethods().sortMapByValue(runData.getEmpiricalPval(), true);		

		for (String curGene : sortedEmpiricalPvalList.keySet()){


			///////////////
			//// Collect line entries

			// String array containing all entries for out line
			List<String> outLine = new LinkedList<>();

			// add gene name to line 
			outLine.add(curGene);


			// check if gene was in original input list if so flag it in results file
			if (curInputList.getQueryGenes().contains(curGene)) {
				outLine.add("X");
			} else {
				outLine.add("");
			}

			
			// mark if gene still lies within threshold
			double empPVal = sortedEmpiricalPvalList.get(curGene);
			if (empPVal <= runData.getFinalThresh()){
				outLine.add("true");
			} else {
				outLine.add("false");
			}

			

			// list of values needed for output (besides curgene)
			// get empirical pValue
			outLine.add(Double.toString(empPVal));






			// get number of enriched resources
			int numbEnrichedResources = runData.getGeneData().get(curGene).getNumberAllFoundResources(); 
			//runData.getNumberEnrichedResources();
			outLine.add(Integer.toString(numbEnrichedResources));

			// get total number of resources checkt for enrichment
			int totalNumberOfResources = init.getResources().size();
			outLine.add(Integer.toString(totalNumberOfResources));

			// get positions of genes in enriched lists 
			for (String curEnrichedResource : runData.getEnrichedResources()){
				String position = runData.getGeneData().get(curGene).getEnrichedResources().get(curEnrichedResource);
				outLine.add(position);
			}



			// Concat elements and write out line
			String completeLine = StringUtils.join(outLine, "\t");
			resultOut.writeFile(completeLine);

		}


		resultOut.closeFile();

	}







	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////


}
