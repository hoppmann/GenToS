package de.gentos.validation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import de.gentos.getSNPs.ExtractData;
import de.gentos.getSNPs.ExtractSNPMain;
import de.gentos.initialize.InitializeMain;
import de.gentos.initialize.ReadInGwasData;
import de.gentos.initialize.data.GeneListInfo;
import de.gentos.main.HandleFile;
import de.gentos.threshold.CreateThresh;

public class ValidationMain {


	///////////////////
	//////// set variables

	private InitializeMain init;
	private HandleFile log;
	private int numberOfIterations;





	///////////////
	//////// constructor
	public ValidationMain(InitializeMain init) {
		this.init = init;
		this.log = init.getLog();
		this.numberOfIterations = init.getOptions().getNumberOfIterations();
	}







	//////////////
	//////// Methods

	// 1. Binomial distribution
	// 2. Iteration over random draw





	//////// simulating binomial distribution
	// (1)
	public void binomial (ExtractSNPMain extractMain) {

		// make log entry that randomDraw validation started
		log.writeOutFile("######## Starting validation with binomial distribution. ########\n");

		// create temporary folder and folder for graphs
		String tmpDir = init.getOptions().getDir()+ System.getProperty("file.separator") + "tmp";
		String validationDir = init.getOptions().getDir()+ System.getProperty("file.separator") + "graphs";
		mkDir(tmpDir);
		mkDir(validationDir);


		/////////////////////
		//// for each database table and each list calculate binomial distribution  
		for (int currentGwasFile : init.getDbSNPInfo().keySet()){

			// get gwas data to pass on
			ReadInGwasData gwasData = init.getDbSNPInfo().get(currentGwasFile).getGwasData();

			// instanciate binomial class for getting informations
			Binomial binom = new Binomial(init, gwasData);


			// user information about progress
			log.writeOutFile("######## Running on " + init.getDbSNPInfo().get(currentGwasFile).getDbName() +"\t" + init.getDbSNPInfo().get(currentGwasFile).getTableName());

			// run over each gene query list
			for (String origList : init.getOptions().getGeneLists().keySet()){

				///////////////
				//////// estimate probability of hit

				//////// gather informations needed
				// get threshold
				Double thresh = init.getDbSNPInfo().get(currentGwasFile).getListThresh().get(origList);

				// estimate probability
				double probHit = binom.estimateProb(thresh);



				///////////////////
				//////// get length of gene list
				int lengthList = init.getOptions().getGeneLists().get(origList).size();



				///////////////		
				//////// get number of iterations
				int numberIterations = init.getOptions().getNumberOfIterations();



				//////////////
				//////// perform random draw of binomial distributed variables
				// Instantiate random generator
				List<Integer> histogram = binom.simulate(lengthList, probHit, numberIterations);




				///////////////
				//// prepare plotting

				// get hits from lookup
				int actualFindings = init.getDbSNPInfo().get(currentGwasFile).getHitsPerList().get(origList);  

				//prepare outName of graphs Validation graphs
				String databaseName = init.getDbSNPInfo().get(currentGwasFile).getDbName();
				String tableName = init.getDbSNPInfo().get(currentGwasFile).getTableName();
				String outName = databaseName + "-" + tableName + "-" + origList;
				double pVal = binom.cummulativeBinom(probHit, lengthList, actualFindings);

				String legend = "pVal = " + String.format(Locale.US, "%.2e", pVal);
				PlotHistogram plotter = new PlotHistogram(init);
				plotter.plotHist(histogram, actualFindings, tmpDir, outName, validationDir, legend, thresh);

				// print out probHit on screen and in file
				if (init.getOptions().isGetProp()){
					System.out.println("p(Hit) " + databaseName + " " + tableName + " " + origList + " = " + String.format("%6.2e", probHit));
					log.writeFile("p(Hit) " + databaseName + " " + tableName + " " + origList + " =  " + String.format("%6.2e", probHit));
				}


			}
		}

		// delete temp dir if not specified to keep
		if (!init.getOptions().getCmd().hasOption("keepTmp")){
			rmDir(tmpDir);
		}
	}







