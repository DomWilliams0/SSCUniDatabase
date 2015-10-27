package dxw405;

import dxw405.util.Config;
import dxw405.util.SQLFileParser;
import dxw405.util.Utils;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection implements AutoCloseable
{
	private Connection connection;
	private SQLFileParser fileParser;
	private Logger logger;
	private Config config;


	/**
	 * @param configFile Path to the config file
	 */
	public DBConnection(String configFile)
	{
		initDefaultLogger();

		// config
		config = new Config(this);
		boolean configLoaded = config.load(new File(configFile));
		if (!configLoaded) halt("Could not load the config");

		DBDetails details = new DBDetails(this);
		if (!details.isValid())
			halt("Could not load database details");

		// logger
		initLogger(details.dbName);

		// db connection
		boolean connectionSuccess = createConnection(details);
		if (!connectionSuccess) halt("Could not obtain a connection to the database");

		// file parser
		fileParser = new SQLFileParser(this);
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
	 * Initiates the logger with a default logging level
	 * This allows for logging before the desired logging level can be loaded from the config (in case of errors)
	 */
	private void initDefaultLogger()
	{
		initLogger("DEFAULT", Level.INFO);
	}

	/**
	 * Initiates the logger, using the logging level specified in the config
	 *
	 * @param name The logger's name
	 */
	private void initLogger(String name)
	{
		Level logLevel = Utils.stringToLevel(config.get("log-level"));
		boolean logLevelFailure = logLevel == null;
		if (logLevelFailure) logLevel = Level.INFO;

		initLogger(name, logLevel);

		if (logLevelFailure) warning("Invalid log level provided in config, reverting to " + logLevel);
	}

	private void initLogger(String name, Level logLevel)
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
			logStackTrace(e);
			return false;
		}

		return true;
	}

	public Statement createStatement() throws SQLException
	{
		return connection.createStatement();
	}

	/**
	 * Executes the queries sequentially in the given file
	 *
	 * @param file The input SQL file
	 * @return An array of result sets from each query, or null if the operation failed
	 */
	public ResultSet[] executeQueriesFromFile(File file)
	{
		List<String> queries = fileParser.parseFile(file);
		if (queries == null) return null;

		ResultSet[] results = new ResultSet[queries.size()];

		Statement statement;
		try
		{
			statement = connection.createStatement();
		} catch (SQLException e)
		{
			logger.severe("Could not create statement: " + e);
			logStackTrace(e);
			return null;
		}

		for (int i = 0; i < results.length; i++)
			results[i] = executeQuery(queries.get(i));

		try
		{
			statement.close();
		} catch (SQLException e)
		{
			logger.severe("Could not close statement: " + e);
			logStackTrace(e);
		}


		return results;
	}


	/**
	 * Executes the commands sequentially in the given file, and logs commands to FINER
	 *
	 * @param file The input SQL file
	 * @return If the operation succeeded
	 */

	public boolean executeUpdateFromFile(File file)
	{
		return executeUpdateFromFile(file, Level.FINER);
	}

	/**
	 * Executes the commands sequentially in the given file
	 *
	 * @param file            The input SQL file
	 * @param commandLogLevel The log level at which to log each command
	 * @return If the operation succeeded
	 */
	public boolean executeUpdateFromFile(File file, Level commandLogLevel)
	{
		List<String> commands = fileParser.parseFile(file);
		if (commands == null) return false;

		Statement statement;
		try
		{
			statement = connection.createStatement();
		} catch (SQLException e)
		{
			logger.severe("Could not create statement: " + e);
			logStackTrace(e);
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
				logStackTrace(e);
			}
		}

		try
		{
			statement.close();
		} catch (SQLException e)
		{
			logger.severe("Could not close statement: " + e);
			logStackTrace(e);
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
			logStackTrace(e);
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
			logStackTrace(e);
			return null;
		}
	}

	public PreparedStatement prepareStatement(String sql) throws SQLException
	{
		return connection.prepareStatement(sql);
	}

	public void setAutoCommit(boolean autoCommit)
	{
		try
		{
			connection.setAutoCommit(autoCommit);
		} catch (SQLException e)
		{
			severe("Could not set auto-commit to " + autoCommit + ": " + e);
			logStackTrace(e);
		}
	}

	public void rollback()
	{
		try
		{
			connection.rollback();
		} catch (SQLException e)
		{
			severe("Could not rollback: " + e);
			logStackTrace(e);
		}
	}

	public void commit()
	{
		try
		{
			connection.commit();
		} catch (SQLException e)
		{
			severe("Could not commit: " + e);
			logStackTrace(e);
		}
	}


	/**
	 * Prepares the statements sequentially from the given file
	 *
	 * @param file The input SQL file
	 * @return An array of prepared statements for each query, or null if the operation failed
	 */
	public PreparedStatement[] prepareStatementsFromFile(File file)
	{
		List<String> statements = fileParser.parseFile(file);
		if (statements == null) return null;

		PreparedStatement[] pss = new PreparedStatement[statements.size()];

		try
		{
			for (int i = 0; i < pss.length; i++)
				pss[i] = connection.prepareStatement(statements.get(i));

		} catch (SQLException e)
		{
			logger.severe("Could not prepare statements from file: " + e);
			logStackTrace(e);
			return null;
		}

		return pss;
	}

	public void close()
	{
		try
		{
			connection.close();
			logger.info("Closed database connection");
		} catch (SQLException e)
		{
			logger.severe("Could not close connection:" + e);
			logStackTrace(e);
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

	public void finer(String msg)
	{
		logger.finer(msg);
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

	public boolean getBooleanFromConfig(String key)
	{
		return config.getBoolean(key);
	}

	/**
	 * Logs the given exception's stack trace at INFO
	 *
	 * @param e The exception
	 */
	public void logStackTrace(Exception e)
	{
		String wrapper = "------------------";
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		logger.log(Level.INFO, "\n" + wrapper + "\n" + sw.toString() + wrapper);
	}
}

class DBDetails
{
	String host, dbName, user, password;

	private boolean success;

	public DBDetails(DBConnection connection)
	{
		String dbFile = connection.getResourcePath("db-details");
		Config dbConfig = new Config(connection);
		success = dbConfig.load(new File(dbFile));
		if (!success)
		{
			connection.severe("Could not find database details file (" + dbFile + ")");
			return;
		}

		connection.fine("Loading database details from " + dbFile);

		host = dbConfig.get("db-host");
		dbName = dbConfig.get("db-name");
		user = dbConfig.get("db-user");
		password = dbConfig.get("db-pass");
	}

	public boolean isValid()
	{
		return success;
	}
}