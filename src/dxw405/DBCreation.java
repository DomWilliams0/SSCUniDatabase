package dxw405;

import java.io.File;
import java.util.logging.Level;

/**
 * Creates and populates the database
 */
public class DBCreation
{
	private DBConnection connection;

	public DBCreation(DBConnection connection)
	{
		this.connection = connection;
	}

	/**
	 * Creates the tables by executing the queries in the given file
	 *
	 * @param inputFile SQL file
	 */
	public void createTables(File inputFile)
	{
		boolean success = connection.executeFile(inputFile, Level.INFO);
		if (success)
			connection.info("Created tables successfully from (" + inputFile.getPath() + ")");
	}

	/**
	 * Populates the tables with randomly generated data
	 */
	public void populateTables()
	{
	}

}
