package dxw405;

import java.io.File;
import java.util.logging.Level;

public class DBTest
{

	public static void main(String[] args)
	{
		DBConnection connection = new DBConnection(Level.INFO, "localhost", "sscuni", "dom", "hunter2", true);

		DBCreation creation = new DBCreation(connection);
		creation.createTables(new File("sql/create_tables.sql"));
	}
}
