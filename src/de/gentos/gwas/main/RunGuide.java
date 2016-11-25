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
		// initialize system
		//		-> read in options and check them
		//		-> read in config file
		//		-> check databases for correctness
		InitializeGwasMain init = new InitializeGwasMain(args);

		return init;
	}


	public void extractSNPs(InitializeGwasMain init) {
		// extract independent snps and calculate threshold
		//		-> extract gene position
		//		-> extract number of independent SNPs
		//		-> calculate threshold 
		//			-> bonferoni (lenient, plenty)
		//			-> FDR
		//		-> extract SNPs according threshold
		//		-> extract low pval SNPs
		//		-> write results in file

		extract = new ExtractSNPMain(init);

	}


	public void validate(InitializeGwasMain init) {

		// validate findings of lists by
		//		-> iteration randomly drawn genes then running the program
		//		-> simulating random draw via binomial distribution


		if (init.getGwasOptions().getCmd().hasOption("enrichment")) {
			if (init.getGwasOptions().isRandomRepeats()){
				
				ValidationMain validation = new ValidationMain(init);
				validation.randomDraw(extract);
				
			} else if (init.getGwasOptions().isBinomial()) {
				
				ValidationMain validation = new ValidationMain(init);
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
