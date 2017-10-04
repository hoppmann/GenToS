package de.gentos.gwas.initialize.data;

import java.util.LinkedList;
import java.util.List;

public class GeneInfo {

	///////////////
	//////// set variables

	// primary usage when reading in bed file or gene DB
//	private Integer chr;
	private LinkedList<ROI> rois; // region of interests containing start stop pairs

	// secondary usage information obtained during calculations
	private List<SnpLine> snpHits = new LinkedList<>();
	private SnpLine lowPvalSNP;
	private boolean hasHit;
	private Integer GwasSNPs;
	private double threshold;
	private String method;
	private int indepSNPs = 0;
	private boolean hasGeneName = true;



	///////////////
	//////// constructor




	public GeneInfo(Integer chr, Integer start, Integer stop) {
		rois = new LinkedList<>();
		this.rois.add(new ROI(chr, start, stop));
		
	}

	public GeneInfo() {

		rois = new LinkedList<>();

	}


	/////////////////////////
	//////// Methods ////////
	/////////////////////////

	// add a region of interest defined as a pair of start and stop
	public void addRoi(Integer chr, Integer start, Integer stop ) {
		this.rois.add(new ROI(chr, start, stop));
	}

	
	// recive a new snp to save in list
	public void addSnpLine(SnpLine snp) {
		snpHits.add(snp);
	}

	// add up independent SNPs
	public void sumUpIndepSnps(Integer indepSNPs) {
		this.indepSNPs += indepSNPs; 
	}


	
	///////////////////////////////////
	//////// getter and setter ////////
	///////////////////////////////////

	//////////////
	// primary variables
	
//	public Integer getChr() {
//		return chr;
//	}
//
//	public void setChr(Integer chr) {
//		this.chr = chr;
//	}

	public Integer getStart() {
		return rois.getFirst().getStart();
	}

	public void setStart(Integer start) {
		rois.getFirst().setStart(start);
	}

	public Integer getStop() {
		return rois.getFirst().getStop();
	}

	public void setStop(Integer stop) {
		rois.getFirst().setStop(stop);
	}

	public LinkedList<ROI> getRois() {
		return rois;
	}

	public void setRois(LinkedList<ROI> rois) {
		this.rois = rois;
	}


	/////////////////////
	//// secondary variables
	
	public Integer getGwasSNPs() {
		return GwasSNPs;
	}

	public void setGwasSNPs(Integer gwasSNP) {
		GwasSNPs = gwasSNP;
	}

	public double getThreshold() {
		return threshold;
	}
	
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	
	public List<SnpLine> getSnpHits() {
		return snpHits;
	}
	
	public void setSnpHits(List<SnpLine> snpHits) {
		this.snpHits = snpHits;
	}
	
	public SnpLine getLowPvalSNP() {
		return lowPvalSNP;
	}
	
	public void setLowPvalSNP(SnpLine lowPvalSNP) {
		this.lowPvalSNP = lowPvalSNP;
	}

	public boolean isHasHit() {
		return hasHit;
	}

	public void setHasHit(boolean hasHit) {
		this.hasHit = hasHit;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public int getIndepSNPs() {
		return indepSNPs;
	}

	public void setIndepSNPs(int indepSNPs) {
		this.indepSNPs = indepSNPs;
	}
	
	public boolean isHasGeneName() {
		return hasGeneName;
	}

	public void setHasGeneName(boolean hasGeneName) {
		this.hasGeneName = hasGeneName;
	}
	
	
	
	/////////////////////////////
	//////// pairs class ////////
	/////////////////////////////

	public class ROI {
		///////////////////////////
		//////// variables ////////
		///////////////////////////

		private Integer chr;
		private Integer start;
		private Integer stop;


		/////////////////////////////
		//////// constructor ////////
		/////////////////////////////

		public ROI(Integer chr, Integer start, Integer stop) {

			this.chr = chr;
			this.start = start;
			this.stop = stop;

		}

		/////////////////////////
		//////// methods ////////
		/////////////////////////

		/////////////////////////////////
		//////// getter / setter ////////
		/////////////////////////////////

		public Integer getStart() {
			return start;
		}

		public void setStart(Integer start) {
			this.start = start;
		}

		public Integer getStop() {
			return stop;
		}

		public void setStop(Integer stop) {
			this.stop = stop;
		}

		public Integer getChr() {
			return chr;
		}

		public void setChr(Integer chr) {
			this.chr = chr;
		}

		


	}





	




}








