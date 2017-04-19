package de.gentos.geneSet.initialize.data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InfoData {
	///////////////////////////
	//////// variables ////////
	///////////////////////////
	int numberEnrichedResources;
	int numberGenesInInput;
	List<String> enrichedResources;	
	private Set<String> enrichedGenes;
	private Set<String> nonEnrichedGenes;


	
	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	public InfoData() {
		
		enrichedGenes = new HashSet<>();
		nonEnrichedGenes = new HashSet<>();

		
	}
	
	
	/////////////////////////
	//////// methods ////////
	/////////////////////////

	public void addEnrichedGene(String geneName, Boolean enriched){
		if (enriched){
			enrichedGenes.add(geneName);
		} else {
			nonEnrichedGenes.add(geneName);
		}
	}

	
	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
	
	
	public int getNumberEnrichedResources() {
		return numberEnrichedResources;
	}
	public void setNumberEnrichedResources(int numberEnrichedResources) {
		this.numberEnrichedResources = numberEnrichedResources;
	}
	public int getNumberGenesInInput() {
		return numberGenesInInput;
	}
	public void setNumberGenesInInput(int numberGenesInList) {
		this.numberGenesInInput = numberGenesInList;
	}
	public List<String> getEnrichedResources() {
		return enrichedResources;
	}
	public void setEnrichedResources(List<String> enrichedResources) {
		this.enrichedResources = enrichedResources;
	}
	public Set<String> getEnrichedGenes() {
		return enrichedGenes;
	}
	public void setEnrichedGenes(Set<String> enrichedGenes) {
		this.enrichedGenes = enrichedGenes;
	}
	public Set<String> getNonEnrichedGenes() {
		return nonEnrichedGenes;
	}
	public void setNonEnrichedGenes(Set<String> nonEnrichedGenes) {
		this.nonEnrichedGenes = nonEnrichedGenes;
	}
	
	
	
	
}
