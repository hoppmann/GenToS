package de.gentos.geneSet.main;

import java.io.File;

import de.gentos.geneSet.initialize.InitializeGeneSetMain;
import de.gentos.geneSet.initialize.data.InputList;
import de.gentos.geneSet.initialize.data.RunData;
import de.gentos.geneSet.initialize.options.GetGeneSetOptions;
import de.gentos.geneSet.lookup.LookupMain;
import de.gentos.geneSet.lookup.ResamplingMain;
import de.gentos.geneSet.lookup.WriteInfoFile;
import de.gentos.general.files.HandleFiles;

public class GeneSetMain {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	GetGeneSetOptions options;
	InitializeGeneSetMain init; 
	InputList inputLists;



	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////




	/////////////////////////
	//////// methods ////////
	/////////////////////////


	public void runLists(String[] args) {




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

			// init new data entry in dataMap
			init.getDataMap().put(inputList.getListName(), new RunData(init, inputList.getQueryGenes().size()));
			
			// print info which input list is processed
			HandleFiles log = init.getLog();
			log.writeOutFile("\n\n################ \n######## " + new File(inputList.getListPath()).getName() );
			
			// for each list check enrichment with query gene list
			LookupMain lookup = new LookupMain(init, inputList);
			


			///////////////////////////
			//////// random repeat for empirical pVal estimation
			new ResamplingMain(options, init, inputList, lookup.getAllScores());


			//////// write info file
			new WriteInfoFile(init);



		}



		// close log file
		init.getLog().writeOutFile("\n######## GenToS successuflly finished.\n");
		init.getLog().closeFile();


	}





	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
}
