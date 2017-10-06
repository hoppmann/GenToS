package de.gentos.general.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import de.gentos.gwas.initialize.data.GeneInfo;

public class HandleFiles {

	//////////////////////
	//////// set variables
	PrintWriter writer;
	HandleFiles log;


	////////////////
	//////// Methods

	public void openWriter(String fileName) {

		try {
			writer = new PrintWriter(fileName);
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: Can't create " + fileName + "!");
			System.out.println(e);
			System.exit(1);
		}		
	}


	//writes handed over String to file
	public void writeFile (String line){
		writer.write(line);
		writer.write(System.lineSeparator());
	}



	// write in file and to screen
	public void writeOutFile (String line) {
		System.out.println(line);
		writer.write(line);
		writer.write(System.lineSeparator());
	}


	// write in file as warning
	public void writeWarning (String line) {
		String warningline = "## WARNING: " + line;
		System.out.println(warningline);
		writer.write(warningline);
		writer.write(System.lineSeparator());

		
	}

	// write in file and to screen and close file
	public void writeError (String line) {
		String errorline = System.lineSeparator() + "## ERROR: " + line + System.lineSeparator();
		System.out.println(errorline);
		writer.write(errorline);
		writer.write(System.lineSeparator());
		writer.close();
	}



	//closes current writer
	public void closeFile() {
		writer.close();
	}


	// check file for existance

	public void exist (String filePath) {

		File file = new File(filePath);

		if (!file.exists()) {
			System.out.println("ERROR:\nFile " + filePath + " not found!");
			System.exit(1);
		}

	}

	
	
	// open text file for read in

	public LinkedList<String> openFile(String filePath, boolean skipHeader) {

		// make log entry if log available else print on screen.
		if (log != null){
			log.writeOutFile("Reading in " + filePath);
		} else {
			System.out.println("Reading in " + filePath);
		}

		
		// open file and return lines in array
		BufferedReader br = null;
		String line;
		LinkedList<String> lines = new LinkedList<>();
		try {
			br = new BufferedReader(new FileReader(filePath));
			while ((line = br.readLine()) != null) {
				
				// check if header should also be read in or be skipped
				if (skipHeader){
					if (line.isEmpty() || line.startsWith("#")) {
						continue;
					}
				}
				lines.add(line);
			}
			br.close();

		} catch (Exception e1) {

			// check if log is available then either print on screen only or make log error entry
			if (log != null) {
				log.writeError("Failed open file " + filePath);
				System.exit(1);
			}  else {
				System.out.println("## ERROR: Failed open file " + filePath);
				System.exit(1);
			}
		}

		// return retrieved lines as linked list
		return lines;
	}


	
	
	
	
	
	//////// read BED file
	public Map<String, GeneInfo> readBed(String bedPath) {
		
		// open file
		LinkedList<String> lines = this.openFile(bedPath, true);
		
		// prepare map for storing data and counter as key
		int keyCounter = 0;
		Map<String, GeneInfo> bedRois = new HashMap<>();
		
		
		// read in file line wise
		for (String curLine : lines) {

			// create counter to get uniq key for each 
			
			String[] splitLine = curLine.split("\t");

			/* 
			 * extract information about current ROI
			 * 		chromosome and remove all besides the chr number
			 * 		start and stop
			 *  	save in GeneInfo object
			 *  gene names have to be treated separately in case no name given
			 */
			
			Integer chr = Integer.parseInt(splitLine[0].replaceAll("[^\\d]", ""));
			Integer start = Integer.parseInt(splitLine[1]);
			Integer stop = Integer.parseInt(splitLine[2]);

			/* 
			 * add current ROI consisting of chr/start/stop to hash
			 * if gene is given in bed file use gene name as key, else use keyInteger as arbitrary key
			 * as key use incrementing integer and not geneName, due to flexibility reasons 
			 * 
			 */
			
			// check if 4th column exists > expecting to be the gene name column
			if (splitLine.length > 3) {
				// get gene name
				String geneName = splitLine[3].replaceAll("\\s", "");
				// handle case if gene names are given. 
				if (bedRois.containsKey(geneName)) {
					bedRois.get(geneName).addRoi(chr, start, stop);
				} else {
					bedRois.put(geneName, new GeneInfo());
					bedRois.get(geneName).addRoi(chr, start, stop);
				}

			
			} else {
				
				// handle case of non gene names given
				String key  = Integer.toString(keyCounter);
				
				bedRois.put(key, new GeneInfo());
				bedRois.get(key).addRoi(chr, start, stop);
				bedRois.get(key).setHasGeneName(false);
				
				// increment key for next ROI
				keyCounter++;
			}
		}
		
		// return read in bed file
		return bedRois;

		
	}
	
	
	
	
	
	public void createDirectory(String path, HandleFiles log) {

		log.writeOutFile("Creating direcotry \"" + path + "\"");
		new File(path).mkdir();



	}


	public void setLog(HandleFiles log) {
		this.log = log;
	}


}
