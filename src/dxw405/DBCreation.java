package dxw405;

import dxw405.util.Person;
import dxw405.util.RandomGenerator;
import dxw405.util.Utils;

import java.io.*;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

/**
 * Creates and populates the database
 */
public class DBCreation
{
	private static final long MIN_DATE = 631152000000L; // 1990-1-1
	private static final long MAX_DATE = 946684800000L; // 2000-1-1

	private DBConnection connection;
	private RandomNames randomNames;

	private int titleCount;
	private int registrationTypeCount;

	private EnumMap<Person, List<Integer>> personIDs;

	public DBCreation(DBConnection connection)
	{
		this.connection = connection;
		this.randomNames = null;
		this.personIDs = new EnumMap<>(Person.class);

		for (Person p : Person.values())
			personIDs.put(p, new ArrayList<>());
	}

	private void countEnums()
	{
		registrationTypeCount = count("registrationTypeID", "RegistrationType");
		titleCount = count("titleID", "Titles");
	}

	/**
	 * Executes a simple COUNT(column) FROM table query
	 *
	 * @param column The column to count
	 * @param table  The table to count in
	 * @return The query result, or -1 if the operation failed
	 */
	private int count(String column, String table)
	{
		try
		{
			ResultSet resultSet = connection.executeQuery("SELECT COUNT(" + column + ") FROM " + table);
			int ret = -1;
			while (resultSet.next())
				ret = resultSet.getInt(1);

			resultSet.close();
			return ret;

		} catch (SQLException e)
		{
			connection.severe("Could not count column " + column + " in table " + table + ": " + e);
			return -1;
		}

	}


	/**
	 * Creates the tables by executing the commands in the create-tables SQL file
	 */
	public void createTables()
	{
		String inputFile = connection.getSQLPath("sql-create-tables");

		boolean success = connection.executeFile(new File(inputFile), Level.INFO);
		if (success)
			connection.info("Created tables successfully from (" + inputFile + ")");

		countEnums();
	}

	/**
	 * Populates the tables with randomly generated data
	 */
	public void populateTables()
	{
		int totalRandomNames = 0;

		// student first+second names, contacts and next of kin
		int studentCount = connection.getIntFromConfig("random-student-count");
		totalRandomNames += (studentCount * 2) * 3;

		// lecturer first+second names and contacts
		int lecturerCount = connection.getIntFromConfig("random-lecturer-count");
		totalRandomNames += (lecturerCount * 2) * 2;

		randomNames = getRandomNames(totalRandomNames, connection.getResourcePath("res-random-names"));
		if (randomNames == null)
			return;

		connection.fine("Loaded " + randomNames.size() + " random names");


		// create students and lecturers
		addRandomPeople(Person.STUDENT, studentCount, 1433000);
		connection.info("Created " + studentCount + " random students");
		addRandomPeople(Person.LECTURER, lecturerCount, 1000);
		connection.info("Created " + lecturerCount + " random lecturers");

		// student registrations
		addStudentRegistrations();

		// contacts
		addPeopleContacts();

	}

	/**
	 * Creates random Student/LecturerContacts for every person
	 */
	private void addPeopleContacts()
	{
		try
		{
			addContacts(Person.STUDENT, new RandomGenerator.RandomEmail(), new RandomGenerator.RandomAddress());
			addContacts(Person.LECTURER, new RandomGenerator.RandomOffice(), new RandomGenerator.RandomBhamEmail());

		} catch (SQLException e)
		{
			connection.severe("Could not add contacts: " + e);
		}

	}

	/**
	 * Adds a Student/LecturerContact
	 *
	 * @param person      Person type
	 * @param secondValue 2nd column value
	 * @param thirdValue  3rd column value
	 */
	private void addContacts(Person person, RandomGenerator secondValue, RandomGenerator thirdValue) throws SQLException
	{
		final String query = "INSERT INTO %s VALUES (?, ?, ?)";
		PreparedStatement ps = connection.prepareStatement(String.format(query, person.getContactTableName()));

		for (Integer id : personIDs.get(person))
		{
			ps.setInt(1, id);
			ps.setString(2, secondValue.generate());
			ps.setString(3, thirdValue.generate());

			ps.executeUpdate();
		}

		ps.close();
	}


	/**
	 * Generates a random StudentRegistration for every student
	 */
	private void addStudentRegistrations()
	{
		List<Integer> studentIDs = personIDs.get(Person.STUDENT);
		String cmd = "INSERT INTO StudentRegistration (studentID, yearOfStudy, registrationTypeID) VALUES (?, ?, ?)";
		try
		{
			PreparedStatement ps = connection.prepareStatement(cmd);

			for (Integer studentID : studentIDs)
			{
				int yearOfStudy = Utils.RANDOM.nextInt(5) + 1;
				int regType = Utils.RANDOM.nextInt(registrationTypeCount) + 1;

				try
				{
					ps.setInt(1, studentID);
					ps.setInt(2, yearOfStudy);
					ps.setInt(3, regType);

					ps.executeUpdate();

					connection.fine("Added StudentRegistration for " + studentID);

				} catch (SQLException e)
				{
					connection.severe("Could not add student registration: " + e);
				}
			}

			ps.close();

		} catch (SQLException e)
		{
			connection.severe("Could not create/close PrepareStatement for student registrations: " + e);
			return;
		}

		connection.info("Registered " + studentIDs.size() + " students");
	}

