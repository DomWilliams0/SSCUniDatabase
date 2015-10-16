package dxw405;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class DBCreation
{
	private DBConnection connection;

	public DBCreation(DBConnection connection)
	{
		this.connection = connection;
	}

	public void createTables(File inputFile)
	{
		// invalid
		if (inputFile == null)
		{
			connection.severe("Null input file");
			return;
		}

		if (!inputFile.exists())
		{
			connection.severe("Could not find input file (" + inputFile.getPath() + ")");
			return;
		}

		connection.info("Creating tables...");

		FileReader in;
		try
		{
			in = new FileReader(inputFile);
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return;
		}

		int statementCount = 0;
		Scanner s = new Scanner(in);
		s.useDelimiter("(;(\r)?\n)|(--\n)");
		Statement st = null;
		try
		{
			st = connection.createStatement();
			while (s.hasNext())
			{
				String line = s.next();
				String cmd = line.trim();

				if (!cmd.isEmpty())
				{
					connection.info("Executing command: \n" + cmd);
					st.executeUpdate(cmd);
					statementCount++;
				}

			}
		} catch (SQLException e)
		{
			e.printStackTrace();
			connection.severe("Could not execute creation command: " + e);
			return;
		} finally
		{
			if (st != null) try
			{
				st.close();
			} catch (SQLException e)
			{
				e.printStackTrace();
			}

			s.close();
		}

		connection.info("Successfully created " + statementCount + " tables");
	}

	public void populateTables()
	{
	}

}
