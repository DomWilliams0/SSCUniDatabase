package dxw405;

import dxw405.util.Config;
import dxw405.util.SQLFileParser;

import java.io.File;
import java.sql.*;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection
{
	private Connection connection;
	private SQLFileParser fileParser;
	private Logger logger;
	private Config config;


	/**
	 * @param logLevel   Log level for the logger
	 * @param configFile Path to the config file
	 * @param dropAll    Whether to drop all tables in the database or not
	 */
	public DBConnection(Level logLevel, String configFile, boolean dropAll)
	{
		// config
		config = new Config(this);
		boolean configLoaded = config.load(new File(configFile));
		if (!configLoaded)
			halt("Could not load the config");

		DBDetails details = new DBDetails(config);

		// logger
		initLogger(logLevel, details.dbName);

		// db connection
		boolean connectionSuccess = createConnection(details);
		if (!connectionSuccess)
			halt("Could not obtain a connection to the database");

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
	 * Immediately crashes the program with the given message
	 *
	 * @param msg Halt message
	 */
	private void halt(String msg)
	{
		severe("HALTING: " + msg);
		System.exit(2);
	}


	/**
	 * @param logLevel The logger's log level
	 * @param name     The logger's name
	 */
	private void initLogger(Level logLevel, String name)
	{
		logger = Logger.getLogger(name);
		logger.setLevel(logLevel);

		// log formatting
		System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tH:%1$tM:%1$tS [%4$7s] %5$s%6$s%n");
		System.setProperty("java.util.logging.ConsoleHandler.formatter", "java.util.logging.SimpleFormatter");

		// log level publishing
		logger.setUseParentHandlers(false);
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(logLevel);
		logger.addHandler(handler);
	}

	/**
	 * Attempts to obtain a connection to the given database using the given credentials
	 *
	 * @param details Database details
	 * @return If the operation was successful
	 */
	private boolean createConnection(DBDetails details)
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

		String url = "jdbc:postgresql://" + details.host + "/" + details.dbName;
		logger.fine("Attempting to connect to " + url);
		try
		{
			connection = DriverManager.getConnection(url, details.user, details.password);
			logger.info("Connected to the database \"" + url + "\"");
		} catch (SQLException e)
		{
			logger.severe("Could not connect to the database (" + url + "): " + e);
			return false;
		}

		return true;
	}

	public Statement createStatement() throws SQLException
	{
		return connection.createStatement();
	}


	/**
	 * Executes the commands sequentially in the given file, and logs commands to FINE
	 *
	 * @param file The input SQL file
	 * @return If the operation succeeded
	 */
	public boolean executeFile(File file)
	{
		return executeFile(file, Level.FINE);
	}

	/**
	 * Executes the commands sequentially in the given file
	 *
	 * @param file            The input SQL file
	 * @param commandLogLevel The log level at which to log each command
	 * @return If the operation succeeded
	 */
	public boolean executeFile(File file, Level commandLogLevel)
	{
		List<String> commands = fileParser.parseFile(file);
		if (commands == null)
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

		for (String command : commands)
		{
			try
			{
				statement.execute(command);
				logger.log(commandLogLevel, "Executed command: " + command);
			} catch (SQLException e)
			{
				logger.log(commandLogLevel, "Could not execute command: " + e);
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
	 * Executes the given command as a Statement using executeUpdate
	 * SQLExceptions will be logged as severe
	 *
	 * @param command The command to execute
	 * @return If the operation succeeded or not
	 */
	public boolean executeUpdate(String command)
	{
		try
		{
			Statement statement = connection.createStatement();
			statement.executeUpdate(command);
			statement.close();
			return true;
		} catch (SQLException e)
		{
			logger.severe("Failed to execute command: " + e);
			return false;
		}
	}

	/**
	 * Executes the given query as a Statement using executeQuery
	 * SQLExceptions will be logged as severe
	 *
	 * @param query The query to execute
	 * @return The ResultSet if successful, null otherwise
	 */
	public ResultSet executeQuery(String query)
	{
		try
		{
			Statement statement = connection.createStatement();
			return statement.executeQuery(query);

		} catch (SQLException e)
		{
			logger.severe("Failed to execute query: " + e);
			return null;
		}
	}

	public PreparedStatement prepareStatement(String sql) throws SQLException
	{
		return connection.prepareStatement(sql);
	}

	public void close()
	{
		try
		{
			connection.close();
		} catch (SQLException e)
		{
			logger.severe("Could not close connection:" + e);
		}
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

	public void fine(String msg)
	{
		logger.fine(msg);
	}

	/**
	 * Gets the relative path to the SQL file corresponding to the given config key
	 *
	 * @param sqlFile Config key for SQL file
	 * @return Relative path
	 */
	public String getSQLPath(String sqlFile)
	{
		return getPath("sql-dir", sqlFile, ".sql");
	}

	/**
	 * Gets the relative path to the resource file corresponding to the given config key
	 *
	 * @param resFile Config key for resource file
	 * @return Relative path
	 */
	public String getResourcePath(String resFile)
	{
		return getPath("res-dir", resFile, null);
	}

	/**
	 * @param prefixKey Config key for prefix
	 * @param fileKey   Config key for file
	 * @param extension Optional file extension, null for none
	 * @return [prefix]/[file][extension]
	 */
	public String getPath(String prefixKey, String fileKey, String extension)
	{
		return getStringFromConfig(prefixKey) + File.separator + getStringFromConfig(fileKey) + (extension == null ? "" : extension);
	}

	public String getStringFromConfig(String key)
	{
		return config.get(key);
	}

	public int getIntFromConfig(String key)
	{
		return config.getInt(key);
	}
}

class DBDetails
{
	String host, dbName, user, password;

	public DBDetails(String host, String dbName, String user, String password)
	{
		this.host = host;
		this.dbName = dbName;
		this.user = user;
		this.password = password;
	}

	public DBDetails(Config config)
	{
		this(config.get("db-host"), config.get("db-name"), config.get("db-user"), config.get("db-pass"));
	}
}