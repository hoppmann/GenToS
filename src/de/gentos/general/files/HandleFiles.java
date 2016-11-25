package de.gentos.general.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.LinkedList;

public class HandleFiles {

	//////////////////////
	//////// set variables
	PrintWriter writer;

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

	
	
	// write in file and to screen and close file
		public void writeError (String line) {
			System.out.println(line);
			writer.write(line);
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
	
	public LinkedList<String> openFile(String filePath) {

		BufferedReader br = null;
		String line;
		LinkedList<String> lines = new LinkedList<>();
		try {
			br = new BufferedReader(new FileReader(filePath));
			while ((line = br.readLine()) != null) {
				if (line.isEmpty() || line.startsWith("#")) {
					continue;
				}
				lines.add(line);
			}
			br.close();

		} catch (Exception e1) {
			System.out.println("Failed open file " + filePath);
			System.exit(1);
		}

		return lines;
	}



}
