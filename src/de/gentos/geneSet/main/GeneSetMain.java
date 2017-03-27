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
		for (InputList inputList : init.getInputLists()){


			///////////////////////////
			//////// calculate enrichment in different lists  

			// create object to save data generated the processing the current input
			String curInputList = inputList.getListName();
			RunData runData = new RunData(init, inputList.getQueryGenes().size());
			runData.setCurListName(curInputList);
			

			// print info which input list is processed
			HandleFiles log = init.getLog();
			log.writeOutFile("\n\n################ \n######## " + new File(inputList.getListPath()).getName() );
			
			
			// for each list check enrichment with query gene list
			new LookupMain(init, inputList, runData);

			
			///////////////////////////
			//////// random repeat for empirical pVal estimation
			new ResamplingMain(options, init, inputList, runData);


			
			//////// write results file
			new WriteResults(init, runData);
			
			
			
			//////// collect data for info file
			new WriteInfoFile().collectData(runData, curInputList, infoMap);

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
