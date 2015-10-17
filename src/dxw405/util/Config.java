package dxw405.util;

import dxw405.DBConnection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config
{
	private Properties properties;
	private DBConnection connection;

	public Config(DBConnection connection)
	{
		this.connection = connection;
		this.properties = new Properties();
	}

	public boolean load(File configFile)
	{
		FileInputStream inStream = Utils.readFile(connection, configFile);
		if (inStream == null)
			return false;

		try
		{
			properties.load(inStream);
			Utils.closeStream(connection, inStream);
			return true;
		} catch (IOException e)
		{
			connection.severe("Could not load config file (" + configFile.getPath() + "): " + e);
			return false;
		}
	}

	public String get(String key)
	{
		String value = properties.getProperty(key);
		if (value == null)
			throw new IllegalArgumentException("Invalid config key: " + key);

		return value;
	}

	public int getInt(String key)
	{
		String value = get(key);

		try
		{
			return Integer.parseInt(value);
		} catch (NumberFormatException e)
		{
			throw new IllegalArgumentException("Cannot get integer from config (" + key + "=" + value + ")");
		}
	}
}
