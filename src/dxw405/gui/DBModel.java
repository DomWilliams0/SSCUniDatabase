package dxw405.gui;

import dxw405.DBConnection;
import dxw405.util.Person;
import dxw405.util.Utils;

import java.io.File;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.logging.Level;

public class DBModel extends Observable
{
	private String[] titles;
	private String[] registrationTypes;

	private DBConnection connection;
	private List<PersonEntry> tableEntries;

	public DBModel(DBConnection connection)
	{
		this.connection = connection;
		this.tableEntries = new ArrayList<>();
	}

	public List<PersonEntry> getTableEntries()
	{
		return tableEntries;
	}

	public void populateTable()
	{
		tableEntries.clear();

		String errorMessage = addEntries(connection.getSQLPath("sql-populate-tables"));

		setChanged();
		notifyObservers(errorMessage);
	}

	/**
	 * Executes the queries in the given file, parses the results and adds them to the table entry list
	 *
	 * @param sqlFile Input SQL file
	 * @return The error message if any, otherwise null if successful
	 */
	private String addEntries(String sqlFile)
	{
		ResultSet[] allResults = connection.executeQueriesFromFile(new File(sqlFile), Level.INFO);
		if (allResults == null)
			return null;

		try
		{
			for (ResultSet resultSet : allResults)
			{
				if (resultSet == null)
					continue;

				while (resultSet.next())
				{
					String personType = resultSet.getString(1);
					Person person = Utils.parseEnum(Person.class, personType);
					if (person == null)
						throw new SQLException("Bad person type specified in query (" + personType + ")");

					int id = resultSet.getInt(2);
					String title = resultSet.getString(3).trim();
					String forename = resultSet.getString(4).trim();
					String surname = resultSet.getString(5).trim();

					java.util.Date dob = null;
					if (resultSet.getMetaData().getColumnCount() > 5)
					{
						Date sqlDOB = resultSet.getDate(6);
						dob = new java.util.Date(sqlDOB.getTime());
					}

					tableEntries.add(new PersonEntry(person, id, title, forename, surname, dob));
				}
			}

			return null;
		} catch (SQLException e)
		{
			connection.severe("Could not retrieve entries: " + e);
			return e.getMessage();
		}

	}

	public String[] getTitles()
	{
		return titles;
	}

	public String[] getRegistrationTypes()
	{
		return registrationTypes;
	}

	public void gatherEnums()
	{
		titles = gatherEnum("Titles", "titleString");
		registrationTypes = gatherEnum("RegistrationType", "description");
	}

	private String[] gatherEnum(String table, String column)
	{
		ResultSet resultSet = connection.executeQuery("SELECT " + column + " FROM " + table);
		if (resultSet == null)
			return null;

		List<String> results = new ArrayList<>();

		try
		{
			while (resultSet.next())
				results.add(resultSet.getString(1).trim());

		} catch (SQLException e)
		{
			connection.severe("Could not gather enum values from table \"" + table + "\": " + e);
			return null;
		}

		String[] ret = new String[results.size()];
		results.toArray(ret);

		return ret;
	}

}
