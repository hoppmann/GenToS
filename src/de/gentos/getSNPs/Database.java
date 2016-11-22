package de.gentos.getSNPs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import de.gentos.initialize.InitializeMain;
import de.gentos.main.HandleFile;

public class Database {

	//////////////////////
	//////// Set Variables
	// database
	private Connection connect = null;
	private String driver = "org.sqlite.JDBC";
	private String dbPath;
	private Statement statement = null;


	// connect to db
	private String url;
	private String user = "";
	private String password = "";

	// other
	InitializeMain init;
	HandleFile log;


	////////////////////
	//////// constructor

	public Database(String dbPath, InitializeMain init) {
		this.dbPath = dbPath;
		this.init = init;
		log = init.getLog();

		// connect to databse

		try {
			connectDatabase();
			log.writeFile("Successfully connected to " + dbPath);
		} catch (Exception e) {
			log.writeError("Connection to database " + dbPath + " failed!");
			System.exit(1);
		}
	}


	////////////////
	//////// Methods

	
	// connect to database
	private void connectDatabase () throws Exception{
		// define database url
		url = "jdbc:sqlite:" + dbPath;

		// load SQL driver
		Class.forName(driver);

		// connect to databse
		connect = DriverManager.getConnection(url, user, password);

	}

	
	// query database
	public ResultSet select(String query) {
		ResultSet result = null;
		try {
			statement = connect.createStatement();
			result = statement.executeQuery(query);
		} catch (Exception e) {
			log.writeError("Failed querying the database " + dbPath);
			System.exit(1);
		}

		return result;


	}	




}