	/**
	 * Generates random people
	 *
	 * @param person     Person type
	 * @param count      Number of people to generate
	 * @param startingID Person ID to start at
	 */
	private void addRandomPeople(Person person, int count, int startingID)
	{
		try
		{
			String command = person == Person.STUDENT ?
					"INSERT INTO Student (studentID, titleID, forename, familyName, dateOfBirth) VALUES (?, ?, ?, ?, ?)" :
					"INSERT INTO Lecturer (lecturerID, titleID, forename, familyName) " + "VALUES (?, ?, ?, ?)";
			PreparedStatement ps = connection.prepareStatement(command);

			String[] names = randomNames.takeNames(count * 2);
			int lastID = startingID;
			List<Integer> ids = personIDs.get(person);

			for (int i = 0; i < names.length - 1; i += 2)
			{
				// generate stats
				int id = lastID++;
				int titleID = Utils.RANDOM.nextInt(titleCount) + 1;
				String forename = names[i];
				String surname = names[i + 1];
				Date dob = person == Person.LECTURER ? null : new Date(MIN_DATE + (long) (Utils.RANDOM.nextFloat() * (MAX_DATE - MIN_DATE)));

				// execute
				ps.setInt(1, id);
				ps.setInt(2, titleID);
				ps.setString(3, forename);
				ps.setString(4, surname);
				if (dob != null)
					ps.setDate(5, dob);

				ps.executeUpdate();
				ids.add(id);

				connection.fine("Added " + person.getTableName() + " " + forename + " " + surname);
			}

			ps.close();
		} catch (SQLException e)
		{
			connection.severe("Could not add random people (" + person.getTableName() + "): " + e);
		}

	}


	/**
	 * Reads n random names from the given file
	 *
	 * @param count         Number of random names to load
	 * @param namesFilePath Path to the file from which to load random names
	 * @return A RandomNamesStream if successful, otherwise null
	 */
	private RandomNames getRandomNames(int count, String namesFilePath)
	{
		File namesFile = new File(namesFilePath);

		if (!Utils.validateFile(connection, namesFile))
		{
			connection.severe("Could not populate the database with random names");
			return null;
		}

		try
		{
			// count line length
			final long fileLength = countLines(namesFile);

			String[] names = new String[count];
			int nameIndex = 0;

			// generate n random names
			long[] lines = new long[names.length];
			for (int i = 0; i < lines.length; i++)
				lines[i] = (long) Math.floor(Utils.RANDOM.nextFloat() * fileLength);
			Arrays.sort(lines);

			// visit these lines
			BufferedReader reader = new BufferedReader(new FileReader(namesFile));
			for (int i = 0; i < lines.length - 1; i++)
			{
				long skipDistance = lines[i + 1] - lines[i];
				for (int j = 0; j < skipDistance; j++)
					reader.readLine();

				String line = reader.readLine();
				if (line != null)
					names[nameIndex++] = line;
			}
			reader.close();

			// top up with random repeats
			int diff = names.length - nameIndex;
			for (int i = 0; i < diff; i++)
			{
				int src = Utils.RANDOM.nextInt(nameIndex);
				names[nameIndex + i] = names[src];
			}

			// shuffle
			List<String> namesList = Arrays.asList(names);
			Collections.shuffle(namesList);

			for (int i = 0; i < namesList.size(); i++)
				names[i] = namesList.get(i);

			return new RandomNames(names);

		} catch (IOException e)
		{
			connection.severe("Could not load random names from " + namesFile.getPath() + ": " + e);
			return null;
		}
	}

	private long countLines(File stream) throws IOException
	{
		LineNumberReader reader = new LineNumberReader(new FileReader(stream));
		reader.skip(Long.MAX_VALUE);
		long count = reader.getLineNumber();
		reader.close();

		return count;
	}

	/**
	 * Pool for random names, which holds a fixed number of names randomly chosen from a file of names
	 */
	private class RandomNames
	{
		private String[] names;
		private int index;

		public RandomNames(String[] names)
		{
			this.names = names;
			this.index = 0;
		}

		public String[] takeNames(int count)
		{
			// not enough
			if (index + count >= names.length)
				throw new RuntimeException("All " + names.length + " random names have been used");

			String[] ret = new String[count];
			System.arraycopy(names, index, ret, 0, count);
			index += count;

			return ret;
		}

		public int size()
		{
			return names == null ? 0 : names.length;
		}
	}

}
