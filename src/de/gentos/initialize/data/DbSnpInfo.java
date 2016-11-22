package de.gentos.initialize.data;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import de.gentos.initialize.ReadInGwasData;

public class DbSnpInfo {

	
	
	private String dbPath;
	private String dbName;
	private String tableName;
	private ReadInGwasData gwasData;
	private Map<String, Double> listThresh = new HashMap<>();
	private Map<String, Integer> hitsPerList = new HashMap<>();
	
	
	
	
	////////////////
	//////// Constructor

	public DbSnpInfo(String dbPath, String tableName) {
		super();
		this.dbPath = dbPath;
		this.dbName = getName(dbPath);
		this.tableName = tableName;
	}


	
	
	
	
	
	////////////
	//////// methods
	
	// get name prefix of dbName
	private String getName(String path) {
		String name = FilenameUtils.getBaseName(path);
		return name;
	}
	
	// add list and thresh to map
	public void addToMap(String listName, Double thresh) {
		
		listThresh.put(listName, thresh);
		
	}
	
	//add value to hitsPerList
	public void putHitsPerList(String currentListName, int counter) {
		this.hitsPerList.put(currentListName, counter);
		
	}

	
	
	
	
	/////////////////////
	//////// getter // setter
	
	
	
	public String getDbPath() {
		return dbPath;
	}


	public void setDbPath(String dbPath) {
		this.dbPath = dbPath;
	}


	public String getDbName() {
		return dbName;
	}


	public void setDbName(String dbName) {
		this.dbName = dbName;
	}


	public String getTableName() {
		return tableName;
	}


	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public ReadInGwasData getGwasData() {
		return gwasData;
	}
	
	public void setGwasData(ReadInGwasData gwasData) {
		this.gwasData = gwasData;
	}
	
	public Map<String, Double> getListThresh() {
		return listThresh;
	}

	public void setListThresh(Map<String, Double> listThresh) {
		this.listThresh = listThresh;
	}

	public Map<String, Integer> getHitsPerList() {
		return hitsPerList;
	}

	public void setHitsPerList(Map<String, Integer> hitsPerList) {
		this.hitsPerList = hitsPerList;
	}


	
}
