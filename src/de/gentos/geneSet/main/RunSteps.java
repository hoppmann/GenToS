package de.gentos.geneSet.main;

import java.io.File;
import java.util.Map;

import de.gentos.geneSet.initialize.InitializeGeneSetMain;
import de.gentos.geneSet.initialize.data.InfoData;
import de.gentos.geneSet.initialize.data.InputList;
import de.gentos.geneSet.initialize.data.RunData;
import de.gentos.geneSet.initialize.options.GetGeneSetOptions;
import de.gentos.geneSet.lookup.LookupMain;
import de.gentos.geneSet.lookup.ResamplingMain;
import de.gentos.geneSet.writeResults.WriteInfoFile;
import de.gentos.geneSet.writeResults.WriteResults;
import de.gentos.general.files.HandleFiles;

public class RunSteps implements Runnable{
	///////////////////////////
	//////// variables ////////
	///////////////////////////

	private GetGeneSetOptions options;
	private InitializeGeneSetMain init;
	private Map<String, InfoData> infoMap;
	private InputList curInputList;
	private String curInputListName;
	private HandleFiles log;
	
	
	
	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////
	
	public RunSteps(InitializeGeneSetMain init, InputList curInputList, Map<String, InfoData> infoMap) {
		
		this.init = init;
		this.options = init.getOptions();
		this.curInputList = curInputList;
		this.infoMap = infoMap;
		this.log = init.getLog();
	}
	
	
	
	/////////////////////////
	//////// methods ////////
	/////////////////////////

	@Override
	public void run() {
		

		///////////////////////////
		//////// calculate enrichment in different lists

		// create object to save data generated the processing the current
		// input
		
		curInputListName = curInputList.getListName();

		if (options.getThreads() != 1){
			log.writeOutFile("Calculating: " + curInputListName);
		}
		
		
		RunData runData = new RunData(init, curInputList.getQueryGenes().size());
		runData.setCurListName(curInputListName);

		// print info which input list is processed
		HandleFiles log = init.getLog();
		if (options.getThreads() == 1 || options.isVerbose()){
			log.writeOutFile("\n\n################ \n######## " + new File(curInputList.getListPath()).getName());
		}
		// for each list check enrichment with query gene list
		new LookupMain(init, curInputList, runData);





		///////////////////////////
		//////// random repeat for empirical pVal estimation
		/*
		 * check if input list has any enriched resources if so do
		 * resampling and write results this avoids unnecessary resampling
		 * iterations and saves time results files without resampling would
		 * be empty thus they are skipped General info still will be written
		 * in Info file
		 */

		if (runData.getNumberEnrichedResources() >= options.getMinEnrichement()) {
			// run random sampling for empirical pVal estimation
			new ResamplingMain(options, init, curInputList, runData);

			//////// write results file
			new WriteResults(init, runData, curInputList);

		}

		//////// collect data for info file
		new WriteInfoFile().collectData(runData, curInputListName, infoMap, curInputList, init);
	}
	
	
	
	
	
	
	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
}
