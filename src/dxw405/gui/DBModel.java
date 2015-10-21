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
import java.util.*;
import java.util.logging.Level;

public class DBModel extends Observable
{
	private String[] titles;
	private String[] registrationTypes;
	private PersonEntry[] lecturers;
	private Map<Integer, Integer> tutors;

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
		updateTutors();

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
					String title = resultSet.getString(3);
					String forename = resultSet.getString(4);
					String surname = resultSet.getString(5);
					String email = resultSet.getString(6);

					Integer yearOfStudy = null;
					String courseType = null;
					Integer tutorID = null;
					java.util.Date dob = null;

					if (person == Person.STUDENT)
					{
						dob = new java.util.Date(resultSet.getDate(7).getTime());
						yearOfStudy = resultSet.getInt(8);
						courseType = resultSet.getString(9);
						tutorID = resultSet.getInt(10);
					}


					tableEntries.add(new PersonEntry(person, id, title, forename, surname, email, yearOfStudy, courseType, tutorID, dob));
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
		String[] s = new String[tutors.size()];
		int i = 0;
		for (PersonEntry tutor : lecturers)
			s[i++] = tutor.getFullName();
		return s;
	}

	public void gatherEnums()
	{
		titles = gatherEnum("Titles", "titleString");
		registrationTypes = gatherEnum("RegistrationType", "description");
		lecturers = gatherLecturers();
	}

	private void updateTutors()
	{
		if (tutors != null)
			tutors.clear();
		else
			tutors = new HashMap<>();

		ResultSet resultSet = connection.executeQuery("SELECT studentID, lecturerID FROM Tutor ORDER BY lecturerID");
		if (resultSet == null) return;

		try
		{
			while (resultSet.next())
			{
				int studentID = resultSet.getInt(1);
				int lecturerID = resultSet.getInt(2);

				tutors.put(studentID, lecturerID);
			}
		} catch (SQLException e)
		{
			connection.severe("Could not update tutor map: " + e);
			connection.logStackTrace(e);
		}
	}


	public Integer getTutorID(int studentID)
	{
		return tutors.get(studentID);
	}

	/**
	 * Finds the person with the given ID
	 *
	 * @param id The ID
	 * @return The Person, or null if not found
	 */
	private PersonEntry getEntryFromID(int id)
	{
		for (PersonEntry entry : tableEntries)
			if (id == entry.id)
				return entry;

		connection.severe("Could not find entry with id " + id);
		return null;
	}

	private PersonEntry[] gatherLecturers()
	{
		ResultSet[] resultSets = connection.executeQueriesFromFile(new File(connection.getSQLPath("sql-get-lecturers")));
		if (resultSets == null) return null;
		ResultSet resultSet = resultSets[0];

		List<PersonEntry> results = new ArrayList<>();

		try
		{
			while (resultSet.next())
			{
				int id = resultSet.getInt(1);
				results.add(PersonEntry.addLecturer(id, resultSet.getString(2), resultSet.getString(3),
						resultSet.getString(4), resultSet.getString(5)));
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
			String surname = i.getValue("surname");
			String forename = i.getValue("forename");

			// add student
			ps = connection.prepareStatement("INSERT INTO Student (studentID, titleID, forename, familyName, dateOfBirth) VALUES (?, ?, ?, ?, ?)");
			ps.setInt(1, studentID);
			ps.setInt(2, i.<Integer>getValue("titleID") + 1);
			ps.setString(3, forename);
			ps.setString(4, surname);
			ps.setDate(5, new Date(i.<java.util.Date>getValue("dob").getTime()));
			ps.executeUpdate();
			connection.fine("Added student " + studentID + " to Student");
			ps.close();

			// course
			int yearOfStudy = i.<Integer>getValue("yearOfStudy") + 1;
			ps = connection.prepareStatement("INSERT INTO StudentRegistration (studentID, yearOfStudy, registrationTypeID) VALUES (?, ?, ?)");
			ps.setInt(1, studentID);
			ps.setInt(2, yearOfStudy);
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

			boolean hasContact = i.getVar("hasContact") == Boolean.TRUE;
			if (hasContact)
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
			String email = hasContact ? i.getValue("contactAddress") : null;
			String courseType = registrationTypes[i.<Integer>getValue("courseTypeID")];
			java.util.Date dob = i.getValue("dob");
			tableEntries.add(PersonEntry.addStudent(studentID, title, forename, surname, email, yearOfStudy, courseType, null, dob));

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

}
