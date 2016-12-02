package de.gentos.lists.main;

import de.gentos.lists.initialize.InitializeListsMain;
import de.gentos.lists.initialize.data.QueryList;
import de.gentos.lists.initialize.options.GetListOptions;
import de.gentos.lists.lookup.LookupMain;
import de.gentos.lists.lookup.ResamplingMain;

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
			new LookupMain(init, queryList);



			///////////////////////////
			//////// random repeat for empirical pVal estimation
			new ResamplingMain(options, init, queryList);





		}



		// close log file
		init.getLog().writeOutFile("\n######## GenToS successuflly finished.\n");
		init.getLog().closeFile();


	}





	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
}
