package de.gentos.gwas.validation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import de.gentos.general.files.ReadInGeneDB;
import de.gentos.gwas.getSNPs.ExtractDataMethods;
import de.gentos.gwas.initialize.InitializeGwasMain;
import de.gentos.gwas.initialize.ReadInGwasData;
import de.gentos.gwas.initialize.data.SnpLine;

public class Binomial {




	////////////////
	//////// set variables
	//// Gwas setting
	InitializeGwasMain init;
	ReadInGeneDB readGenes;
	ReadInGwasData gwasData;


	
	//// list setting



	///////////////
	//////// Constructor


	public Binomial(InitializeGwasMain init, ReadInGwasData gwasDataReference) {
		this.init = init;
		this.readGenes = init.getReadGenes();
		this.gwasData = gwasDataReference;
	}

	
	
	////////////
	//////// Methods

	/////// estimate the probability of getting a hit by chance
	public double estimateProb(double thresh) {

		
		
		// for each gene extract lowest Pval 
		List<Double> lowesPvalPerGene = new ExtractDataMethods(init).extractLowestPvalPerGene(gwasData);

		
		// get total number of genes in gwas
		int totalNumberOfGenesInGwas = gwasData.getGwasSnps().keySet().size();

		// extract number of genes with pval < thresh
		Collections.sort(lowesPvalPerGene);
		int possibleHitGenes = 0;
		while ((possibleHitGenes < totalNumberOfGenesInGwas ) && (lowesPvalPerGene.get(possibleHitGenes) <= thresh)) {
			possibleHitGenes++;
		}


		
		// probability of hit is equal to possibleHitGenes / totalNumberGenesInGwas 
		double probHit = possibleHitGenes / (double) totalNumberOfGenesInGwas;

//		System.out.println("number hit genes = " + possibleHitGenes);
		
		return probHit;
		
	}


	
	

	////////perform random draw of binomial distributed variables
	public List<Integer> simulate(int lengthList, double probHit, int numberIterations) {


		// instanciate random generator and if seed option chosen set seed
		RandomGenerator rng = new MersenneTwister();
		if (init.getGwasOptions().getCmd().hasOption("seed")){
			rng.setSeed(init.getGwasOptions().getSeed());
		}

		///////// simulate binomial draw
		List<Integer> numberHits = new LinkedList<>();
		for (int i = 0; i < numberIterations; i++){
			BinomialDistribution binom = new BinomialDistribution(rng, lengthList, probHit);
			numberHits.add(binom.sample());
		}

		// return list of integer representing hits
		return numberHits;
		
	}



	
	
	//////// calculate z-score from distribution an value
	public double zscore(List<Integer> distribution, Integer hit) {

		// gather needed numbers
		double mean = mean(distribution);
		double distance = hit - mean;
		double deviation = deviation(distribution);

		// calculate zScore
		double zScore = distance / deviation;		
		return zScore;

		
	}



	
	
	//////// caclulate mean
	public double mean(List<Integer> distribution) {

		// sum up values
		int sum = 0;
		for (int i : distribution){
			sum += i;
		}

		// return mean
		return sum/ (double) distribution.size();

	}

	
	
	
	
	//////// calculate standard deviation
	public double deviation(List<Integer> distribution) {

		// init variable
		double deviation = 0;

		// get mean
		double mean = mean(distribution);

		// calc standard deviation
		for (int number : distribution){
			deviation +=  (mean - number) * (mean - number);
		}
		deviation = deviation / (double) distribution.size();
		deviation = Math.sqrt(deviation);

		return deviation;

	}

	
	
	
	
	//////// extract smallest Pval in file
	public double smallesPval(){

		// init variable
		double smallestPval = 1;

		// for each gene; for each SNP save if pval < smallestPval
		for (String gene : init.getReadGenes().getAllGeneNames()) {
			// for each SNP: if pval < threshold save data to hash
			if (!(gwasData.getGwasSnps().get(gene) == null)) {


				for (SnpLine currentSNP : gwasData.getGwasSnps().get(gene)) {
					Double pval = currentSNP.getpValue();

					// if pval smaller thresh mark as hit go to next gene
					if ( pval < smallestPval) {
						smallestPval = pval;
					}
				}
			}
		}

		return smallestPval;
	}

	
	
	
	
	
	//////// get number of actual hits depending on threshold and queryGenes
	public Integer extractHits(double thresh, List<String> queryGenes) {

		// init variable
		int numberRealHits = 0;

		// for each gene in queryList check if a snp has pVal < thresh
		for (String gene : queryGenes){

			// for each SNP: if pval < threshold save data to hash
			if (!(gwasData.getGwasSnps().get(gene) == null)) {


				for (SnpLine currentSNP : gwasData.getGwasSnps().get(gene)) {
					Double pval = currentSNP.getpValue();

					// if pval smaller thresh mark as hit go to next gene
					if ( pval < thresh) {
						numberRealHits++;
						break;
					}
				}
			}
		}

		return numberRealHits;
	}



	
	
	
	//////// get the pVal for number of hits by using the cummulative binomial distribution
	public double cummulativeBinom (double probHit, int lengthList, int actualFindings) {
		
		// define variables
		double pVal = 0;
		
		// prepare binomial distribution 
		BinomialDistribution bino = new BinomialDistribution(lengthList, probHit);
		
		// calculate pVal ( p(X) >= x )
		pVal = 1 - bino.cumulativeProbability(actualFindings-1);
		
		
		// return pVal
		return pVal;
	}
	
	
}
