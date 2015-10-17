package dxw405.util;

import dxw405.DBConnection;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Reads in SQL commands from a file, excluding comments
 */
public class SQLFileParser
{
	private DBConnection connection;

	public SQLFileParser(DBConnection connection)
	{
		this.connection = connection;
	}

	/**
	 * @param file SQL file with commands
	 * @return List of commands (in order) read from the file. Null if operation failed
	 */
	public List<String> parseFile(File file)
	{
		FileInputStream stream = Utils.readFile(connection, file);
		if (stream == null)
			return null;

		Scanner scanner = new Scanner(stream);
		scanner.useDelimiter("(;(\r)?\n)|(--\n)");

		List<String> commands = new ArrayList<>();

		while (scanner.hasNext())
		{
			String line = scanner.next().trim();
			if (line.isEmpty())
				continue;

			commands.add(stripComments(line));
		}

		Utils.closeStream(connection, stream);
		return commands;
	}

	/**
	 * Strips preceding comments (--) from the given string
	 *
	 * @param line SQL command with preceding comments
	 * @return The command without preceding comments
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
