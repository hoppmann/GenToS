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


	
	
	public void createDirectory(String path, HandleFiles log) {

		log.writeOutFile("Creating direcotry \"" + path + "\"");
		new File(path).mkdir();



	}


	public void setLog(HandleFiles log) {
		this.log = log;
	}


}
