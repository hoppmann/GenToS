package de.gentos.gwas.initialize.data;

import java.util.LinkedList;
import java.util.List;

public class SnpLine {

	///////////////
	//////// set variables
	private String rsid;
	private Integer chr;
	private Integer position;
	private Double pValue;
	private List<String> furtherColumns;
	
	/////////////////
	//////// constructor
	
	public SnpLine(String rsid, Integer currentChr, Integer position, Double pValue, List<String> remainingColumns) {
		this.rsid = rsid;
		this.chr = currentChr;
		this.position = position;
		this.pValue = pValue;
		this.furtherColumns = remainingColumns;
	}
	
	
	
	
	/////////////
	//////// methods
	
	// prepare list of all information for output
	public LinkedList<String> formOutput() {
		
		LinkedList<String> outLine = new LinkedList<>();
		outLine.add(rsid);
		outLine.add(chr.toString());
		outLine.add(position.toString());
		outLine.add(pValue.toString());
		outLine.addAll(furtherColumns);
		
		return outLine;
		
		
	}
	
	
	
	
	
	
	///////////////////
	//////// setter getter
	
	public String getRsid() {
		return rsid;
	}
	
	public void setRsid(String rsid) {
		this.rsid = rsid;
	}
	
	public Integer getChr() {
		return chr;
	}
	
	public void setChr(Integer chr) {
		this.chr = chr;
	}
	
	public Integer getPosition() {
		return position;
	}
	
	public void setPosition(Integer position) {
		this.position = position;
	}
	
	public Double getpValue() {
		return pValue;
	}
	
	public void setpValue(Double pValue) {
		this.pValue = pValue;
	}
		
	public List<String> getFurtherColumns() {
		return furtherColumns;
	}
	
	public void setFurtherColumns(List<String> furtherColumns) {
		this.furtherColumns = furtherColumns;
	}
	
}
