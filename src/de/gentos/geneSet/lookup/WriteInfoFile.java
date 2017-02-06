package de.gentos.geneSet.lookup;

import java.io.File;
import java.util.Map;

import de.gentos.geneSet.initialize.InitializeGeneSetMain;
import de.gentos.geneSet.initialize.data.RunData;
import de.gentos.geneSet.initialize.options.GetGeneSetOptions;
import de.gentos.general.files.HandleFiles;

public class WriteInfoFile {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	private GetGeneSetOptions options;
	private InitializeGeneSetMain init;
	private Map<String, RunData> data;
	
	
	
	
	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public WriteInfoFile(InitializeGeneSetMain init) {
		
		this.init = init;
		this.options = init.getOptions();
		this.data = init.getDataMap();
		
		
		writeInfo();
	}
	
	
	
	/////////////////////////
	//////// methods ////////
	/////////////////////////

	private void writeInfo(){
		
		// open file for writing
		String fileName = options.getOutDir() + File.separator + options.getInfoFile();
		HandleFiles infoFile = new HandleFiles();
		infoFile.openWriter(fileName);
		
		
		/* create short summary in the top containing 
			- resource list
			- number of enriched lists
		*/
		infoFile.writeFile("Inputlist\tnumber of enriched lists");
		for (String curInputList : init.getDataMap().keySet()) {
			infoFile.writeFile(curInputList + "\t" + Integer.toString(data.get(curInputList).getNumberEnrichedLists()));
		}
		
		// enter empty lines for better visability
		infoFile.writeFile("\n");
		
		
		
		
		
		
		/* create a long summary list containing
		 * - resource list
		 * - number of enriched lists
		 * - name of enriched list
		 * 
		 */
		
		
		// for each input list write infos in file
		for ( String curInputList : init.getDataMap().keySet()){
			
			infoFile.writeFile(curInputList);
			infoFile.writeFile("Number of enriched lists: " + Integer.toString(data.get(curInputList).getNumberEnrichedLists()));
			
			// extract found lists
			for (String curEnrichedList : data.get(curInputList).getEnrichedLists()) {
				
				// check if enriched list is sorted
				if (init.getResources().get(curEnrichedList).isSorted()) {
					infoFile.writeFile(curEnrichedList + "\t sorted ");	
				} else {
					infoFile.writeFile(curEnrichedList + "\t NOT sorted ");	

				}
				
			}
			
			infoFile.writeFile("\n\n");
			
		}
		
		
		// closer writer
		infoFile.closeFile();
		
		
	}
	
	
	
	
	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
}
