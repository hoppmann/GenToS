package de.gentos.lists.lookup;

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

		
		// calculate cumulative pVal
		cumPVal += getP(a11, b21, c12, d22);

		if((a11*d22)>=(b21*c12)) {
			min=(c12<b21)?c12:b21;
			for(i=0; i<min; i++) {
				cumPVal+=getP(++a11, --b21, --c12, ++d22);
			}
		}
		
		if((a11*d22)<(b21*c12)) {
			min=(a11<d22)?a11:d22;
			for(i=0; i<min; i++) {
				cumPVal+=getP(--a11, ++b21, ++c12, --d22);
			}
		}

		// return pvalue
				return cumPVal;
	}


	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////




	//		  /**
	//		   * calculates the one tail P-value for the Fisher Exact test
	//		   * This
	//		   *
	//		   * @param a,b,c,d are the four cells in a 2x2 matrix
	//		   * @return the P-value
	//		   */
	//		  public final double getCumlativeP(int a, int b, int c, int d) {
	//		    int min,i;
	//		    int n=a+b+c+d;
	//		    if(n>maxSize)
	//		      {return Double.NaN;}
	//		    double p=0;
	//		    p+=getP(a, b, c, d);
	//		    if((a*d)>=(b*c))
	//		      {min=(c<b)?c:b;
	//		      for(i=0; i<min; i++)
	//		        {p+=getP(++a, --b, --c, ++d);}
	//		      }
	//		    if((a*d)<(b*c))
	//		      {min=(a<d)?a:d;
	//		      for(i=0; i<min; i++)
	//		        {p+=getP(--a, ++b, ++c, --d);}
	//		      }
	//		    return p;
	//		  }
	//
	//		}
}
