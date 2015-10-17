package dxw405;

import dxw405.util.SQLFileParser;

import java.io.File;
import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection
{
	private Connection connection;
	private SQLFileParser fileParser;
	private Logger logger;


	public DBConnection(Level logLevel, String host, String dbName, String user, String pwd, boolean dropAll)
	{
		// logger
		initLogger(logLevel, dbName);

		// db connection
		boolean connectionSuccess = createConnection(host, dbName, user, pwd);
		if (!connectionSuccess)
		{
			logger.severe("Could not obtain a connection to the database");
			return;
		}

		// file parser
		fileParser = new SQLFileParser(this);

		// drop all tables
		if (dropAll)
		{
			boolean success = executeUpdate("DROP SCHEMA public CASCADE; CREATE SCHEMA public;");
			if (success)
				logger.info("Dropped all tables");
		}
	}

	/**
	 * @param logLevel The logger's log level
	 * @param name     The logger's name
	 */
	private void initLogger(Level logLevel, String name)
	{
		logger = Logger.getLogger(name);
		logger.setLevel(logLevel);

		System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tH:%1$tM:%1$tS [%4$7s] %5$s%6$s%n");
	}

	/**
	 * Attempts to obtain a connection to the given database using the given credentials
	 *
	 * @param host   DB host
	 * @param dbName DB name
	 * @param user   DB username
	 * @param pwd    DB password
	 * @return If the operation was successful
	 */
	private boolean createConnection(String host, String dbName, String user, String pwd)
	{
		System.setProperty("jdbc.drivers", "org.postgresql.Driver");

		try
		{
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e)
		{
			logger.severe("Postgresql driver not found (" + e + ")");
			return false;
		}

		String url = "jdbc:postgresql://" + host + "/" + dbName;
		try
		{
			connection = DriverManager.getConnection(url, user, pwd);
			logger.info("Connected to the database \"" + url + "\"");
		} catch (SQLException e)
		{
			logger.severe("Could not connect to the database (" + e + ")");
			return false;
		}

		return true;
	}

	public Statement createStatement() throws SQLException
	{
		return connection.createStatement();
	}


	/**
	 * Executes the queries sequentially in the given file, and logs queries to FINE
	 *
	 * @param file The input SQL file
	 * @return If the operation succeeded
	 */
	public boolean executeFile(File file)
	{
		return executeFile(file, Level.FINE);
	}

	/**
	 * Executes the queries sequentially in the given file
	 *
	 * @param file          The input SQL file
	 * @param queryLogLevel The log level at which to log each query
	 * @return If the operation succeeded
	 */
	public boolean executeFile(File file, Level queryLogLevel)
	{
		List<String> queries = fileParser.parseFile(file);
		if (queries == null)
			return false;

		Statement statement;
		try
		{
			statement = connection.createStatement();
		} catch (SQLException e)
		{
			logger.severe("Could not create statement: " + e);
			return false;
		}

		for (String query : queries)
		{
			try
			{
				statement.execute(query);
				logger.log(queryLogLevel, "Executed query: " + query);
			} catch (SQLException e)
			{
				logger.log(queryLogLevel, "Could not execute query: " + e);
			}
		}

		try
		{
			statement.close();
		} catch (SQLException e)
		{
			logger.severe("Could not close statement: " + e);
		}

		return true;
	}


	/**
	 * Executes the given query as a Statement using executeUpdate
	 * SQLExceptions will be logged as severe
	 *
	 * @param query The query to execute
	 * @return If the operation succeeded or not
	 */
	public boolean executeUpdate(String query)
	{
		try
		{
			Statement statement = connection.createStatement();
			statement.executeUpdate(query);
			statement.close();
			return true;
		} catch (SQLException e)
		{
			logger.severe("Failed to execute query: " + e);
			return false;
		}
	}

	public PreparedStatement prepareStatement(String sql) throws SQLException
	{
		return connection.prepareStatement(sql);
	}

	public void close() throws SQLException
	{
		connection.close();
	}

	public void info(String msg)
	{
		logger.info(msg);
	}

	public void severe(String msg)
	{
		logger.severe(msg);
	}

	public void warning(String msg)
	{
		logger.warning(msg);
	}
}
