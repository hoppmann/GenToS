package de.gentos.geneSet.writeResults;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.gentos.geneSet.initialize.InitializeGeneSetMain;
import de.gentos.geneSet.initialize.data.InfoData;
import de.gentos.geneSet.initialize.data.RunData;
import de.gentos.geneSet.initialize.options.GetGeneSetOptions;
import de.gentos.general.files.HandleFiles;
import de.gentos.general.misc.generalMethods;

public class WriteInfoFile {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	
	
	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////
	
	/////////////////////////
	//////// methods ////////
	/////////////////////////

	public void writeInfo(InitializeGeneSetMain init, Map<String, InfoData> infoMap){
		
		// retrieve and extract needed variables
		GetGeneSetOptions options = init.getOptions();
		
		
		
		
		// open file for writing
		String fileName = options.getOutDir() + File.separator + options.getInfoFile();
		HandleFiles infoFile = new HandleFiles();
		infoFile.openWriter(fileName);
		
		
		/* create short summary in the top containing 
			- resource list
			- number of enriched lists
			- number of genes in input list
		*/
		
		
		// prepare header
		infoFile.writeFile("Inputlist\tgenes in input list\tenriched resources");
		
		
		// sort infoMap by number of enriched resources
		Map<String, Double> toBeSorted = new HashMap<>();
		for ( String curInputList : infoMap.keySet()) {
			toBeSorted.put(curInputList, (double) infoMap.get(curInputList).getNumberEnrichedResources());
		}
		Map<String, Double> sortedInfoMap = new generalMethods().sortMapByValue(toBeSorted, false);

		
		
		// write condense primary info 
		for (String curInputList : sortedInfoMap.keySet()){
			
			// retrieve variables for readability
			int numberEnrichedResources = infoMap.get(curInputList).getNumberEnrichedResources();
			int genesInInputList = infoMap.get(curInputList).getNumberGenesInInput();
			
			// prepare line
			String line = curInputList + "\t" + genesInInputList + "\t" + numberEnrichedResources;
			
			// Write out in info-file
			infoFile.writeFile(line);
			
		}
		
		
		// enter empty lines for better visibility
		infoFile.writeFile("\n");
		
		
		
		
		
		
		/* create a long summary list containing
		 * - resource list
		 * - number of enriched lists
		 * - name of enriched list
		 * - number of genes in input list
		 */
		
		// for each input list write infos in file
		for ( String curInputList : sortedInfoMap.keySet()){
			infoFile.writeFile(curInputList);
			infoFile.writeFile("Number of genes in input list: " + infoMap.get(curInputList).getNumberGenesInInput());;
			infoFile.writeFile("Number of enriched resource lists: " + Integer.toString(infoMap.get(curInputList).getNumberEnrichedResources()));

			// extract found lists
			for (String curEnrichedList : infoMap.get(curInputList).getEnrichedResources()) {
				
				// check if enriched list is sorted
				if (init.getResources().get(curEnrichedList).isSorted()) {
					infoFile.writeFile(curEnrichedList + "\t sorted ");	
				} else {
					infoFile.writeFile(curEnrichedList + "\t NOT sorted ");	
				}
			}
			// add spacing for visibility
			infoFile.writeFile("\n\n");
		}
		
		
		// closer writer
		infoFile.closeFile();
		
		
	}

	
	
	public void collectData(RunData runData, String curInputName, Map<String, InfoData> info) {
		
		// create new InfoData object containing  
		InfoData infoData = new InfoData();
		infoData.setNumberEnrichedResources(runData.getNumberEnrichedResources());
		infoData.setNumberGenesInInput(runData.getLengthInput());
		infoData.setEnrichedResources(runData.getEnrichedResources());
		
		// add infoData to info map
		info.put(curInputName, infoData);
		
		
		
		
	}
	
	
	
	
	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
}
