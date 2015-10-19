package dxw405.gui;

import dxw405.DBConnection;
import dxw405.util.Person;

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

		boolean success = addEntries(connection.getSQLPath("sql-populate-tables"));
		// todo: dialog box if unsuccessful

		setChanged();
		notifyObservers();
	}

	private boolean addEntries(String sqlFile)
	{
		ResultSet[] allResults = connection.executeQueriesFromFile(new File(sqlFile), Level.INFO);
		if (allResults == null)
			return false;

		try
		{
			for (ResultSet resultSet : allResults)
			{
				if (resultSet == null)
					continue;

				while (resultSet.next())
				{
					Person person = Person.parse(resultSet.getString(1));
					if (person == null)
						throw new SQLException("Bad person type specified in query (" + resultSet.getString(1) + ")");

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

					String fullName = String.format("%s. %s %s", title, forename, surname);

					tableEntries.add(new PersonEntry(person, id, fullName, dob));
				}
			}

			return true;
		} catch (SQLException e)
		{
			connection.severe("Could not retrieve entries: " + e);
			return false;
		}


	}
}
