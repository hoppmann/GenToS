package de.gentos.gwas.initialize;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.gentos.gwas.initialize.data.GwasDbInfo;

public class ExtracSpecFile {

	//////////////////////////
	///// initialize variables
	String specFile;
	String dbGene;
	String tableGene;
	Map<Integer, GwasDbInfo> dbSNP = new HashMap<>();

	/////////////////
	///// Constructor
	public ExtracSpecFile(String specFile) {
		this.specFile = specFile;
		checkSpec();
	}

	////////////
	/////methods

	private void checkSpec() {

		// init variables
		String line;
		String start = null;
		int counter = 0;

		// open file and read in lines
		try (BufferedReader br = new BufferedReader(new FileReader(specFile))) {

			//read in lines
			while (( line = br.readLine()) != null){

				////////check for header and save stats in corresponding variables
				// check header for signal words
				if (line.startsWith("#dbGene") || line.startsWith("#dbSNP")) {
					start = line;
				} else if (line.isEmpty() || line.startsWith("#")) {

					// skip if line is empty or starts with #
					continue;

				} else {

					// split line and save in corresponding (to signal word) variable 
					String[] lineSplit = line.split("\t");
					if (start.startsWith("#dbGene")) {
						dbGene = lineSplit[0];
						tableGene = lineSplit[1];
					} else if (start.startsWith("#dbSNP")) {
						// save dbSNP and tableSNP in array, connect array to hash.
						String dbPath = lineSplit[0];
						String tableName = lineSplit[1];

						GwasDbInfo dbInfo = new GwasDbInfo(dbPath, tableName);
						dbSNP.put(counter, dbInfo);
						counter++;
					}
				}
			}

			// Exit and error message if file not found
		} catch  (IOException e) {
			System.out.println("ERROR:\nFile " + specFile + " not found!");
			System.exit(1);
		}

	}


	/////////////
	////// getter

	public String getDbGene() {
		return dbGene;
	}

	public String getTableGene() {
		return tableGene;
	}

	public Map<Integer, GwasDbInfo> getDbSNP() {

		return dbSNP;
	}

}
