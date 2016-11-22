package de.gentos.initialize;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.gentos.main.HandleFile;

public class InitDatabase {

	// Set Vaiables
	// database
	private Connection connect = null;
	private String driver = "org.sqlite.JDBC";

	// connect to db
	private String url;
	private String user = "";
	private String password = "";



	// other
	HandleFile log;
	String dbPath;

	////////////////////
	//////// Constructor

	public InitDatabase(String dbPath, HandleFile log) {
		this.dbPath = dbPath;
		this.log = log;

		//try to connect to database if database exists else abort
		File file = new File(dbPath);
		if (file.exists()){
			try {
				connectDatabase();
			} catch (Exception e) {
				log.writeOutFile("Failed to connect to database " + dbPath);
				System.exit(1);
			}
			log.writeFile("Connection to database " + dbPath + " successful.");
		} else {
			log.writeError("Database " + dbPath + " not found.");
			System.exit(1);
		}
	}

	

	////////////////
	//////// Methods 

	//////// Connect to Database


	private void connectDatabase() throws Exception{

		// define database url
		url = "jdbc:sqlite:" + dbPath;

		// load SQL driver
		Class.forName(driver);

		// connect to databse
		connect = DriverManager.getConnection(url, user, password);
	}




	//////// check Database

	public void checkDatabases(String tableName, String[] columnNames) {
		// Init variables
		DatabaseMetaData meta = null;
		
		try {
			//Get Table metadata
			meta = connect.getMetaData();
			ResultSet table = meta.getTables(null, null, tableName, null);

			// check if table exists else Error and quit
			if (table.next()){
				log.writeOutFile("Table " + tableName + " exists");
			} else {
				// error message and exit
				log.writeError("Table "+ tableName + " not found in Database " + dbPath + "!");
				System.exit(1);
			}

		} catch (Exception e) {
			log.writeError("Unable to get meta data for database!");
			System.exit(1);
		}

		// check for existence of columns
		try {

			for ( String col : columnNames) {

				// extract meta data
				ResultSet rsColumn = meta.getColumns(null, null, tableName, col);
				
				// check if column names were found else print out error message
				if(rsColumn.next()) {

				} else {
					log.writeError("Column " + col + " not found in table " + tableName);
					System.exit(1);
				}
			}
			
			log.writeOutFile("All columns found!");

		} catch (Exception e) {

			// write failure if something happened getting meta data
			log.writeError("Failure during checking for columns.");
			System.exit(1);
		}
		
		// checking done -> close DB
		
		try {
			connect.close();
		} catch (SQLException e) {
			System.out.println("WARNING: Couldn't close database!"); 
		}
	}
}
