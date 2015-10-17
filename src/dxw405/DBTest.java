package dxw405;

import java.util.logging.Level;

public class DBTest
{

	public static void main(String[] args)
	{
		DBConnection connection = new DBConnection(Level.FINEST, "res/config.properties", true);

		// create the database
		DBCreation creation = new DBCreation(connection);
		creation.createTables();
		creation.populateTables();
	}
}
