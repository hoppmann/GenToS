package de.gentos.gwas.main;

import de.gentos.gwas.initialize.InitializeMain;

public class GentosMain {
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




		// initialize system
		//	-> read in options and check them
		//	-> read in config file
		//	-> check databases for correctness

		InitializeMain init = rg.initializeMain(args);





		// extract snps and calculate threshold
		//	-> extract gene position
		//	-> extract number of independent SNPs
		//	-> calculate threshold 
		//		-> bonferoni (lenient, plenty)
		//		-> FDR
		//	-> extract SNPs according threshold
		//	-> extract low pval SNPs
		//	-> write results in file

		rg.extractSNPs(init);






		// validate results (random draw)
		//	-> iterate entire program
		//	-> random draw on calculated thresh
		//	-> distributon based validation? 

		rg.validate(init);





		//close log file

		rg.finish(init);




	}

	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
}