	//////// Estimate enrichment by drawing random lists and iterate over program.
	// (2)
	public void randomDraw(ExtractSNPMain extractMain) {


		// make log entry that randomDraw validation started
		log.writeOutFile("######## Starting validation with randomRepeats. ########\n");

		// create multimap with lists of random genes
		Multimap<String, LinkedList<String>> allLists = LinkedListMultimap.create();

		// instanciate RandomDraw
		RandomDraw random = new RandomDraw(init);


		/////////////////
		//// for each list draw random list of length list

		for (String origList : init.getOptions().getGeneLists().keySet()){

			// get length of list 
			int lengthCurrenList = init.getOptions().getGeneLists().get(origList).size(); 

			// creat random lists
			random.drawList(lengthCurrenList, numberOfIterations, origList, allLists);
		}


		// create temporary folder and folder for graphs
		String tmpDir = init.getOptions().getDir()+ System.getProperty("file.separator") + "tmp";
		String validationDir = init.getOptions().getDir()+ System.getProperty("file.separator") + "graphs";
		mkDir(tmpDir);
		mkDir(validationDir);




		/////////////////////
		//// for each database table run program on random lists  
		for (int currentGwasFile : init.getDbSNPInfo().keySet()){

			// usr information about progress
			log.writeOutFile("######## Running on " + init.getDbSNPInfo().get(currentGwasFile).getDbName() +"\t" + init.getDbSNPInfo().get(currentGwasFile).getTableName());

			// get gwas data to pass on
			ReadInGwasData gwasData = init.getDbSNPInfo().get(currentGwasFile).getGwasData();

			// init extraction class
			ExtractData extract = new ExtractData(init);
			extract.setVerbose(false);


			//// Instanciate Binom for pVal calculation
			Binomial binom = new Binomial(init, gwasData);



			// for each original list iterate over each randomly generated list
			// get independent SNPs, get threshold and extract snps to get number of hist in list
			for (String origList : allLists.keySet()){


				// init variable for validation results
				LinkedList<Integer> histogram = new LinkedList<>();
				double thresh = 0;

				// counter to give user information about progress
				int counter = 1;
				System.out.println("\nRunning on " + origList);
				for (LinkedList<String> currentRandomList : allLists.get(origList)){

					// inform user about progression
					System.out.println("Iteration list " + counter + "/" + numberOfIterations);
					counter++;

					// run validation on each randomly drawn list 
					// set variable 
					Map<String, GeneListInfo> genesWithThresh = new HashMap<>();

					// get threshold for each gene
					CreateThresh createThresh = new CreateThresh(init, gwasData);
					createThresh.choose(extract, currentRandomList, genesWithThresh);

					thresh = createThresh.getThresh();

					// extract snps with pval lower then threshold
					// create result hash containing extracted pvals
					extract.extractSNPs(gwasData, genesWithThresh);

					// save number of hits for later plotting
					int count = 0;
					for (String gene : genesWithThresh.keySet()){
						if (genesWithThresh.get(gene).isHasHit()){
							count++;
						}
					}	

					// add to list each run the number of hits detected. Collecting for producing histogram. 
					histogram.add(count);
				}


				///////////////
				//////// get information to calculate pVal based on binom dist

				// get threshold
				thresh = init.getDbSNPInfo().get(currentGwasFile).getListThresh().get(origList);

				// estimate probability
				double probHit = binom.estimateProb(thresh);

				//////// get length of gene list
				int lengthList = init.getOptions().getGeneLists().get(origList).size();


				///////////////
				//// prepare plotting

				// get hits from lookup
				int actualFindings = init.getDbSNPInfo().get(currentGwasFile).getHitsPerList().get(origList);  


				//prepare outName of graphs Validation graphs
				String databaseName = init.getDbSNPInfo().get(currentGwasFile).getDbName();
				String tableName = init.getDbSNPInfo().get(currentGwasFile).getTableName();
				String outName = databaseName + "-" + tableName + "-" + origList;
				double pVal = binom.cummulativeBinom(probHit, lengthList, actualFindings);
				String legend = "pVal = " + String.format(Locale.US, "%.2e", pVal);

				PlotHistogram plotter = new PlotHistogram(init);
				plotter.plotHist(histogram, actualFindings, tmpDir, outName, validationDir, legend, thresh);


			}
		}

		// delete temp dir if not specified to keep
		if (!init.getOptions().getCmd().hasOption("keepTmp")){
			rmDir(tmpDir);
		}
	}

	// create dir
	private void mkDir(String dir) {

		try {
			FileUtils.forceMkdir(new File(dir));
		} catch (IOException e) {
			log.writeError("## An error occured while generating directory" + dir + ".");
			System.exit(1);
		}


	}


	// remove dir 
	private void rmDir (String dir){
		try {
			FileUtils.forceDelete(new File(dir));
		} catch (IOException e) {
			log.writeError("## Warning: couldn't remove " + dir + "!");	}
	}



	///////////////
	//////// Getters








}
