package de.gentos.lists.initialize;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.gentos.general.files.HandleFiles;
import de.gentos.general.options.lists.GetListOptions;

public class ReadInResourceLists {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	GetListOptions options;
	HandleFiles log;
	Map<String, GeneLists> geneLists;

	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public ReadInResourceLists(GetListOptions options, HandleFiles log) {

		// retrieve variables
		this.options = options;
		this.log = log;


	}



	/////////////////////////
	//////// methods ////////
	/////////////////////////

	public Map<String, GeneLists> readInLists() {


		// init resource map
		geneLists = new HashMap<>();

		
		
		
		///////////////////////////////////
		//////// get list of all resource files in resource direcotry
		List<String> folderContent = new ArrayList<String>();

		// get direcrory of resource files
		String resourceDir = options.getListDir();
		File dir = new File(resourceDir);

		// check if path is directory
		if (!dir.isDirectory()) {
			log.writeError("Directory " + resourceDir + " not found.");
			System.exit(1);
		}

		// check if directory is empty else read in names 
		if (new File(resourceDir).list().length > 0) {

			// get list of files in directory
			File[] listOfFiles = new File(resourceDir).listFiles();
			for (File file : listOfFiles) {
				if (file.isFile()) {
					folderContent.add(resourceDir + File.separator + file.getName());
				}
			}	
		} else {
			// if directory is empty make log error entry and exit 
			log.writeError(resourceDir + " does not contain any files!");
			System.exit(1);
		}






		///////////////////////////////
		//////// read in resource lists 
		HandleFiles file = new HandleFiles();
		file.setLog(log);
		for (String  curFile : folderContent) {

			// prepare list for collection of resource data for hash
			//			List<ResourceData> data = new LinkedList<>();

			// create file resource file object for information storage
			GeneLists curListIn = new GeneLists();

			// open file
			file.setLog(log);
			LinkedList<String> lines = file.openFile(curFile, true);

			// read in line wise and save correnspondingly
			for (String line : lines ) {

				// split line 
				String[] splitLine = line.split("\t");

				// extract header and check if file is sorted or not.
				if (splitLine[0].startsWith("#")){

					// save header
					curListIn.addHeader(splitLine);

					// check if list is sorted if so store it
					if (splitLine[0].toUpperCase().startsWith("#SORTED")){
							curListIn.setSorted(true);
					}

				} else 

					// if not header section any more save as gene element
					curListIn.addGeneLine(splitLine);
			}
			
			
			// save current list to map containing all resource lists
			geneLists.put(curFile, curListIn);
			
			
			
		}

		
		
		

		/////////////////////////////
		//////// return read in files
		return geneLists;

	}





	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////







}
