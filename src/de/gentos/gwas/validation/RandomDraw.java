package de.gentos.gwas.validation;

import java.util.LinkedList;
import java.util.Random;

import com.google.common.collect.Multimap;

import de.gentos.general.files.HandleFiles;
import de.gentos.general.files.ReadInGenes;

public class RandomDraw {





	///////////////////
	//////// set variables

	private HandleFiles log;





	///////////////
	//////// constructor
	public RandomDraw(HandleFiles log) {
		this.log = log;

	}





	//////////////
	//////// Methods

	// 1. Iteration over random draw


	// draw random list of genes
	public void drawList(int length, int iterations, String listName, Multimap<String, LinkedList<String>> allRandomLists, long seed, ReadInGenes genes) {


		// make log entry
		log.writeOutFile("Drawing random lists of genes for " + listName);


		// init random class
		//if seed option is chosen use value as seed
		Random rand = new Random();
		if (seed != -1 ){
			rand.setSeed(seed);
		}


		// draw random lists depending on defined iterations
		for (int iter = 0 ; iter < iterations; iter++) {

			// create list to save genes to
			LinkedList<String> randomList = new LinkedList<>();

			// draw randomly according to list length
			int counter = 0;
			while (counter < length){
				int randInt = rand.nextInt(genes.getAllGeneNames().size() - 1);

				// check that gene isn't in list yet
				if (!randomList.contains(genes.getAllGeneNames().get(randInt))){
					randomList.add(genes.getAllGeneNames().get(randInt));
					counter++;
				} 
			}

			
//			randomList = new LinkedList<String>(Arrays.asList("MIR22HG", "DOT1L", "LYRM4", "PSG1", "RNASEH2A", "TMEM51", "KRT6A", "LOC101928227"));
//			, "LOC101927123"
			
			// add random list to hash 
			allRandomLists.put(listName, randomList);
			
			
			

		}
	}



}