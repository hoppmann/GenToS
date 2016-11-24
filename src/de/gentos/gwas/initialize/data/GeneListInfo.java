package de.gentos.gwas.initialize.data;

import java.util.LinkedList;
import java.util.List;

public class GeneListInfo {

	private List<SnpLine> snpHits = new LinkedList<>();
	private SnpLine lowPvalSNP;
	private boolean hasHit;
	private Integer GwasSNPs;
	private double threshold;
	private String method;
	private int indepSNPs;









	//////// Methods
	// recive a new snp to save in list
	public void addSnpLine(SnpLine snp) {
		snpHits.add(snp);
	}

	///////////////////
	//////// Getter Setter	



	

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





}
