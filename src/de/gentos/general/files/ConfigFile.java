package de.gentos.general.files;

import java.io.IOException;
import java.util.Properties;

public class ConfigFile {
	
		//define variables; values in config file to be extracted
		private String Rpath;
		private String dbGene;
		private String tableGene;
		private String indepDir;
		
		//////////////////
		////// constructor
		
		//constructor executing the loading of the config file
		public ConfigFile() throws IOException {
			System.out.println("Reading config file");
			
			loadConfig();
			
		}
		

		///////////////
		////// methods
		
		// load config file
		private void loadConfig() throws IOException {
			
			//create new object
			Properties prop = new Properties();

				prop.load(getClass().getClassLoader().getResourceAsStream("GenToS.config"));
			
			//read in config file informations
			Rpath = prop.getProperty("Rpath");
			testR(prop);
			
			dbGene = prop.getProperty("dbGene");
			
			tableGene = prop.getProperty("tableGene");
			
			indepDir = prop.getProperty("indepDir");
			
		}
		
		
		private void testR(Properties prop) {
			ProcessBuilder process = new ProcessBuilder(Rpath);
			try {
				process.start();
			} catch (IOException e) {
				System.out.println("#### An error occured testing Rscript! \nMake sure the path to Rscript is set properly in config File.\n");
				System.exit(1);
			}

		}

		
		//////////
		// getters
		
		public String getDbGene() {
			return dbGene;
		}

		public String getTableGene() {
			return tableGene;
		}

		public String getRpath() {
			return Rpath;
		}


		public String getIndepDir() {
			return indepDir;
		}

}
