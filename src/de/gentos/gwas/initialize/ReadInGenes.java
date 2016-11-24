package de.gentos.gwas.initialize;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import de.gentos.general.options.gwas.GetGwasOptions;
import de.gentos.gwas.getSNPs.Database;
import de.gentos.gwas.initialize.data.GeneInfo;
import de.gentos.gwas.main.HandleFile;

public class ReadInGenes {



	//////////////
	//////// set variables

	InitializeMain init;
	HandleFile log;
	GetGwasOptions options;
	String tableGene;
	String dbGene;
	Map<String, GeneInfo> geneInfo = new HashMap<>();
	Map<String, String> nonGoodGenes = new HashMap<>(); 
	Multimap<Integer, String> chrGene = LinkedListMultimap.create();
	ArrayList<String> allGeneNames = new ArrayList<>();
	Integer[] correctChr = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
			12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22};






	//////////////
	//////// constructor

	public ReadInGenes(InitializeMain init) {

		// set variables
		this.init = init;
		this.options = init.getGwasOptions();
		this.log = init.getLog();
		this.tableGene = options.getTableGene();
		this.dbGene = options.getDbGene();

		// extract gene info and save them in Hash with gene as key
		readGeneInfo();
	}








	/////////////
	//////// methods



	// extract all gene names and sort to hash
	// 1. key gene : chr, star, stop -> for extraction purpose
	// 2. key chr : gene -> for reading in data choromosome wise
	private void readGeneInfo(){

		// query for all gene info.
		String query = "select * from " + tableGene + " order by start asc";

		// connect to database and send query
		Database connection = new Database(dbGene, init);
		ResultSet rs = connection.select(query);

		// retrieve flanking parameters
		int[] flanking = init.getGwasOptions().getFlank();


		// retrieve gene info and save in hash
		try {
			while (rs.next()){


				// retrieve gene info and add flanking to position
				String gene = rs.getString("gene");
				Integer chr = rs.getInt("chr");
				String chrom = rs.getString("chr");
				Integer start = Integer.valueOf(rs.getString("start")) - flanking[0];
				Integer stop = Integer.valueOf(rs.getString("stop")) + flanking[1];

				// save geneNames of all genes for random sampling
				allGeneNames.add(gene);

				// check to exclude all gonomsomes and save others
				if (Arrays.asList(correctChr).contains(chr)){

					// save data to geneList opject
					GeneInfo genes = new GeneInfo(Integer.valueOf(chr), start, stop);

					// save geneList object to hash with key gene
					geneInfo.put(gene, genes);

					// add genes to hash with chr as key
					chrGene.put(chr, gene);

				} else {
					nonGoodGenes.put(gene, chrom);
				}
			}

		} catch (SQLException e) {
			log.writeError("An error occured querying the gene DB. During data extraction.");
			System.exit(1);
		}

		// make log entry
		log.writeOutFile("Gene information read in.");

	}









	///////////////
	//////// Getters

	public ArrayList<String> getAllGeneNames() {
		return allGeneNames;
	}

	public Map<String, GeneInfo> getGeneInfo() {
		return geneInfo;
	}

	public Multimap<Integer, String> getChrGene() {
		return chrGene;
	}

	public Map<String, String> getNonGoodGenes() {
		return nonGoodGenes;
	}



}
