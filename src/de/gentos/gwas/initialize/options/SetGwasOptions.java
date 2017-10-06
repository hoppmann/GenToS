package de.gentos.gwas.initialize.options;

import org.apache.commons.cli.Options;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import de.gentos.general.misc.OptionValue;

public class SetGwasOptions {

	//////////////////////
	//////// Set variables
	private Options options;
	private Multimap<String, OptionValue> opts;
	
	// names for grouping 
	String mandatory = "main options";
	String run = "run specific";
	String column = "column names";
	String threshold = "threshold defining";
	String general = "general settings";
	String validation = "validation";
	String plot = "plotting";
	String other = "other";
	
	
	
	////////////////////
	//////// constructor
	public SetGwasOptions() {
		makeOptions();
	}

	////////////////
	//////// Methods

	//setting options to be chosen from
	private void makeOptions() {

		options = new Options();
		opts = LinkedListMultimap.create();

		// main options
		opts.put(mandatory, new OptionValue(options, "listCollection", true, "File containing a list of file names with gene lists to look for"));
		opts.put(mandatory, new OptionValue(options, "list", true, "\tFile name of list of genes (one gene per row) to be analyzed"));
		opts.put(mandatory, new OptionValue(options, "gene", true, "\tQuery gene to look for"));
		opts.put(mandatory, new OptionValue(options, "dbGene", true, "\tFile name of gene database to be used [tableGene option required]"));
		opts.put(mandatory, new OptionValue(options, "tableGene", true, "Name of gene table in dbGene (default = genes)"));
		opts.put(mandatory, new OptionValue(options, "specFile", true, "File containing database specifications (tab delimited) (see also option 'getSpec'"));
		opts.put(mandatory, new OptionValue(options, "dbSNP", true, "\tName of database containing GWAS summary statistics. See specifications"));
		opts.put(mandatory, new OptionValue(options, "tableSNP", true, "Name of table containing GWAS summary statistics"));
		opts.put(mandatory, new OptionValue(options, "bedFile", false, "\tIf chosen, gene lists are chosen as BED files. (See specs for more details)\n"));
		// 
		
		// other run specific
		opts.put(run, new OptionValue(options, "flanking", true, "Length of region flanking (upstream AND downstream) gene of interest which should also be analyzed (default 10000) (integers only)"));
		opts.put(run, new OptionValue(options, "upstream", true, "Length of region upstream of gene that should also be analyzed (overwrites the option flanking)"));
		opts.put(run, new OptionValue(options, "downstream", true, "Length of region downstream of gene that should also be analyzed (overwrites the option flanking)"));
		opts.put(run, new OptionValue(options, "pop", true, "\tName of population for extraction of independent SNPs [ALL, AFR, ASN, EUR (default), or custom]"));
		opts.put(run, new OptionValue(options, "popDIR", true, "\tFor custom population, directory where population based independent SNPs database is stored."));
		
		// column names
		opts.put(column, new OptionValue(options, "colRsID", true, "\tName of column containing the rsIDs! (default rsid)"));
		opts.put(column, new OptionValue(options, "colChr", true, "\tName of column containing chromosomes (default chr)"));
		opts.put(column, new OptionValue(options, "colPos", true , "\tName of column containing SNP position (default pos)"));
		opts.put(column, new OptionValue(options, "colpVal", true, "\tName of column containing p-values (default pval)"));

		// pval correction options
		opts.put(threshold, new OptionValue(options, "plenty", false, "\tCorrects for independent SNPs across all number of genes in list"));
		opts.put(threshold, new OptionValue(options, "fixThresh", true, "Sets a fixed threshold to define statistical significance"));
		opts.put(threshold, new OptionValue(options, "bonferroni", false, "Significance threshold is calculated useing Bonferroni correction [default]"));
		opts.put(threshold, new OptionValue(options, "FDR", false, "\tIf chosen FDR based threshold is calculated using Bejnamini-Hochberg"));
		opts.put(threshold, new OptionValue(options, "alpha", true, "\tSets the alpha for the FDR calculation (default = 0.05)"));
//		opts.put(threshold, new OptionValue(options, "maxEnrichment", false, "Estimates the maximum enrichment using binomial distribution, then uses the max enrichment as significance threshold (to be added)"));
		
		//general settings
		opts.put(general, new OptionValue(options, "log", true, "\tName of logfile"));
		opts.put(general, new OptionValue(options, "outDir", true, "\tName of output directory"));
		opts.put(general, new OptionValue(options, "csvDir", true, "\tName of output directory for csv files (default \"csv\")"));
		
		// plotting options
		opts.put(plot, new OptionValue(options, "title", true, "\tSets the title for the enrichment graph. If set to \"NONE\", no title will be displayed"));
		opts.put(plot, new OptionValue(options, "format", true, "\tType of output format for graph. Possible options png(default) and pdf"));
		opts.put(plot, new OptionValue(options, "scaling", true, "\tScales all fonts of the enrichment graph by chosen value. Must be > 0 (1.4 default)"));
		
		// validation of enrichment
		opts.put(validation, new OptionValue(options, "enrichment", false, "Choose this option if the enrichment should be validated"));
		opts.put(validation, new OptionValue(options, "randomRepeat", false, "Graphical validation of findings by an iteration of drawing random genes for each list of equal number of original list, then running the program on drawn genes (slowest method)"));
		opts.put(validation, new OptionValue(options, "iterations", true, "Number of times randomRepeat shuld be repeated (default 2000)"));
		opts.put(validation,  new OptionValue(options, "binomial", false, "Graphical validation of findings by estimating the enrichment using binomial distribution (default) (not supported for bed files)"));
		opts.put(validation, new OptionValue(options, "seed", true, "\tSet seed for random generator."));
		opts.put(validation, new OptionValue(options, "getProbHit", false, "If chosen gives out the theoretical probability of observing a gene as being significant used to calculate binomial distribution"));
		opts.put(validation, new OptionValue(options, "reference", true, "A bed file that should be used as reference for drawing random sets of ROIs."));
		opts.put(validation, new OptionValue(options, "randList", true, "A list of files to be used for validation, instead of random drawing validation lists."));
		
		
		// stuff
		opts.put(other, new OptionValue(options, "getSpec", false, "\tIf chosen prints out the specifications for the databases"));
		opts.put(other, new OptionValue(options, "help", false, "\tDisplayes this help message"));
		opts.put(other, new OptionValue(options, "keepTmp", false, "\tKeep temp directory containing data for plotting"));
		
		
		
	}


	//display Help
	public void callHelp() {
		
		System.out.println("options");
		
		// for each group display help text
		for (String key : opts.keySet()) {

			// display group name
			System.out.println("\n\t" + key);
			
			// extract help text and display it
			for (OptionValue currentOption : opts.get(key)) {
				String shortcut = currentOption.getShortcut();
				String description = currentOption.getDescription();
				System.out.println("\t\t" + shortcut + "\t\t" + description);
			}
		}
	}
	
	
	
	///////////////
	//////// getter

	public Options getOptions() {
		return options;
	}






}
