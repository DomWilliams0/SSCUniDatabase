package dxw405.gui;

import dxw405.DBConnection;
import dxw405.gui.dialog.UserInput;
import dxw405.util.Person;
import dxw405.util.Utils;

import java.io.File;
import java.sql.Date;
import java.sql.PreparedStatement;
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
	private PersonEntry[] lecturers;
	private String[] lecturerNames;

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
		if (allResults == null) return null;

		try
		{
			for (ResultSet resultSet : allResults)
			{
				if (resultSet == null) continue;

				while (resultSet.next())
				{
					String personType = resultSet.getString(1);
					Person person = Utils.parseEnum(Person.class, personType);
					if (person == null) throw new SQLException("Bad person type specified in query (" + personType + ")");

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
			connection.logStackTrace(e);
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

	public String[] getLecturerNames()
	{
		return lecturerNames;
	}

	public void gatherEnums()
	{
		titles = gatherEnum("Titles", "titleString");
		registrationTypes = gatherEnum("RegistrationType", "description");
		lecturers = gatherLecturers();
		if (lecturers == null)
			return;

		lecturerNames = new String[lecturers.length];
		for (int i = 0; i < lecturers.length; i++)
			lecturerNames[i] = lecturers[i].forename;

	}

	private PersonEntry[] gatherLecturers()
	{
		String query = "SELECT lecturerID, titleString, forename, familyName FROM Lecturer INNER JOIN Titles ON Lecturer.titleID = Titles.titleID";
		ResultSet resultSet = connection.executeQuery(query);
		if (resultSet == null) return null;

		List<PersonEntry> results = new ArrayList<>();

		try
		{
			while (resultSet.next())
			{
				String fullName = resultSet.getString(2).trim() + ". " + resultSet.getString(3).trim() + " " + resultSet.getString(4).trim();
				int id = resultSet.getInt(1);
				PersonEntry entry = new PersonEntry(Person.LECTURER, id, null, fullName, null, null);
				results.add(entry);
			}

		} catch (SQLException e)
		{
			connection.severe("Could not gather lecturers" + e);
			connection.logStackTrace(e);
			return null;
		}

		PersonEntry[] ret = new PersonEntry[results.size()];
		results.toArray(ret);

		connection.fine("Gathered " + results.size() + " lecturers");

		return ret;
	}

	private String[] gatherEnum(String table, String column)
	{
		ResultSet resultSet = connection.executeQuery("SELECT " + column + " FROM " + table);
		if (resultSet == null) return null;

		List<String> results = new ArrayList<>();

		try
		{
			while (resultSet.next()) results.add(resultSet.getString(1).trim());

		} catch (SQLException e)
		{
			connection.severe("Could not gather enum values from table \"" + table + "\": " + e);
			connection.logStackTrace(e);
			return null;
		}

		String[] ret = new String[results.size()];
		results.toArray(ret);

		connection.fine("Gathered " + ret.length + " enum values from " + table);

		return ret;
	}

	/**
	 * Tries to add a student with the given input
	 *
	 * @param i The user's input
	 * @return The error message if the operation fails, otherwise null
	 */
	public String addStudent(UserInput i)
	{
		try
		{
			PreparedStatement ps;
			final int studentID = i.getValue("studentID");

			// add student
			ps = connection.prepareStatement("INSERT INTO Student (studentID, titleID, forename, familyName, dateOfBirth) VALUES (?, ?, ?, ?, ?)");
			ps.setInt(1, studentID);
			ps.setInt(2, i.<Integer>getValue("titleID") + 1);
			ps.setString(3, i.getValue("forename"));
			ps.setString(4, i.getValue("surname"));
			ps.setDate(5, new Date(i.<java.util.Date>getValue("dob").getTime()));
			ps.executeUpdate();
			connection.fine("Added student " + studentID + " to Student");
			ps.close();

			// course
			ps = connection.prepareStatement("INSERT INTO StudentRegistration (studentID, yearOfStudy, registrationTypeID) VALUES (?, ?, ?)");
			ps.setInt(1, studentID);
			ps.setInt(2, i.<Integer>getValue("yearOfStudy") + 1);
			ps.setInt(3, i.<Integer>getValue("courseTypeID") + 1);
			ps.executeUpdate();
			connection.fine("Added StudentRegistration for " + studentID);
			ps.close();

			// contacts
			if (i.getVar("hasNOK") == Boolean.TRUE)
			{
				ps = connection.prepareStatement("INSERT INTO NextOfKin (studentID, name, eMailAddress, postalAddress) VALUES (?, ?, ?, ?)");
				ps.setInt(1, studentID);
				ps.setString(2, i.getValue("nokName"));
				ps.setString(3, i.getValue("nokEmail"));
				ps.setString(4, i.getValue("nokAddress"));
				ps.executeUpdate();
				connection.fine("Added NextOfKin for " + studentID);
				ps.close();
			}

			if (i.getVar("hasContact") == Boolean.TRUE)
			{
				ps = connection.prepareStatement("INSERT INTO StudentContact (studentID, eMailAddress, postalAddress) VALUES (?, ?, ?)");
				ps.setInt(1, studentID);
				ps.setString(2, i.getValue("contactEmail"));
				ps.setString(3, i.getValue("contactAddress"));
				ps.executeUpdate();
				connection.fine("Added StudentContact for " + studentID);
				ps.close();
			}


			// create entry and add
			String title = titles[i.<Integer>getValue("titleID")];
			tableEntries.add(new PersonEntry(Person.STUDENT, studentID, title, i.getValue("forename"), i.getValue("surname"), i.getValue("dob")));

			// update observers
			setChanged();
			notifyObservers();

			return null;


		} catch (SQLException e)
		{
			connection.severe("Could not add student: " + e);
			connection.logStackTrace(e);
			return e.getMessage();
		}
	}

	/**
	 * Checks if a person with the given ID already exists
	 *
	 * @param id The ID to check
	 * @return If a person already exists with that ID
	 */
	public boolean personExists(int id)
	{
		for (PersonEntry entry : tableEntries)
			if (entry.id == id)
				return true;
		return false;
	}

	public String addTutor(PersonEntry student, UserInput input)
	{
		// not a student
		if (student.person != Person.STUDENT)
			return "Only students can have tutors";

		try
		{
			PreparedStatement ps = connection.prepareStatement("INSERT INTO Tutor (studentID, lecturerID) VALUES (?, ?)");
			ps.setInt(1, student.id);
			ps.setInt(2, lecturers[input.<Integer>getValue("lecturerID")].id);
			ps.executeUpdate();
			connection.fine("Added student " + student.id + " to tutor group " + input.getValue("lecturerID"));
			ps.close();

			return null;
		} catch (SQLException e)
		{
			connection.severe("Could not add tutor for student: " + e);
			connection.logStackTrace(e);
			return e.getMessage();
		}
	}

	public void logInfo(String msg) {connection.info(msg);}

	public void logSevere(String msg) {connection.severe(msg);}

	public void logWarning(String msg) {connection.warning(msg);}

	public void logFine(String msg) {connection.fine(msg);}

	public void logStackTrace(Exception e) {connection.logStackTrace(e);}

	public Integer getTutorID(int studentID)
	{
		// todo keep track of tutors, just like table entries
		return 0;
	}
}
