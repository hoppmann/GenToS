package de.gentos.gwas.validation;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.plaf.synth.SynthSpinnerUI;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import de.gentos.general.files.HandleFiles;
import de.gentos.gwas.getSNPs.ExtractDataMethods;
import de.gentos.gwas.getSNPs.ExtractSNPMain;
import de.gentos.gwas.initialize.InitializeGwasMain;
import de.gentos.gwas.initialize.ReadInGwasData;
import de.gentos.gwas.initialize.data.GeneInfo;
import de.gentos.gwas.initialize.data.GwasDbInfo;
import de.gentos.gwas.initialize.options.GetGwasOptions;
import de.gentos.gwas.threshold.CreateThresh;

public class ValidationMain {


	///////////////////
	//////// set variables

	private InitializeGwasMain init;
	private HandleFiles log;
	private int numberOfIterations;
	private GwasDbInfo curGWASdbInfo;
	private GetGwasOptions options;



	///////////////
	//////// constructor
	public ValidationMain(InitializeGwasMain init, Integer curGWASdbKey) {
		this.init = init;
		this.log = init.getLog();
		this.numberOfIterations = init.getGwasOptions().getNumberOfIterations();
		this.curGWASdbInfo = init.getOptions().getGwasDbs().get(curGWASdbKey);
		this.options = init.getOptions();
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
		String tmpDir = init.getGwasOptions().getDir()+ System.getProperty("file.separator") + "tmp";
		String validationDir = init.getGwasOptions().getDir()+ System.getProperty("file.separator") + "graphs";
		mkDir(tmpDir);
		mkDir(validationDir);

		
		
		/////////////////////
		//// for current GWAS database and each gene query list calculate binomial distribution  

		// get read in GWAS data to to be processed
		ReadInGwasData gwasData = init.getGwasData();

		// instanciate binomial class for getting informations
		Binomial binom = new Binomial(init, gwasData);


		// user information about progress
		String gwasDbName = curGWASdbInfo.getDbName();
		String gwasTableName = curGWASdbInfo.getTableName();
		log.writeOutFile("######## Running on " + gwasDbName + "\t" + gwasTableName);

		

		
		// iterate over each gene query list
		for (String origList : init.getGeneLists().keySet()){

			
			///////////////
			//////// estimate probability of hit

			//////// gather informations needed
			// get threshold
			Double thresh = curGWASdbInfo.getListThresh().get(origList);

			// estimate probability
			
			double probHit = binom.estimateProb(thresh);
			
			
			///////////////////
			//////// get length of query gene list
			int lengthInputList = init.getGeneLists().get(origList).size();


			//////////////
			//////// perform random draw of binomial distributed variables
			// Instantiate random generator
			List<Integer> histogram = binom.simulate(lengthInputList, probHit, numberOfIterations);

			
			
			///////////////
			//// prepare plotting

			// get hits from lookup
			int actualFindings = curGWASdbInfo.getHitsPerList().get(origList);  

			//prepare outName of graphs Validation graphs
			String outName = gwasDbName + "-" + gwasTableName + "-" + origList;
			double pVal = binom.cummulativeBinom(probHit, lengthInputList, actualFindings);

			String legend = "pVal = " + String.format(Locale.US, "%.2e", pVal);
			PlotHistogram plotter = new PlotHistogram(init);
			plotter.plotHist(histogram, actualFindings, tmpDir, outName, validationDir, legend, thresh);

			// print out probHit on screen and in file
			if (init.getGwasOptions().isGetProp()){
				System.out.println("p(Hit) " + gwasDbName + " " + gwasTableName + " " + origList + " = " + String.format("%6.2e", probHit));
				log.writeFile("p(Hit) " + gwasDbName + " " + gwasTableName + " " + origList + " =  " + String.format("%6.2e", probHit));
			}

			
			
		}
		
		
		// delete temp dir if not specified to keep
		if (!init.getGwasOptions().getCmd().hasOption("keepTmp")){
			rmDir(tmpDir);
		}
	}



	
	
	
	
	
	
	
	
	
	
