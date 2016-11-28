package de.gentos.main;

import de.gentos.gwas.main.GwasMain;
import de.gentos.lists.main.ListsMain;





public class Main {
	
	//declare variables
	
	
	
	public static void main(String[] args) {
		
		///////////////////////////////////////////////
		//////// check which package is chosen ////////
		///////////////////////////////////////////////
		
		// extract first entry which has to be chosen package

		String toRun = args[0];
		// check if GenToS GWAS is chosen, then start
		
		if (args[0].equals("gwas")) {
		
			GwasMain gwas = new GwasMain();
			gwas.runGentos(args);
		
		} else if (args[0].equals("list")) {

			ListsMain list = new ListsMain();
			list.runLists(args);
		}
			
			
		
		
//		for (String bla : args) {
//			System.out.println(bla);
//		}
		
//		System.out.println(args[0]);
		
		
		
//		
//		RunGuide rg = new RunGuide();
//		
//		
//		
//		
//		// initialize system
////		-> read in options and check them
////		-> read in config file
////		-> check databases for correctness
//
//		InitializeMain init = rg.initializeMain(args);
//		
//
//		
//		
//		
//		// extract snps and calculate threshold
////		-> extract gene position
////		-> extract number of independent SNPs
////		-> calculate threshold 
////			-> bonferoni (lenient, plenty)
////			-> FDR
////		-> extract SNPs according threshold
////		-> extract low pval SNPs
////		-> write results in file
//		
//		rg.extractSNPs(init);
//		
//
//
//		
//		
//		
//		// validate results (random draw)
////		-> iterate entire program
////		-> random draw on calculated thresh
////		-> distributon based validation? 
//		
//		rg.validate(init);
//		
//		
//		
//		
//				
//		//close log file
//		
//		rg.finish(init);
		
	}
	
	
	

}
