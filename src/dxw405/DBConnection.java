package dxw405;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection
{
	private Connection connection;
	private Logger logger;

	public DBConnection(Level logLevel, String host, String dbName, String user, String pwd, boolean dropAll)
	{
		initLogger(logLevel, dbName);

		createConnection(host, dbName, user, pwd);
		if (connection == null) throw new IllegalStateException("Could not obtain a connection to the database");

		if (dropAll)
		{
			String dropAllStatement = "DROP SCHEMA public CASCADE; CREATE SCHEMA public;";
			Statement statement;
			try
			{
				statement = createStatement();
				statement.executeUpdate(dropAllStatement);
				logger.info("Dropped all tables");
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void initLogger(Level logLevel, String name)
	{
		logger = Logger.getLogger(name);
		logger.setLevel(logLevel);

		System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tH:%1$tM:%1$tS [%4$7s] %5$s%6$s%n");
	}

	private void createConnection(String host, String dbName, String user, String pwd)
	{
		System.setProperty("jdbc.drivers", "org.postgresql.Driver");

		try
		{
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e)
		{
			logger.severe("Postgresql driver not found (" + e + ")");
			return;
		}

		String url = "jdbc:postgresql://" + host + "/" + dbName;
		try
		{
			connection = DriverManager.getConnection(url, user, pwd);
			logger.info("Connected to the database \"" + url + "\"");
		} catch (SQLException e)
		{
			logger.severe("Could not connect to the database (" + e + ")");
		}
	}

	public Statement createStatement() throws SQLException
	{
		return connection.createStatement();
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
