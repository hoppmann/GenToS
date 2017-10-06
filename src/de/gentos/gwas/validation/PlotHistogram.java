package de.gentos.gwas.validation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import de.gentos.general.files.HandleFiles;
import de.gentos.gwas.initialize.InitializeGwasMain;
import de.gentos.gwas.initialize.options.GetGwasOptions;

public class PlotHistogram {

	//////////////
	//////// variables


	InitializeGwasMain init;
	HandleFiles log;
	GetGwasOptions options;





	////////////////
	//////// constructor


	public PlotHistogram(InitializeGwasMain init) {

		this.init = init;
		this.log = init.getLog();
		this.options = init.getGwasOptions();

	}










	//////////////
	//////// Methods


//	@SuppressWarnings("unused")
	public void plotHist (List<Integer> histogram, int actualFindings, String tempDir, String outName, String validationDir, String legend, Double thresh) {


		//save Data to tmp file in  dir
		HandleFiles file = new HandleFiles();
		String tempFileName = tempDir + System.getProperty("file.separator") + outName;
		file.openWriter(tempFileName);
		
		for (Integer line : histogram){
			String lineToFile = line.toString();
			file.writeFile(lineToFile);
		}
		file.closeFile();

		
		// for readability prepare output strings
		String rStarter = init.getConfig().getRpath();
		String rScript = init.getGwasOptions().getProgPath() + "MakeHist.R";
		String measuredHits = Integer.toString(actualFindings);
		String nameOfGraph = validationDir + System.getProperty("file.separator") + outName;
		String suffix = init.getGwasOptions().getGraphSuffix();
		String scaling = options.getScaling();
		
		// prepare title
		String title = null;
		// if method is FDR include FDR threshold
		if (init.getGwasOptions().getMethod().equals("FDR")){
		
			String threshold = String.format(Locale.US, "%.2e", thresh);
			title = outName + "\n" + init.getGwasOptions().getMethod() + ": " + init.getGwasOptions().getAlpha() + "\nThreshold: " + threshold;
		} else {

			String threshold = String.format(Locale.US, "%.2e", thresh);
			title = outName + "\n" + init.getGwasOptions().getMethod() + "\nThreshold: " + threshold;

		}
		
		
		// check if strings get overwritten by commandline options
		if (options.getCmd().hasOption("title")){
			title = options.getGraphTitel();
		}
		
		// make log entry
		log.writeOutFile("Plotting: " + outName);
		
		// prepare command
		ArrayList<String> command = new ArrayList<>();
		command.add(rStarter);
		command.add(rScript);
		command.add(tempFileName);
		command.add(nameOfGraph);
		command.add(title);
		command.add(legend);
		command.add(suffix);
		command.add(scaling);
		command.add(measuredHits);

		
		//instanciate process builder
		ProcessBuilder builder = new ProcessBuilder(command);
		
		try {

			//execute command
			Process proc = builder.start();

			//check for noScreen option
			//print R output
			BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

			String lineIn = in.readLine();
			
			// comment out if R output not desired
			while (lineIn != null){
				System.out.println(lineIn);

				lineIn = in.readLine();
			}

		} catch (IOException e) {
			log.writeError("An error occured while executing Rscript.");
			System.exit(1);
		}


	}




}
