package de.gentos.geneSet.main;

import java.io.File;
import java.util.HashMap;
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

public class GeneSetMain {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	GetGeneSetOptions options;
	InitializeGeneSetMain init;
	InputList inputLists;
	Map<String, InfoData> infoMap;

	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	/////////////////////////
	//////// methods ////////
	/////////////////////////

	public void runLists(String[] args) {

		// init info map for later use to produce info file
		infoMap = new HashMap<>();

		///////////////////////////
		//////// initialize program

		// getting command line options
		// prepare log file
		// read in resource lists
		// read in query gene list

		init = new InitializeGeneSetMain(args);
		options = init.getOptions();

		// for each query input list run program
		for (InputList curInputList : init.getInputLists()) {

			///////////////////////////
			//////// calculate enrichment in different lists

			// create object to save data generated the processing the current
			// input
			String curInputListName = curInputList.getListName();
			RunData runData = new RunData(init, curInputList.getQueryGenes().size());
			runData.setCurListName(curInputListName);

			// print info which input list is processed
			HandleFiles log = init.getLog();
			log.writeOutFile("\n\n################ \n######## " + new File(curInputList.getListPath()).getName());

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

		//////// write info file
		new WriteInfoFile().writeInfo(init, infoMap);

		// close log file
		init.getLog().writeOutFile("\n######## GenToS successuflly finished.\n");
		init.getLog().closeFile();

	}

	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
}
