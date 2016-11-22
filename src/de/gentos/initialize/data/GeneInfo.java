package de.gentos.initialize.data;


public class GeneInfo {

	///////////////
	//////// set variables
	
	private Integer chr;
	private Integer start;
	private Integer stop;
	
	
	

	
	///////////////
	//////// constructor
	



	public GeneInfo(Integer chr, Integer start, Integer stop) {
		this.chr = chr;
		this.start = start;
		this.stop = stop;
	}

	
	//////////////////
	//////// getter setter
	
	public Integer getChr() {
		return chr;
	}
	
	public void setChr(Integer chr) {
		this.chr = chr;
	}

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


	
}

