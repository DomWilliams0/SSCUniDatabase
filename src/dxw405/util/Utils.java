package dxw405.util;

import dxw405.DBConnection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils
{
	private Utils()
	{
	}


	/**
	 * Opens the given file for reading
	 *
	 * @param connection DBConnection for logging
	 * @param file       The file to read
	 * @return The file stream, or null if the operation failed
	 */
	public static FileInputStream readFile(DBConnection connection, File file)
	{
		// null
		if (file == null)
		{
			connection.severe("Input file is null");
			return null;
		}

		// doesn't exist
		if (!file.exists())
		{
			connection.severe("Input file doesn't exist (" + file.getPath() + ")");
			return null;
		}

		FileInputStream stream;
		try
		{
			stream = new FileInputStream(file);
			return stream;
		} catch (IOException e)
		{
			connection.severe("Could not load file (" + file.getPath() + "): " + e);
			return null;
		}
	}

	/**
	 * Closes the given stream
	 *
	 * @param connection DBConnection for logging
	 * @param stream     The stream to close
	 */
	public static void closeStream(DBConnection connection, InputStream stream)
	{
		try
		{
			stream.close();
		} catch (IOException e)
		{
			connection.severe("Cannot close stream: " + e);
		}
	}


}