	//////// Estimate enrichment by drawing random lists and iterate over program.
	// (2)
	public void randomDraw(ExtractSNPMain extractMain) {


		// make log entry that randomDraw validation started
		log.writeOutFile("######## Starting validation with randomRepeats. ########\n");

		// create multimap containing of original list name as key, and all random lists as values
		Multimap<String, Map<String, GeneInfo>> randomListsForAllOriginalLists = LinkedListMultimap.create();

		// instanciate RandomDraw
		RandomDraw random = new RandomDraw(log);


		
		
		
		
		
		//////////////////////
		//////// for each list draw random list of length list

		// check if input file was bed file, it so draw from given reference bed file 
		if (options.isBedFile()) {

			// read in reference bed file
			String bedPath = options.getReference();
			Map<String, GeneInfo> referenceBed = new HandleFiles().readBed(bedPath);

			

			// iterate over each original list
			for (String origList : init.getGeneLists().keySet()){
			
				// get length of list
				int lengthCurrentList = init.getGeneLists().get(origList).size();


				// make random draw based on given reference list
				long seed = -1;
				if (options.getCmd().hasOption("seed")) {
					seed = options.getSeed();
				}

				// draw lists
				random.drawFromReference(referenceBed, randomListsForAllOriginalLists, lengthCurrentList, numberOfIterations, origList, seed, false);

			}			
		
			//// draw random list from gene databsae as reference
		} else {
			for (String origList : init.getGeneLists().keySet()){

				// get length of list 
				int lengthCurrenList = init.getGeneLists().get(origList).size(); 

				//// Create random lists
				// check if seed is given, else set to -1
				long seed = -1;
				if (options.getCmd().hasOption("seed")){
					seed = options.getSeed();
				}
				// draw lists
				random.drawInMap(lengthCurrenList, numberOfIterations, origList, randomListsForAllOriginalLists, seed, init.getReadGenes(), true);

			}
		}

		

		
		
		
		//////////////////////////////////////
		//////// for current GWAS database and each gene query list run program on random lists  
		
		//// create temporary folder and folder for graphs
		String tmpDir = init.getGwasOptions().getDir()+ System.getProperty("file.separator") + "tmp";
		String validationDir = init.getGwasOptions().getDir()+ System.getProperty("file.separator") + "graphs";
		mkDir(tmpDir);
		mkDir(validationDir);

		
		// user information about progress
		
		String gwasDbName = curGWASdbInfo.getDbName();
		String gwasDbTableName = curGWASdbInfo.getTableName();
		log.writeOutFile("######## Running on " + gwasDbName +" " + gwasDbTableName);

		
		
		// get gwas data to pass on
		ReadInGwasData gwasData = init.getGwasData();

		// init extraction class
		ExtractDataMethods extract = new ExtractDataMethods(init);
		extract.setVerbose(false);
		
		
		
		///////////
		/* 
		 * for each original list iterate over each randomly generated list;
		 * get independent SNPs, get threshold and extract SNPs to get number of hits in list
		 */
		
		for (String origList : randomListsForAllOriginalLists.keySet()){


			// init variable for validation results
			LinkedList<Integer> histogram = new LinkedList<>();
			double thresh = 0;

			// counter to give user information about progress
			int counter = 1;
			log.writeOutFile("\nRunning on " + origList);
			
			

			// iterate over each randomly drawn list
			for (Map<String, GeneInfo> currentRandomList : randomListsForAllOriginalLists.get(origList)){

				
				// inform user about progression, not log fi
				log.writeOutFile("Iteration list " + counter + "/" + numberOfIterations);
				counter++;

				// run validation on each randomly drawn list 
				// set variable 

				// check if information for genes are available if not bed-file based
				if ( !options.isBedFile()) {
					List<String> nonGoodGenes = new LinkedList<>();
					for (String curGene : currentRandomList.keySet()) {
						if (! extract.checkGene(curGene)) {
							nonGoodGenes.add(curGene);
						}
					}
					
					// remove genes with no information from hash map
					for (String curGene : nonGoodGenes) {
						currentRandomList.remove(curGene);
					}
				}
				
				
				
				
				// extract the number of independent SNPs
				extract.extractIndep(currentRandomList);

				
				
				// get threshold for each gene
				CreateThresh createThresh = new CreateThresh(init, gwasData);
				createThresh.choose(extract, currentRandomList);
				
				thresh = createThresh.getThresh();

				
				// extract snps with pval lower then threshold
				// create result hash containing extracted pvals
				extract.extractSNPs(gwasData, currentRandomList);

				// save number of hits for later plotting
				int count = 0;
				for (String gene : currentRandomList.keySet()){
					if (currentRandomList.get(gene).isHasHit()){
						count++;
					}
				}
				
				// add to list each run the number of hits detected. Collecting for producing histogram. 
				histogram.add(count);
				
			}
			
			//////////
			//////// get information to calculate pVal based on binom dist

			// get threshold
			thresh = curGWASdbInfo.getListThresh().get(origList);

			// exclude enrichment p-val if bed file is used else need to implement that entire background is read in.
			double probHit = 0;
			int lengthList = 0;
			if (!options.isBedFile()) {

				// estimate probability
				probHit = new Binomial(init, gwasData).estimateProb(thresh); 

				//////// get length of gene list
				lengthList = init.getGeneLists().get(origList).size();
			}

			///////////////
			//// prepare plotting

			// get hits from lookup
			int actualFindings = curGWASdbInfo.getHitsPerList().get(origList);  


			//prepare outName of graphs Validation graphs
			String databaseName = curGWASdbInfo.getDbName();
			String tableName = curGWASdbInfo.getTableName();
			String outName = databaseName + "-" + tableName + "-" + origList;
			//calculate p-val if not running in bed file mode
			String legend;
			if (options.isBedFile()) {
				legend = "";
			} else {
				double pVal = new Binomial(init, gwasData).cummulativeBinom(probHit, lengthList, actualFindings);
				legend = "pVal = " + String.format(Locale.US, "%.2e", pVal);
			}
			 

			PlotHistogram plotter = new PlotHistogram(init);
			plotter.plotHist(histogram, actualFindings, tmpDir, outName, validationDir, legend, thresh);


		}


		// delete temp dir if not specified to keep
		if (!init.getGwasOptions().getCmd().hasOption("keepTmp")){
			rmDir(tmpDir);
		}
	}



		


	
	
	
	
	
	
	
	
	
	
	
	////////////////
	//////// create dir
	private void mkDir(String dir) {

		try {
			FileUtils.forceMkdir(new File(dir));
		} catch (IOException e) {
			log.writeError("## An error occured while generating directory" + dir + ".");
			System.exit(1);
		}


	}

	////////////////
	//////// remove dir 
	private void rmDir (String dir){
		try {
			FileUtils.forceDelete(new File(dir));
		} catch (IOException e) {
			log.writeError("## Warning: couldn't remove " + dir + "!");	}
	}



	///////////////
	//////// Getters








}
