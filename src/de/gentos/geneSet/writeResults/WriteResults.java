package de.gentos.geneSet.writeResults;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

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
	private RunData runData;



	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////


	public WriteResults(InitializeGeneSetMain init, RunData runData) {

		// retrieve data
		this.init = init;
		this.options = init.getOptions();
		this.runData = runData;

		// write result
		write();

	}



	/////////////////////////
	//////// methods ////////
	/////////////////////////

	// write outfile

	private void write() {

		// prepare out file name
		String outDir = options.getOutDir();
		String sep = File.separator;
		String outFileName = outDir + sep + runData.getCurListName();
		System.out.println(outFileName);


		// out file for writing 
		HandleFiles resultOut = new HandleFiles();
		resultOut.openWriter(outFileName);


		///////////////////////
		//////// prepare header
		int numberEnrichedLists = runData.getNumberEnrichedResources();
		LinkedList<String> enrichedLists = runData.getEnrichedResources();


		//// write out number of enriched lists, name of enriched list and if sorted or not
		resultOut.writeFile("# Number of enriched lists: " + numberEnrichedLists);
		for (String curEnrichedList : enrichedLists){
			Boolean isSorted = init.getResources().get(curEnrichedList).isSorted();

			if (isSorted) {
				resultOut.writeFile("# " + curEnrichedList + "\tsorted");
			} else {
				resultOut.writeFile("# " + curEnrichedList + "\tnot sorted");
			}
		}

		// prepare final header line
		String finalHeader = "gene\tpVal\t#enrichedResources\t#ofAllResources";
		
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
		Map<String, Double> sortedEmpiricalPvalList = sortByValue(runData.getEmpiricalPval());		

		for (String curGene : sortedEmpiricalPvalList.keySet()){


			///////////////
			//// Collect line entries

			// String array containing all entries for out line
			List<String> outLine = new LinkedList<>();

			// add gene name to line 
			outLine.add(curGene);

			// list of values needed for output (besides curgene)
			// get empirical pValue
			double empPVal = sortedEmpiricalPvalList.get(curGene);
			outLine.add(Double.toString(empPVal));

			// get number of enriched resources
			int numbEnrichedResources = runData.getNumberEnrichedResources();
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













	////////////
	//////// sort hash according to the values
	public Map<String, Double> sortByValue(Map<String, Double> map) {
		List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(map.entrySet());


		// sort by values
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {

			public int compare(Map.Entry<String, Double> m1, Map.Entry<String, Double> m2) {
				return (m2.getValue()).compareTo(m1.getValue());
			}
		});

		// reverse sort order to start with smallest
		List<Map.Entry<String, Double>> reverseList = Lists.reverse(list);

		// transfer sorted list to linked hash map
		Map<String, Double> result = new LinkedHashMap<String, Double>();
		for (Map.Entry<String, Double> entry : reverseList) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}









	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////


}
