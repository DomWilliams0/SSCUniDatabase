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

	private DBConnection connection;

	public DBModel(DBConnection connection)
	{
		this.connection = connection;
	}

	public List<PersonEntry> fetchEntries()
	{
		ResultSet[] allResults = connection.executeQueriesFromFile(new File(connection.getSQLPath("sql-populate-tables")));
		if (allResults == null)
		{
			setChanged();
			notifyObservers();
			return null;
		}

		List<PersonEntry> entries = new ArrayList<>();

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

					entries.add(getEntry(person, id));
				}
			}

		} catch (SQLException e)
		{
			connection.severe("Could not retrieve entries: " + e);
			connection.logStackTrace(e);
		}

		return entries;
	}

	public String[] getTitles()
	{
		return titles;
	}

	public String[] getRegistrationTypes()
	{
		return registrationTypes;
	}

	public List<String> getLecturerNames()
	{
		ResultSet[] results = connection.executeQueriesFromFile(new File(connection.getSQLPath("sql-get-lecturer-names")));

		if (results == null)
			return null;

		List<String> names = new ArrayList<>();
		try
		{
			ResultSet resultSet = results[0];

			while (resultSet.next())
			{
				String name = String.format("%d: %s. %s %s", resultSet.getInt(1),
						resultSet.getString(2), resultSet.getString(3), resultSet.getString(4));
				names.add(name);
			}

			resultSet.close();


			return names;

		} catch (SQLException e)
		{
			connection.severe("Could not get lecturer names: " + e);
			connection.logStackTrace(e);
			return null;
		}
	}

	public void gatherEnums()
	{
		titles = gatherEnum("Titles", "titleString");
		registrationTypes = gatherEnum("RegistrationType", "description");
	}

	/**
	 * @return Gets a map of tutors, in the format studentID:lecturerID, or null if the operation failed
	 */
	public Map<Integer, Integer> getTutors()
	{

		ResultSet resultSet = connection.executeQuery("SELECT studentID, lecturerID FROM Tutor");
		if (resultSet == null)
			return null;

		Map<Integer, Integer> tutorIDs = new TreeMap<>();

		try
		{
			while (resultSet.next())
			{
				int studentID = resultSet.getInt(1);
				int lecturerID = resultSet.getInt(2);

				tutorIDs.put(studentID, lecturerID);
			}

			resultSet.close();
			return tutorIDs;

		} catch (SQLException e)
		{
			connection.severe("Could not get tutors: " + e);
			connection.logStackTrace(e);
			return null;
		}
	}

	/**
	 * Gets the ID of the given student's tutur
	 *
	 * @param studentID The student's ID
	 * @return The tutor's ID, or null if the operation failed
	 */
	public Integer getTutorID(int studentID)
	{
		try
		{
			PreparedStatement ps = connection.prepareStatement("SELECT lecturerID FROM Tutor WHERE studentID = ?");
			ps.setInt(1, studentID);

			ResultSet resultSet = ps.executeQuery();

			if (!resultSet.next())
				return null;

			return resultSet.getInt(1);

		} catch (SQLException e)
		{
			connection.severe("Could not get tutor ID for student " + studentID + ": " + e);
			connection.logStackTrace(e);
			return null;
		}
	}

	/**
	 * Creates a PersonEntry from the given resultset
	 *
	 * @param personType The person type
	 * @param id         The person's ID
	 * @param resultSet  The result set
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

			connection.commit();

			// update observers
			setChanged();
			notifyObservers();

			return null;


		} catch (SQLException e)
		{
			connection.rollback();
			connection.severe("Could not add student: " + e);
			connection.logStackTrace(e);
			return e.getMessage();
		} finally
		{
			connection.setAutoCommit(true);
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
		// todo count select where id = id
		return false;
	}

	public String addTutor(PersonEntry student, UserInput input)
	{
		// not a student
		if (student.getPersonType() != PersonType.STUDENT)
			return "Only students can have tutors";

		final int studentID = student.getID();

		PreparedStatement psRem = null;
		PreparedStatement psAdd = null;

		boolean success = true;
		connection.setAutoCommit(false);

		// remove current tutor
		if (student.getTutorID() != null)
		{
			int oldTutor = student.getTutorID();

			try
			{
				psRem = connection.prepareStatement("DELETE FROM Tutor WHERE studentID = ?");
				psRem.setInt(1, studentID);
				psRem.executeUpdate();
				connection.fine("Removed old tutor " + oldTutor + " from student " + studentID);
			} catch (SQLException e)
			{
				success = false;
				connection.severe("Could not remove tutor from student: " + e);
				connection.logStackTrace(e);
				return e.getMessage();
			}

		}

		// add new tutor
		try
		{
			psAdd = connection.prepareStatement("INSERT INTO Tutor (studentID, lecturerID) VALUES (?, ?)");
			psAdd.setInt(1, studentID);
			psAdd.setInt(2, input.getVar("lecturerID"));
			psAdd.executeUpdate();
			connection.fine("Added student " + studentID + " to tutor group " + input.getVar("lecturerID"));

			return null;
		} catch (SQLException e)
		{
			success = false;
			connection.severe("Could not add tutor for student: " + e);
			connection.logStackTrace(e);
			return e.getMessage();
		} finally
		{
			if (success)
				connection.commit();
			else
				connection.rollback();

			try
			{
				connection.setAutoCommit(false);

				if (psAdd != null)
					psAdd.close();
				if (psRem != null)
					psRem.close();
			} catch (SQLException e)
			{
				connection.severe("Could not close statements: " + e);
				connection.logStackTrace(e);
			}

			setChanged();
			notifyObservers(studentID);
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

			if (!resultSet.next())
				return;

			entry.setAddress(resultSet.getString(1));
			entry.setNOKName(resultSet.getString(2));
			entry.setNOKEmail(resultSet.getString(3));
			entry.setNOKAddress(resultSet.getString(4));

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
