package de.gentos.gwas.initialize;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.primitives.Ints;

import de.gentos.general.files.Database;
import de.gentos.general.files.HandleFiles;
import de.gentos.general.files.ReadInGeneDB;
import de.gentos.gwas.initialize.data.GeneInfo.ROI;
import de.gentos.gwas.initialize.data.SnpLine;
import de.gentos.gwas.initialize.options.GetGwasOptions;

public class ReadInGwasData {

	//////////////
	//////// set variables

	InitializeGwasMain init;
	HandleFiles log;
	GetGwasOptions options;
	String tableGene;
	String dbGene;
	Map<String, List<SnpLine>> gwasSnps = new HashMap<>();

	Integer[] correctChr = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
			12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22};
	final LinkedList<String> header = new LinkedList<>();








	//////////////
	//////// constructor

	public ReadInGwasData(InitializeGwasMain init) {

		// set variables
		this.init = init;
		this.options = init.getGwasOptions();
		this.log = init.getLog();
	}







	/////////////////////////
	//////// methods ////////
	/////////////////////////
	
	////////////////////////////////////////
	//////// read in GWAS file in case of corresponding bed file
	
	public void readGwasFileBed(String currentDB, String currentTable, String curGeneList) {
		
		// set column names
		String colRsID = options.getColrsID();
		String colChr = options.getColChr();
		String colPval = options.getColPval();
		String colPos = options.getColPos();

		// connect to database
		Database db = new Database(currentDB, log);
		
		log.writeOutFile("Reading in GWAS database " + currentDB + " ROI wise.");		
		
		/* 
		 * for each gene within bed file 
		 * 		extract ROI
		 * 		for each ROI
		 * 			get GWAS summary SNPs for this region
		 * 			save SNPs in has
		*/
		
		
		/////////////////
		//////// iterate for the different genes listed in the current bed file
		for (String curGene : init.getGeneLists().get(curGeneList).keySet()) {


			// prepare linked list to collect all SNP informations from the different ROIs
			LinkedList<SnpLine> curGeneSnps = new LinkedList<>();
			
			/////////////////////
			//////// iterate for each ROI listed for the current gene
			for (ROI curRoi : init.getGeneLists().get(curGeneList).get(curGene).getRois()) {

				// gather ROI informations
				int chr = curRoi.getChr();
				int start = curRoi.getStart();
				int stop = curRoi.getStop();

				
				
				// prepare query to extract all SNPs in current ROI
				String query = "select * from " + currentTable + " where " + colChr + " == '" + chr 
						+ "' and " + colPos + " > " + start + " and " + colPos + " < " + stop + " order by " 
						+ colPos + " asc";

				// execute query and save all entries
				ResultSet rs = db.select(query);

				
				///////////////////////
				//////// extract meta information (columns, header)
				
				
				// extract column numbers of rsid, chr, pos, pval
				int[] colNumbers = new int[4];
				ResultSetMetaData rsmd;
				try {
					rsmd = rs.getMetaData();
					for (int i = 1 ; i <= rsmd.getColumnCount(); i++ ) {

						if (colRsID.equals(rsmd.getColumnName(i))) {
							colNumbers[0] = i;
						} else if (colChr.equals(rsmd.getColumnName(i))){
							colNumbers[1] = i;
						}else if (colPos.equals(rsmd.getColumnName(i))){
							colNumbers[2] = i;
						}else if (colPval.equals(rsmd.getColumnName(i))){
							colNumbers[3] = i;
						}
					}


					// add header names to header array
					if (header.isEmpty()){
						header.add(colRsID);
						header.add(colChr);
						header.add(colPos);
						header.add(colPval);
						for (int currentCol = 1 ; currentCol <= rsmd.getColumnCount(); currentCol++) {
							if (!Ints.contains(colNumbers, currentCol)) {
								header.add(rsmd.getColumnName(currentCol));
							}
						}
					}

					////////////////////////
					//////// collect SNP line informations
					
					// read in results line wise 
					while (rs.next()) {
						
						// extract rsId, chr, pos and pVal from results
						String curRsId = rs.getString(colNumbers[0]);
						Integer curChr = rs.getInt(colNumbers[1]);
						Integer curPos = rs.getInt(colNumbers[2]);
						Double curPval = rs.getDouble(colNumbers[3]);
						
						
						// extract the remaining columns
						LinkedList<String> remainingColumns = new LinkedList<>();
						for (int currentCol = 1 ; currentCol <= rsmd.getColumnCount(); currentCol++) {
							if (!Ints.contains(colNumbers, currentCol)) {
								remainingColumns.add(rs.getString(currentCol));
							}
						}
							
						// save current line in SNP line object
						curGeneSnps.add(new SnpLine(curRsId, curChr, curPos, curPval, remainingColumns));
					}
					
					
					

				} catch (SQLException e) {
					log.writeError("An error occured querying the gene DB during data extraction.");
					System.exit(1);
				}
			}
			
			
			
			// add gene as key and linked list with gene information to hash for collection GWAS SNP informations
			gwasSnps.put(curGene, curGeneSnps);
		}

	}
	
	
	
	
	
	
	/////////////////////////
	//////// read GWAS file for the case of a plain gene list
	public void readGWASFileStandard(String currentDB, String currentTable, ReadInGeneDB genes) {

		// set column names
		String colRsID = options.getColrsID();
		String colChr = options.getColChr();
		String colPval = options.getColPval();
		String colPos = options.getColPos();

		// connect to DB
		Database db = new Database(currentDB, log);



		//only included for debugging purpose to load less chromosomes
		Integer[] correctChr = new Integer[1];
		correctChr[0] = 14;
		//		correctChr[0] = 6;
		//		correctChr[1] = "13";
		//		correctChr[2] = "4";
		//		correctChr[3] = 12
		System.out.println("\n##################### WARNIG: fixed chromosome for debugging only. No Real run.\n");


		// make log entry about db and table to read data from
		log.writeOutFile("Reading in data from: " + currentDB + "\t" + currentTable);

		// extract data chr wise and sort to corresponding gene
		for ( Integer currentChr : correctChr) {

			// print out status information
			log.writeOutFile("Reading in chromosome " + currentChr + ".");

			// create query to extract information needed for sorting

			String query = "select * from " + currentTable + " where " + colChr + 
					" == '" + currentChr + "' order by " + colPos + " asc";



			// execute query
			ResultSet rs = db.select(query);

			Collection<String> genesOnChrom = genes.getChrGene().get(currentChr);
			int sizeGenelist = genesOnChrom.size();



			/* 
			 * sort SNPs to gene
			 * for each line from DB add all genes to list with starting position <= pos
			 * save gene info for those genes
			 * if pos > stop exclude gene from list >> no iteration over entire gene list
			 */ 

			// set counter to run through genes in parallel with increasing snp positions
			int counter = 0;
			ArrayList<String> currentList = new ArrayList<String>(); 

			try {


				// extract column numbers of column not rsid, chr, pos, pval to add to list after desired 4 columns 
				int[] colNumbers = new int[4];
				ResultSetMetaData rsmd = rs.getMetaData();
				for (int i = 1 ; i <= rsmd.getColumnCount(); i++) {
					if (colRsID.equals(rsmd.getColumnName(i))){
						colNumbers[0] = i;
					} else if (colChr.equals(rsmd.getColumnName(i))){
						colNumbers[1] = i;
					}else if (colPos.equals(rsmd.getColumnName(i))){
						colNumbers[2] = i;
					}else if (colPval.equals(rsmd.getColumnName(i))){
						colNumbers[3] = i;
					}
				}

				// add header names to header array
				if (header.isEmpty()){
					header.add(colRsID);
					header.add(colChr);
					header.add(colPos);
					header.add(colPval);
					for (int currentCol = 1 ; currentCol <= rsmd.getColumnCount(); currentCol++) {
						if (!Ints.contains(colNumbers, currentCol)) {
							header.add(rsmd.getColumnName(currentCol));
						}
					}
				}


				while (rs.next()){



					// extract current position, for comparison

					int pos;
					try {
						pos = Integer.valueOf(rs.getString(colPos));
					} catch (NumberFormatException e) {
						System.out.println("##WARNING " + colPos + " does not only contain numbers. Non numeric value skiped!");
						continue;
					}


					// in list of current interesting genes check if a gene has stop < pos if so, then exclude it from list  
					for (Iterator<String> iterator = currentList.iterator(); iterator.hasNext();){

						// extract stop position and gene to test for
						String geneToTest = iterator.next();
						int stop = genes.getGeneListMap().get(geneToTest).getStop();

						// check if position is larger then stop. If so exclude gene from loop array
						if (pos > stop) {
							iterator.remove();
						}
					}



					// for each SNP if start < pos < stop save to currentList, AND if start > pos stop and save SNP to gene
					// this way there is no need to iterate over entire gene list, but only over genes with suitable positions
					boolean check = false;
					while (check == false) {

						// avoid out of bound error since last gene already tested and upcoming SNPs beyond genes
						if (counter < sizeGenelist){

							// take next gene and check if SNP is in gene > save to current list
							// extract needed information for readability
							String currentGene = genesOnChrom.toArray()[counter].toString();
							int start = genes.getGeneListMap().get(currentGene).getStart();
							int stop = genes.getGeneListMap().get(currentGene).getStop();

							// if start < pos < stop (in gene) save in list. Else set check to true to check gene on next SNP
							if (pos >= start) {
								if (pos <= stop){
									currentList.add(currentGene);
								}
								// increment counter to check on next gene on next iteration
								counter++;
							} else {
								check = true;
							}
						} else {
							// if pos > start and pos > stop break current loop -> iterate over next SNP
							break;
						}
					}

					
					
					//////////
					/////// create list containing SNP info and add list to hash 
					List<String> remainingColumns = new LinkedList<>();

					// add the remaining columns
					for (int currentCol = 1 ; currentCol <= rsmd.getColumnCount(); currentCol++) {
						if (!Ints.contains(colNumbers, currentCol)) {
							remainingColumns.add(rs.getString(currentCol));
						}
					}
					SnpLine line = new SnpLine(rs.getString(colRsID), currentChr, rs.getInt(colPos),
							rs.getDouble(colPval), remainingColumns);


					// add to hash GWAS info object with gene as key
					for (String gene : currentList) {
						List<SnpLine> list = gwasSnps.get(gene);
						if (list == null) {
							list = new LinkedList<>();
							gwasSnps.put(gene, list);
						}
						list.add(line);
					}
				}
			} catch (SQLException e) {
				log.writeError("An error occured querying the gene DB during data extraction.");
				System.exit(1);
			}
		}
	}





	////////////////
	//////// getter


	public Map<String, List<SnpLine>> getGwasSnps() {
		return gwasSnps;
	}

	public LinkedList<String> getHeader() {
		return header;
	}





}
