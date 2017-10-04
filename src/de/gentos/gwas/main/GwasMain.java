package de.gentos.gwas.main;

import de.gentos.gwas.initialize.InitializeGwasMain;

public class GwasMain {
	///////////////////////////
	//////// variables ////////
	///////////////////////////

	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	/////////////////////////
	//////// methods ////////
	/////////////////////////

	public void runGentos(String[] args) {


		RunGuide rg = new RunGuide();




		/* 
		 * 	initialize system
		 *	-> read in options and check them
		 *	-> read in config file
		 *	-> check databases for correctness
		 *	-> reading in the data
		 *		- independent SNPs
		 *		- gene positions
		 */

		InitializeGwasMain init = rg.initializeGwasMain(args);




		// iterate program for for the different GWAS summary files

		for (Integer curGwasDb : init.getOptions().getGwasDbs().keySet()) {

			/* 
			 * read in current GWAS database
			 * 
			 * extract SNPs and calculate threshold
			 *	-> extract gene position or read in bed file
			 *	-> extract number of independent SNPs
			 *	-> calculate threshold 
			 *		-> bonferroni (plenty)
			 *		-> FDR
			 *	-> extract SNPs according threshold
			 *	-> extract low pval SNPs
			 *	-> write results in file
			 */

			rg.extractSNPs(init, curGwasDb);


			
				

			/* 
			 * validate results (random draw)
			 *	-> iterate entire program
			 *	-> random draw on calculated thresh
			 *	-> distributon based validation? 
			 */
			rg.validate(init, curGwasDb);

		}


		//close log file

		rg.finish(init);




	}

	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
}
