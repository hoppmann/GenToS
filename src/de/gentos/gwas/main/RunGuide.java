package de.gentos.gwas.main;

import de.gentos.gwas.getSNPs.ExtractSNPMain;
import de.gentos.gwas.initialize.InitializeGwasMain;
import de.gentos.gwas.validation.ValidationMain;


public class RunGuide {



	//////////////////////
	//////// Set variables

	ExtractSNPMain extract;



	////////////////
	//////// Methods

	public InitializeGwasMain initializeGwasMain(String[] args){
		
		/* 
		 * 	initialize system
		 *	-> read in options and check them
		 *	-> read in config file
		 *	-> check databases for correctness
		 *	-> reading in the data
		 *		- GWAS
		 *		- independent SNPs
		 *		- gene positions
		 *		- gene lists
		 */

		
		InitializeGwasMain init = new InitializeGwasMain(args);
		return init;
	}


	
	
	
	public void extractSNPs(InitializeGwasMain init, Integer curGwasDb) {
	
		/* 
		 * read in current GWAS database 
		 * 
		 * extract SNPs and calculate threshold
		 *	-> extract gene position if not given via bed file
		 *	-> extract number of independent SNPs
		 *	-> calculate threshold 
		 *		-> bonferroni (lenient, plenty)
		 *		-> FDR
		 *	-> extract SNPs according threshold
		 *	-> extract low pval SNPs
		 *	-> write results in file
		*/
		extract = new ExtractSNPMain(init, curGwasDb);

		
	}
	
	
	


	public void validate(InitializeGwasMain init, Integer curGwasDb) {

		/*
		 * 
		 * validate results 
		 * 	(random draw)
		 *		-> iterate entire program
		 *		-> random draw on calculated threshold
		 *		-> distribution based validation?
		 *	binomial
		 *		-> calculate binomial output for random draws
		 *		-> plot histogram
		 */

		if (init.getGwasOptions().getCmd().hasOption("enrichment")) {
			
			if (init.getGwasOptions().isRandomRepeats()){
				
				ValidationMain validation = new ValidationMain(init, curGwasDb);
				validation.randomDraw(extract);
				
			} else if (init.getGwasOptions().isBinomial()) {
				
				ValidationMain validation = new ValidationMain(init, curGwasDb);
				validation.binomial(extract);
				
			}
		}

	}


	public void finish(InitializeGwasMain init) {
		//close log file
		init.getLog().writeOutFile("Program finished.");
		init.getLog().closeFile();
	}

}
