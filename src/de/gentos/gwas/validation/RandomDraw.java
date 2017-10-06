package de.gentos.gwas.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.Multimap;

import de.gentos.general.files.HandleFiles;
import de.gentos.general.files.ReadInGeneDB;
import de.gentos.gwas.initialize.data.GeneInfo;

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
	public void drawInMap(int length, int iterations, String listName, Multimap<String, Map<String, GeneInfo>> allLists, long seed, ReadInGeneDB genes, Boolean printLog) {

		// make log entry
		if (printLog) {
			log.writeOutFile("Drawing random lists of genes for " + listName);
		}

		// init random class
		//if seed option is chosen use value as seed
		Random rand = new Random();
		if (seed != -1 ){
			rand.setSeed(seed);
		}


		// draw random lists depending on defined iterations
		for (int iter = 0 ; iter < iterations; iter++) {

			// create list to save genes to
			Map<String, GeneInfo> randomList = new HashMap<>();

			// draw randomly according to list length
			int counter = 0;
			while (counter < length){
				int randInt = rand.nextInt(genes.getAllGeneNames().size() - 1);

				// check that gene isn't in list yet
				if (!randomList.containsKey(genes.getAllGeneNames().get(randInt))){
					randomList.put(genes.getAllGeneNames().get(randInt), new GeneInfo());
					counter++;
				} 
			}

			
			// add random list to hash 
			allLists.put(listName, randomList);
		}
	}

	
	
	
	// draw random list of genes and save in single LinkedList
	public void drawSingleList(int length, int iterations, String listName, ArrayList<LinkedList<String>> allLists, long seed, ReadInGeneDB genes, Boolean printLog) {

		// make log entry
		if (printLog){
			log.writeOutFile("Drawing random lists of genes for " + listName);
		}

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

			
			// add random list to hash 
			allLists.add(randomList);
		}
	}


	
	
	// draw from user given reference file
	public void drawFromReference(Map<String, GeneInfo> reference, Multimap<String, Map<String, GeneInfo>> allRandomLists,int lengthOrigList, int iterations, String listName, long seed, boolean printLog) {
		
		//make log entry if desired 
		if (printLog){
			log.writeOutFile("Drawing random lists of genes for " + listName);
		}
		
		
		// init random class 
		// if seed option is chosen use value as seed
		Random rand = new Random();
		if (seed != -1) {
			rand.setSeed(seed);
		}
		
		
		// check that reference is at least of the size of current query
		if (reference.keySet().size()  < lengthOrigList) {
			log.writeError("The reference for drawing random lists must be at least of the size of the query list."
					+ "\nLength reference = " + reference.keySet().size() + " Length query list = " + lengthOrigList);
			System.exit(1);
		}
		
		
		
		// save all keys from reference in linked list
		List<String> allKeys = new LinkedList<>(); 
		allKeys.addAll(reference.keySet());
		
		
		
		
		// draw random lists equal to the number of iterations 
		for (int i = 0 ; i < iterations; i++) {
			
			// create map of ROIs
			Map<String, GeneInfo> randList = new HashMap<>();
			
			// draw randomly according to original list length
			int counter = 0;
			while (counter < lengthOrigList) {
				
				// get random integer and extract corresponding key
				int randInt = rand.nextInt(reference.keySet().size());
				String curKey =  allKeys.get(randInt);

				// check if key is already in use, then rerun current draw, else save ROI
				if (! randList.containsKey(curKey)) {
					randList.put(curKey, reference.get(curKey));
					counter++;
				}
			}
			
			// store random drawn list in list of maps
			allRandomLists.put(listName, randList);
		}
	}
}