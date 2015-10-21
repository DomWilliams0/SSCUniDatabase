package dxw405.util;

import dxw405.DBConnection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;
import java.util.Random;
import java.util.logging.Level;

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

	/**
	 * Capitalises every word in the given string
	 *
	 * @param sentence The sentence to capitalise
	 * @return The first letter of every word capitalised, the rest lowercase
	 */
	public static String capitalise(String sentence)
	{
		if (sentence == null)
			return null;
		if (sentence.isEmpty())
			return sentence;

		String[] split = sentence.split(" ");
		StringBuilder sb = new StringBuilder();
		for (String s : split)
		{
			int length = s.length();
			switch (length)
			{
				case 0:
					sb.append(" ");
					break;
				case 1:
					sb.append(s.toUpperCase()).append(" ");
					break;
				default:
					sb.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1).toLowerCase()).append(" ");
					break;
			}
		}

		return sb.toString();
	}

	/**
	 * Converts a string to a log level
	 *
	 * @param s String to parse
	 * @return The corresponding log level, or null if invalid
	 */
	public static Level stringToLevel(String s)
	{
		try
		{
			return Level.parse(s.toUpperCase());
		} catch (IllegalArgumentException e)
		{
			return null;
		}
	}

	/**
	 * Converts a String into an Enum
	 *
	 * @param enumClass The enum
	 * @param s         The string to parse
	 * @return The corresponding enum value, or null if none was found
	 */
	public static <E extends Enum<E>> E parseEnum(Class<E> enumClass, String s)
	{
		return parseEnum(enumClass, s, true);
	}

	/**
	 * Converts a String into an Enum
	 *
	 * @param enumClass      The enum
	 * @param s              The string to parse
	 * @param convertToUpper If the given string should be converted to uppercase before comparison
	 * @return The corresponding enum value, or null if none was found
	 */
	public static <E extends Enum<E>> E parseEnum(Class<E> enumClass, String s, boolean convertToUpper)
	{
		EnumSet<E> values = EnumSet.allOf(enumClass);
		String sCompare = convertToUpper ? s.toUpperCase() : s;
		for (E value : values)
			if (value.toString().equals(sCompare))
				return value;
		return null;
	}


}
