package de.gentos.general.misc;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

public class generalMethods {
	///////////////////////////
	//////// variables ////////
	///////////////////////////

	/////////////////////////////
	//////// constructor ////////
	/////////////////////////////

	/////////////////////////
	//////// methods ////////
	/////////////////////////

	
	
	////////////
	//////// sort hash according to the values
	public Map<String, Double> sortMapByValue(Map<String, Double> map, Boolean inverse) {
		// Convert map with entries to list
		List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(map.entrySet());


		// sort by values
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {

			public int compare(Map.Entry<String, Double> m1, Map.Entry<String, Double> m2) {
				return (m2.getValue()).compareTo(m1.getValue());
			}
		});

		// reverse sort order to start with smallest if needed
		List<Map.Entry<String, Double>> tempList;

		if (inverse == true){
			tempList = Lists.reverse(list);
			list = tempList;
		}
		
		// transfer sorted list to linked hash map
		Map<String, Double> result = new LinkedHashMap<String, Double>();
		for (Map.Entry<String, Double> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		
		// return sorted map
		return result;
	}

	
	
	/////////////////////////////////
	//////// getter / setter ////////
	/////////////////////////////////
}
