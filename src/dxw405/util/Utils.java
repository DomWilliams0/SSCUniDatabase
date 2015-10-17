package dxw405.util;

import dxw405.DBConnection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class Utils
{
	public static final Random RANDOM = new Random();


	private Utils()
	{
	}

	/**
	 * Validates that the given file is not null and exists
	 *
	 * @param connection DBConnection for loggin
	 * @param file       The file to validate
	 * @return If the file is valid
	 */
	public static boolean validateFile(DBConnection connection, File file)
	{
		// null
		if (file == null)
		{
			connection.severe("Input file is null");
			return false;
		}

		// doesn't exist
		if (!file.exists())
		{
			connection.severe("Input file doesn't exist (" + file.getPath() + ")");
			return false;
		}

		return true;
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
		if (!validateFile(connection, file))
			return null;

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
