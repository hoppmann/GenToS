package de.gentos.main;

import de.gentos.geneSet.main.ListsMain;
import de.gentos.gwas.main.GwasMain;





public class Main {
	
	//declare variables
	
	
	
	public static void main(String[] args) {
		
		///////////////////////////////////////////////
		//////// check which package is chosen ////////
		///////////////////////////////////////////////
		
		// extract first entry which has to be chosen package

		// check if GenToS GWAS is chosen, then start
		
		if (args[0].equals("gwas")) {
		
			GwasMain gwas = new GwasMain();
			gwas.runGentos(args);
		
		} else if (args[0].equals("GeneSet")) {

			ListsMain list = new ListsMain();
			list.runLists(args);
		} else {
			System.out.println("Missing analysis type: \nChoosable types are \"GWAS\" and \"GeneSet\"");
		}

	
	}
	
	
	

}
