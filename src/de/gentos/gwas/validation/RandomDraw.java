package de.gentos.gwas.validation;

import java.util.LinkedList;
import java.util.Random;

import com.google.common.collect.Multimap;

import de.gentos.gwas.initialize.InitializeMain;
import de.gentos.gwas.initialize.ReadInGenes;
import de.gentos.gwas.main.HandleFile;

public class RandomDraw {





	///////////////////
	//////// set variables

	private InitializeMain init;
	private HandleFile log;





	///////////////
	//////// constructor
	public RandomDraw(InitializeMain init) {
		this.init = init;
		this.log = init.getLog();



	}







	//////////////
	//////// Methods

	// 1. Iteration over random draw


	// draw random list of genes
	public void drawList(int length, int iterations, String key, Multimap<String, LinkedList<String>> allRandomLists) {

		
		// make log entry
		log.writeOutFile("Drawing random lists of genes for " + key);


		// extract list of all genes
		ReadInGenes genes = init.getReadGenes();


		// init random class
		//if seed option is chosen use value as seed
		Random rand = new Random();
		if (init.getGwasOptions().getCmd().hasOption("seed") ){
			long seed = init.getGwasOptions().getSeed();
			rand.setSeed(seed);
		}
		

		// draw random lists depending on defined iterations
		for (int iter = 0 ; iter < iterations; iter++) {

			// create list to save genes to
			LinkedList<String> randomList = new LinkedList<>();

			// draw randomly according to list length
			int counter = 0;
			while (counter <= length){
				int randInt = rand.nextInt(genes.getAllGeneNames().size() - 1);

				// check that gene isn't in list yet
				if (!randomList.contains(genes.getAllGeneNames().get(randInt))){
					randomList.add(genes.getAllGeneNames().get(randInt));
					counter++;
				} 
			}
			
			// add random list to hash 
			allRandomLists.put(key, randomList);
		}
	}


	
	
	
	
	
	
	
	
	
	
	
	
	


}