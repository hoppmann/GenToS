package de.gentos.geneSet.lookup;

import de.gentos.general.files.HandleFiles;

public class FisherTest {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	private double[] factories;
	private int maxSize;
	private HandleFiles log; 



	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public FisherTest(int maxSize, HandleFiles log) {

		this.maxSize = maxSize;
		this.log = log;

		// create array of double containing all possibly needed factories to increase speed
		// work in log space
		factories = new double[maxSize + 1];
		for (int i = 1; i <= this.maxSize; i++){
			factories[i] = factories[i-1] + Math.log(i);
		}

	}




	/////////////////////////
	//////// methods ////////
	/////////////////////////




	// calculates the P-value for this specific state
	// @param a,b,c,d are the four cells in a 2x2 matrix
	// @return the P-value



	public double getP(int a11, int b21, int c12, int d22){

		// init and precalculate variables
		int n = a11 + b21 + c12 + d22;
		double p;

		// check that the factories used aren't greater then the one precalculated
		if (n > maxSize) {
			log.writeError("An ERROR occured calculating the fisher exact test.");
			System.exit(1);
		}

		p = (factories[a11+b21]+factories[c12+d22]+factories[a11+c12]+factories[b21+d22])
				-(factories[a11]+factories[b21]+factories[c12]+factories[d22]+factories[n]);


		// return pval in normal state
		return Math.exp(p);
	}







	// calculates the one tail P-value for the Fisher Exact test
	// @param a,b,c,d are the four cells in a 2x2 matrix
	// @return the P-value

	public double getCumulativevP (int a11, int b21, int c12, int d22) {
		double cumPVal = 0;
		int min, i;
		int n = a11 + b21 + c12 + d22;

		// check that the factories used aren't greater then the one precalculated
		if (n > maxSize) {
			log.writeError("An ERROR occured calculating the fisher exact test.");
			System.exit(1);
		}

		/*
		 *  check if enrichment or depleation needs less calculations
		 * take the one with fewer steps (speed issue)
		 */

		// check if enrichment is faster
		if((a11*d22)>=(b21*c12)) {
			cumPVal += getP(a11, b21, c12, d22);

			// get minimum
			// min = If()?then:else;
			min=(c12<b21)?c12:b21;
			for(i=0; i<min; i++) {
				cumPVal+=getP(++a11, --b21, --c12, ++d22);
			}

			// else calculate depleation with inverse P
		} else {
			min=(a11<d22)?a11:d22;
			for(i=0; i<min; i++) {
				cumPVal+=getP(--a11, ++b21, ++c12, --d22);
			}

			// inverse p-val
			cumPVal = 1 - cumPVal;
		}

		// return pvalue
		return cumPVal;
	}


	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////


}
