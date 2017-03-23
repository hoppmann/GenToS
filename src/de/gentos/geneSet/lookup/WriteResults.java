package de.gentos.geneSet.lookup;

import java.io.File;
import java.util.LinkedList;
import java.util.Map;

import de.gentos.geneSet.initialize.InitializeGeneSetMain;
import de.gentos.geneSet.initialize.data.RunData;
import de.gentos.geneSet.initialize.options.GetGeneSetOptions;
import de.gentos.general.files.HandleFiles;

public class WriteResults {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	private GetGeneSetOptions options;
	private InitializeGeneSetMain init;
	private Map<String, RunData> data;

	
	
	
	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	
	public WriteResults(InitializeGeneSetMain init) {
		this.init = init;
		this.options = init.getOptions();
		this.data = init.getDataMap();
	
	
	
	
	}
	
	
	
	/////////////////////////
	//////// methods ////////
	/////////////////////////

	
	public void write(String outName, String curGene) {
		
		// open file for writing
		HandleFiles out = new HandleFiles();
		out.openWriter(options.getOutDir() + File.separator + outName);
		
		
		for (String curGene : )
		
		
		
	}
	
	
	
	
	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
	
	
//	//// prepare out file header
//	// prepare summary of enriched Lists 
//	
//	int numberEnrichedLists = init.getDataMap().get(curInputList).getNumberEnrichedLists();
//	LinkedList<String> enrichedLists = init.getDataMap().get(curInputList).getEnrichedLists();
//
//	resultOut.writeFile("# Number of enriched lists: " + numberEnrichedLists);
//	for (String curEnrichedList : enrichedLists){
//		Boolean isSorted = init.getResources().get(curEnrichedList).isSorted();
//		
//		if (isSorted) {
//			resultOut.writeFile("# " + curEnrichedList + "\tsorted");
//		} else {
//			resultOut.writeFile("# " + curEnrichedList + "\tnot sorted");
//		}
//		
//	
//	}
//	
//	// prepare values definitions
//	resultOut.writeFile("#gene\tp-value\tscore [-log10]");
//
//	// print out values
//	for (String curGene : sortedGeneList.keySet()){
//		double pval = originalScores.get(curGene).getEmpiricalPVal();
//		double score = originalScores.get(curGene).getLogCumScore();
//		
//		resultOut.writeFile(curGene + "\t" + pval + "\t" + score);
//		
//	}
//	
//	// close result file
//	resultOut.closeFile();
//}

	
	
}
