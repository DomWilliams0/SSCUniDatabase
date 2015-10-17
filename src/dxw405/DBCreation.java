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
	 * Creates the tables by executing the commands in the create-tables SQL file
	 */
	public void createTables()
	{
		String inputFile = connection.getSQLPath("sql-dir", "sql-create-tables");

		boolean success = connection.executeFile(new File(inputFile), Level.INFO);
		if (success)
			connection.info("Created tables successfully from (" + inputFile + ")");
	}

	/**
	 * Populates the tables with randomly generated ata
	 */
	public void populateTables()
	{
	}

}
