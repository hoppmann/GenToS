package de.gentos.geneSet.main;

import de.gentos.geneSet.initialize.InitializeListsMain;
import de.gentos.geneSet.initialize.data.QueryList;
import de.gentos.geneSet.initialize.options.GetListOptions;
import de.gentos.geneSet.lookup.LookupMain;
import de.gentos.geneSet.lookup.ResamplingMain;

public class ListsMain {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	GetListOptions options;
	InitializeListsMain init; 
	QueryList queryLists;



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

		init = new InitializeListsMain(args);
		options = init.getOptions();


		// for each query input list run program
		for (QueryList queryList : init.getQueryLists()){


			///////////////////////////
			//////// calculate enrichment in different lists  

			// for each list check enrichment with query gene list
			LookupMain lookup = new LookupMain(init, queryList);
			


			///////////////////////////
			//////// random repeat for empirical pVal estimation
			new ResamplingMain(options, init, queryList, lookup.getAllScores());





		}



		// close log file
		init.getLog().writeOutFile("\n######## GenToS successuflly finished.\n");
		init.getLog().closeFile();


	}





	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
}
