package de.gentos.geneSet.main;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.gentos.geneSet.initialize.InitializeGeneSetMain;
import de.gentos.geneSet.initialize.data.InfoData;
import de.gentos.geneSet.initialize.data.InputList;
import de.gentos.geneSet.initialize.options.GetGeneSetOptions;
import de.gentos.geneSet.writeResults.WriteInfoFile;

public class GeneSetMain extends Thread{
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
		infoMap = new ConcurrentHashMap<>();

		///////////////////////////
		//////// initialize program

		// getting command line options
		// prepare log file
		// read in resource lists
		// read in query gene list

		init = new InitializeGeneSetMain(args);
		options = init.getOptions();

		
		
		
		//////// make multi threading per input list
		// set maximum number of threads to be executed
		ExecutorService executor = Executors.newFixedThreadPool(options.getThreads());
		
		// for each query input list run program
		for (InputList curInputList : init.getInputLists()) {


			// create steps to run in parallel and execute
			Runnable task = new RunSteps(init, curInputList, infoMap);
			executor.execute(task);
			
		}
		
		// wait for all threads to finish 
		executor.shutdown();
		while (!executor.isTerminated()){
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
