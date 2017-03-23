package de.gentos.gwas.threshold;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.gentos.general.files.ReadInGeneDB;
import de.gentos.gwas.initialize.InitializeGwasMain;
import de.gentos.gwas.initialize.ReadInGwasData;
import de.gentos.gwas.validation.Binomial;

public class MaxEnrichment {

	////////////////
	//////// set variables
	InitializeGwasMain init;
	ReadInGeneDB readGenes;
	ReadInGwasData gwasData;


	public MaxEnrichment(InitializeGwasMain init, ReadInGeneDB readGenes,
			ReadInGwasData gwasData) {
		this.init = init;
		this.readGenes = readGenes;
		this.gwasData = gwasData;
	}





	/////////////
	//////// Methods


	// Draw random genes
	// iterate to max
	// for thresh estimate zscore
	//
	//

	public void maximize( ArrayList<String> queryGenes) {

		// iterate over thresholds
		//		Double thresh = 0.0001;

		// as inital borders test start of getting  smallest hitting pval
		//		double lowerBorder = smallesPval();
		double lowerBorder = 0;
		double upperBorder = 1;

		// init compare zScore and set break condition
		double zScoreComp = -100000;
		double abort = 0.001;
		double diff = 1;
		int counter = 0;
		int decimal = 1;






		// instanciate binomial class
		Binomial binom = new Binomial(init, gwasData);

		// while abbortion criteria is not reached iterate to find max enrichment
		do {

			// get list of 10 thresholds to check for max enrichment
			List<Double> threshList = makeThreshList(lowerBorder, upperBorder, decimal);

			System.out.println(threshList);

			for (double thresh : threshList){

				//////// Gather needed information for estimating enrichment
				// estimate probability
				double probHit = binom.estimateProb(thresh);

				// get length of list
				int lengthList = queryGenes.size();

				// get binomila distribution
				List<Integer> binomHits = binom.simulate(lengthList, probHit, 20000);


				// get number of real hits depending on threshold
				int hits = binom.extractHits(thresh, queryGenes);

				// calculate zScore
				Double zScore = binom.zscore(binomHits, hits);



//				System.out.println("Hits in Lists " + hits);
				System.out.println(probHit);
				System.out.println("Binom mean " + binom.mean(binomHits));
				System.out.println("Z-Score " + zScore);
				System.out.println("Z-Score comp " + zScoreComp);
				System.out.println("Threshold " + thresh);
				System.out.println("Counter " + counter);
				System.out.println("Difference " + (zScore - zScore));
				System.out.println("Upper border " + upperBorder);
				System.out.println("Lower border " + lowerBorder);
				System.out.println("");


				
				// check if current zScore is bigger than last zscore. 
				// if so remember variables, and check if next two iterations there isn't an increase either
				// if so break and do next iteration

				// if 
				if (zScore > zScoreComp){
					upperBorder = thresh;
					zScoreComp = zScore;
				} else if (zScore <= zScoreComp) {
					lowerBorder = thresh;
					counter++;
					if (counter == 2){
						counter = 0;
						break;
					}
				}
				
				
			}

		} while (diff > abort && upperBorder != lowerBorder);

		System.out.println("DONE");
		System.exit(3);
	}
				
				
				
				
//				if (zScore > zScoreComp) {
//					System.out.println("Z-Score " + zScore);
//					System.out.println("Upper border " + upperBorder);
//					System.out.println("");
//					//					if (counter == 0) {
//					diff = zScore - zScoreComp;
//					zScoreComp = zScore;
//					upperBorder = thresh;
//					//						System.out.println("Difference " + diff);
//					break;
//					//						counter++;
//					//					} else if (counter == 3){
//					//						counter = 0;
//					//						break;
//					//					} else {
//					//						counter++;
//					//					}
//					//				} else if ( zScore < zScoreComp) {
//					//					break;
//					//					// if zScore not greater then old mark current thresh as lower border for thresh definition next round
//					//					lowerBorder = thresh;
//					//					counter = 0;
//				}
			








