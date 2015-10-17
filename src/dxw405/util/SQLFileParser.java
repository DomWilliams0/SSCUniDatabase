package dxw405.util;

import dxw405.DBConnection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Reads in SQL queries from a file, excluding comments
 */
public class SQLFileParser
{
	private DBConnection connection;

	public SQLFileParser(DBConnection connection)
	{
		this.connection = connection;
	}

	/**
	 * @param file SQL file with queries
	 * @return List of queries (in order) read from the file. Null if operation failed
	 */
	public List<String> parseFile(File file)
	{
		// invalid
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

		Scanner scanner;

		try
		{
			scanner = new Scanner(new FileReader(file));
			scanner.useDelimiter("(;(\r)?\n)|(--\n)");
		} catch (FileNotFoundException e)
		{
			connection.severe("Could not read input file: " + e);
			return null;
		}

		List<String> queries = new ArrayList<>();

		while (scanner.hasNext())
		{
			String line = scanner.next().trim();
			if (line.isEmpty())
				continue;

			queries.add(stripComments(line));
		}


		return queries;
	}

	/**
	 * Strips preceding comments (--) from the given string
	 *
	 * @param line SQL query with preceding comments
	 * @return The query without preceding comments
	 */
	private String stripComments(String line)
	{
		int first = line.indexOf("--");

		// none
		if (first < 0)
			return line;

		int comment = first;
		int last = comment;
		while (true)
		{
			comment = line.indexOf("--", comment + 1);
			if (comment < 0)
			{
				comment = last;
				break;
			}
		}

		// find end of line from there
		int splitPoint = Math.max(comment, line.indexOf('\n', comment) + 1);
		return line.substring(splitPoint);
	}

}
