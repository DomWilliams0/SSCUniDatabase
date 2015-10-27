package dxw405.gui;

import dxw405.DBConnection;
import dxw405.gui.dialog.UserInput;
import dxw405.util.PersonType;
import dxw405.util.Utils;

import java.io.File;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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
		ResultSet[] allResults = connection.executeQueriesFromFile(new File(sqlFile));
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
					PersonType person = Utils.parseEnum(PersonType.class, personType);
					if (person == null)
						throw new SQLException("Bad person type specified in query (" + personType + ")");

					int id = resultSet.getInt(2);

					tableEntries.add(getEntry(person, id));
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
		String[] s = new String[lecturers.length];
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
		if (resultSet == null)
			return;

		try
		{
			while (resultSet.next())
			{
				int studentID = resultSet.getInt(1);
				int lecturerID = resultSet.getInt(2);

				tutors.put(studentID, lecturerID);

				PersonEntry entry = getEntryFromID(studentID);
				entry.setTutorID(lecturerID, this);

			}
		} catch (SQLException e)
		{
			connection.severe("Could not update tutor map: " + e);
			connection.logStackTrace(e);
		}
	}

	public Map<Integer, Integer> getTutors()
	{
		return tutors;
	}

	public Integer getTutorID(int studentID)
	{
		return tutors.get(studentID);
	}

	/**
	 * Creates a PersonEntry from the given resultset
	 * @param personType The person type
	 * @param id The person's ID
	 * @param resultSet The result set
	 * @return A new PersonEntry
	 */
	private PersonEntry getEntry(PersonType personType, int id, ResultSet resultSet) throws SQLException
	{
		String title = resultSet.getString(2);
		String forename = resultSet.getString(3);
		String surname = resultSet.getString(4);
		String email = resultSet.getString(5);

		PersonEntry entry = new PersonEntry(personType, id, title, forename, surname, email);

		if (personType == PersonType.STUDENT)
		{
			entry.setDOB(new java.util.Date(resultSet.getDate(6).getTime()));
			entry.setYearOfStudy(resultSet.getInt(7));
			entry.setCourseType(resultSet.getString(8));
			entry.setTutorID(resultSet.getInt(9));
			entry.setAddress(resultSet.getString(10));
			entry.setNOKName(resultSet.getString(11));
			entry.setNOKEmail(resultSet.getString(12));
			entry.setNOKAddress(resultSet.getString(13));

		} else if (personType == PersonType.LECTURER)
			entry.setOffice(resultSet.getString(6));

		return entry;
	}

	/**
	 * Gets the person with the given type and id from the database
	 *
	 * @param personType The person type
	 * @param id         The person's ID
	 * @return A new PersonEntry, or null if the operation failed
	 */
	public PersonEntry getEntry(PersonType personType, int id)
	{
		String queryFile = "sql-get-" + personType.toString().toLowerCase();
		PreparedStatement[] pss = connection.prepareStatementsFromFile(new File(connection.getSQLPath(queryFile)));

		if (pss == null)
			return null;

		try
		{
			PreparedStatement ps = pss[0];
			ps.setInt(1, id);
			ResultSet resultSet = ps.executeQuery();

			if (!resultSet.next())
				return null;

			PersonEntry ret = getEntry(personType, id, resultSet);

			ps.close();
			return ret;

		} catch (SQLException e)
		{
			connection.severe("Could not get " + personType + " with id " + id + ": " + e);
			connection.logStackTrace(e);
			return null;
		}
	}

	/**
	 * Finds the entry with the given ID
	 *
	 * @param id The ID
	 * @return The entry, or null if not found
	 */
	public PersonEntry getEntryFromID(int id)
	{
		for (PersonEntry entry : tableEntries)
			if (id == entry.getID())
				return entry;

		return null;
	}

	/**
	 * Finds the entry with the given name
	 *
	 * @param fullName Title. Forename Surname, as generated by PersonEntry#getFullName()
	 * @param type     The PersonType to check
	 * @return The entry, or null if not found
	 */
	public PersonEntry getEntryFromFullName(String fullName, PersonType type)
	{
		for (PersonEntry entry : tableEntries)
			if (entry.getPersonType() == type && entry.getFullName().equals(fullName))
				return entry;

		connection.severe("Could not find entry with full name: " + fullName);
		return null;
	}

	private PersonEntry[] gatherLecturers()
	{
		ResultSet[] resultSets = connection.executeQueriesFromFile(new File(connection.getSQLPath("sql-get-lecturers")));
		if (resultSets == null)
			return null;
		ResultSet resultSet = resultSets[0];

		List<PersonEntry> results = new ArrayList<>();

		try
		{
			while (resultSet.next())
			{
				int id = resultSet.getInt(1);
				results.add(PersonEntry.addLecturer(id, resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5),
						resultSet.getString(6)));
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
			connection.setAutoCommit(false);

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
			connection.finer("Added student " + studentID + " to Student");
			ps.close();

			// course
			int yearOfStudy = i.<Integer>getValue("yearOfStudy") + 1;
			ps = connection.prepareStatement("INSERT INTO StudentRegistration (studentID, yearOfStudy, registrationTypeID) VALUES (?, ?, ?)");
			ps.setInt(1, studentID);
			ps.setInt(2, yearOfStudy);
			ps.setInt(3, i.<Integer>getValue("courseTypeID") + 1);
			ps.executeUpdate();
			connection.finer("Added StudentRegistration for " + studentID);
			ps.close();

			// contacts
			if (i.getVar("hasNOK"))
			{
				ps = connection.prepareStatement("INSERT INTO NextOfKin (studentID, name, eMailAddress, postalAddress) VALUES (?, ?, ?, ?)");
				ps.setInt(1, studentID);
				ps.setString(2, i.getValue("nokName"));
				ps.setString(3, i.getValue("nokEmail"));
				ps.setString(4, i.getValue("nokAddress"));
				ps.executeUpdate();
				connection.finer("Added NextOfKin for " + studentID);
				ps.close();
			}

			boolean hasContact = i.getVar("hasContact");
			if (hasContact)
			{
				ps = connection.prepareStatement("INSERT INTO StudentContact (studentID, eMailAddress, postalAddress) VALUES (?, ?, ?)");
				ps.setInt(1, studentID);
				ps.setString(2, i.getValue("contactEmail"));
				ps.setString(3, i.getValue("contactAddress"));
				ps.executeUpdate();
				connection.finer("Added StudentContact for " + studentID);
				ps.close();
			}


			// create entry and add
			String title = titles[i.<Integer>getValue("titleID")];
			String email = hasContact ? i.getValue("contactAddress") : null;
			String courseType = registrationTypes[i.<Integer>getValue("courseTypeID")];
			java.util.Date dob = i.getValue("dob");
			tableEntries.add(PersonEntry.addStudent(studentID, title, forename, surname, email, yearOfStudy, courseType, null, dob));

			connection.commit();

			// update observers
			setChanged();
			notifyObservers();

			return null;


		} catch (SQLException e)
		{
			try
			{
				connection.rollback();
			} catch (SQLException e1)
			{
				connection.severe("Could not rollback transaction: " + e1);
				connection.logStackTrace(e1);
			}

			connection.severe("Could not add student: " + e);
			connection.logStackTrace(e);
			return e.getMessage();
		} finally
		{
			try
			{
				connection.setAutoCommit(true);
			} catch (SQLException e)
			{
				connection.severe("Could not enable auto commit: " + e);
				connection.logStackTrace(e);
			}
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
			if (entry.getID() == id)
				return true;
		return false;
	}

	public String addTutor(PersonEntry student, UserInput input)
	{
		// not a student
		if (student.getPersonType() != PersonType.STUDENT)
			return "Only students can have tutors";

		final int studentID = student.getID();

		// remove current tutor
		if (student.getTutorID() != null)
		{
			int oldTutor = student.getTutorID();

			// from student entry
			student.setTutorID(null);

			// from tutor map
			tutors.remove(studentID);

			// from database
			try
			{
				PreparedStatement ps = connection.prepareStatement("DELETE FROM Tutor WHERE studentID = ?");
				ps.setInt(1, studentID);
				ps.executeUpdate();
				connection.fine("Removed old tutor " + oldTutor + " from student " + studentID);
				ps.close();
			} catch (SQLException e)
			{
				setChanged();
				notifyObservers();

				connection.severe("Could not remove tutor from student: " + e);
				connection.logStackTrace(e);
				return e.getMessage();
			}

		}

		// add new tutor
		PersonEntry newTutor = input.getVar("lecturerEntry");
		try
		{
			// to student entry
			student.setTutorID(newTutor.getID(), this);

			// to tutor map
			tutors.put(studentID, student.getTutorID());

			// to database
			PreparedStatement ps = connection.prepareStatement("INSERT INTO Tutor (studentID, lecturerID) VALUES (?, ?)");
			ps.setInt(1, studentID);
			ps.setInt(2, lecturers[input.<Integer>getValue("lecturerID")].getID());
			ps.executeUpdate();
			connection.fine("Added student " + studentID + " to tutor group " + input.getValue("lecturerID"));
			ps.close();

			return null;
		} catch (SQLException e)
		{
			connection.severe("Could not add tutor for student: " + e);
			connection.logStackTrace(e);
			return e.getMessage();
		} finally
		{
			setChanged();
			notifyObservers();
		}
	}

	/**
	 * Populates the given entry with the corresponding contact and emergency contact information
	 * This is only loaded on demand
	 *
	 * @param entry The entry to populate
	 */
	public void populateStudent(PersonEntry entry)
	{
		if (entry.isPopulated())
			return;

		PreparedStatement[] pss = connection.prepareStatementsFromFile(new File(connection.getSQLPath("sql-full-student")));
		if (pss == null)
			return;

		try
		{
			PreparedStatement ps = pss[0];
			ps.setInt(1, entry.getID());
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next())
			{
				entry.setAddress(resultSet.getString(1));
				entry.setNOKName(resultSet.getString(2));
				entry.setNOKEmail(resultSet.getString(3));
				entry.setNOKAddress(resultSet.getString(4));
				break;
			}

			entry.setPopulated(true);
			connection.fine("Populated student " + entry.getID() + " with full details");
			ps.close();


		} catch (SQLException e)
		{
			connection.severe("Could not populate student " + entry.getID() + " with full details: " + e);
			connection.logStackTrace(e);
		}
	}

	public void logInfo(String msg)
	{
		connection.info(msg);
	}

	public void logSevere(String msg)
	{
		connection.severe(msg);
	}

	public void logWarning(String msg)
	{
		connection.warning(msg);
	}

	public void logFine(String msg)
	{
		connection.fine(msg);
	}

	public void logStackTrace(Exception e)
	{
		connection.logStackTrace(e);
	}
}