	//////// generate a list of thresholds to testIterate over
	public List<Double> makeThreshList(double lowerBorder, double upperBorder, int decimal) {

		// init variable for rounding 
		double rounding = 100d * decimal;
		
		// set stepsize to devide threshold in
		int stepSize = 10;
		
		// init return variable
		List<Double> threshList = new LinkedList<>();

		double stepLength = (upperBorder - lowerBorder) / stepSize;

		for ( int i = 0; i <= stepSize; i++) {
			threshList.add(Math.round(rounding * (lowerBorder + i * stepLength))/rounding);
		}

//		Collections.reverse(threshList);
		Collections.sort(threshList);
		return threshList;

	}



//
//
//
//
//	//////// estimate the probability of getting a hit by chance
//	public double estimateProb(double thresh) {
//
//		// for each gene extract lowest Pval 
//		List<Double> lowesPvalPerGene = new ExtractData(init).extractLowesPvalPerGene(gwasData);
//
//		// get total number of genes in gwas
//		int totalNumberOfGenesInGewas = gwasData.getGeneSNP().keySet().size();
//
//		// extract number of genes with pval < thresh
//		Collections.sort(lowesPvalPerGene);
//		int possibleHitGenes = 0;
//		while ((possibleHitGenes < totalNumberOfGenesInGewas ) && (lowesPvalPerGene.get(possibleHitGenes) <= thresh)) {
//			possibleHitGenes++;
//		}
//
//
//		// probability of hit is equal to possibleHitGenes / totalNumberGenesInGwas 
//		double probHit = possibleHitGenes / (double) totalNumberOfGenesInGewas;
//
//		return probHit;
//	}
//
//
//
//	//////// perform random draw of binomial distributed variables
//	public List<Integer> binomial(int lengthList, double probHit, int numberIterations) {
//
//
//		// instanciate random generator
//		RandomGenerator rng = new MersenneTwister();
//
//		///////// simulate binomial draw
//		List<Integer> numberHits = new LinkedList<>();
//		for (int i = 0; i < numberIterations; i++){
//			BinomialDistribution binom = new BinomialDistribution(rng, lengthList, probHit);
//			numberHits.add(binom.sample());
//		}
//
//		// return list of integer representing hits
//		return numberHits;
//	}
//
//
//
//
//	//////// calculate z-score from distribution an value
//	public double zscore(List<Integer> distribution, Integer hit) {
//
//		// gather needed numbers
//		double mean = mean(distribution);
//		double distance = hit - mean;
//		double deviation = deviation(distribution);
//
//		// calculate zScore
//		double zScore = distance / deviation;		
//		return zScore;
//
//	}
//
//
//
//	//// caclulate standard deviation
//	public double mean(List<Integer> distribution) {
//
//		// sum up vlues
//		int sum = 0;
//		for (int i : distribution){
//			sum += i;
//		}
//
//		// return mean
//		return sum/ (double) distribution.size();
//
//	}
//
//	//// calculate standard deviation
//	public double deviation(List<Integer> distribution) {
//
//		// init variable
//		double deviation = 0;
//
//		// get mean
//		double mean = mean(distribution);
//
//		// calc standard deviation
//		for (int number : distribution){
//			deviation +=  (mean - number) * (mean - number);
//		}
//		deviation = deviation / (double) distribution.size();
//		deviation = Math.sqrt(deviation);
//
//		return deviation;
//
//	}
//
//	// extract smallest Pval in file
//
//	public double smallesPval(){
//
//		// init variable
//		double smallestPval = 1;
//
//		// for each gene; for each SNP save if pval < smallestPval
//		for (String gene : init.getReadGenes().getAllGeneNames()) {
//			// for each SNP: if pval < threshold save data to hash
//			if (!(gwasData.getGeneSNP().get(gene) == null)) {
//
//
//				for (SnpLine currentSNP : gwasData.getGeneSNP().get(gene)) {
//					Double pval = currentSNP.getpValue();
//
//					// if pval smaller thresh mark as hit go to next gene
//					if ( pval < smallestPval) {
//						smallestPval = pval;
//					}
//				}
//			}
//		}
//
//		return smallestPval;
//	}
//
//	// get number of actual hist depending on threshold and queryGenes
//	public Integer extractHits(double thresh, List<String> queryGenes) {
//
//		// init variable
//		int numberRealHits = 0;
//
//		// for each gene in queryList check if a snp has pVal < thresh
//		for (String gene : queryGenes){
//
//			// for each SNP: if pval < threshold save data to hash
//			if (!(gwasData.getGeneSNP().get(gene) == null)) {
//
//
//				for (SnpLine currentSNP : gwasData.getGeneSNP().get(gene)) {
//					Double pval = currentSNP.getpValue();
//
//					// if pval smaller thresh mark as hit go to next gene
//					if ( pval < thresh) {
//						numberRealHits++;
//						break;
//					}
//				}
//			}
//		}
//
//		return numberRealHits;
//
//	}
}
