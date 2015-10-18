package dxw405;

import dxw405.util.Person;
import dxw405.util.Utils;

import java.io.*;
import java.sql.Date;
import java.sql.*;
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
		addRandomPeople(studentCount, 1433000, Person.STUDENT);
		connection.info("Created " + studentCount + " random students");

		addRandomPeople(lecturerCount, 1000, Person.LECTURER);
		connection.info("Created " + lecturerCount + " random lecturers");

		// cache ids for further use
		personIDs = gatherIDs();

		// student registrations
		addStudentRegistrations();
	}

	private void addStudentRegistrations()
	{
		List<Integer> studentIDs = personIDs.get(Person.STUDENT);
		String cmd = "INSERT INTO StudentRegistration (studentID, yearOfStudy, registrationTypeID) VALUES (?, ?, ?)";
		PreparedStatement ps;
		try
		{
			ps = connection.prepareStatement(cmd);

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

	private EnumMap<Person, List<Integer>> gatherIDs()
	{
		try
		{
			Statement statement = connection.createStatement();
			String query = "SELECT %s FROM %s";

			EnumMap<Person, List<Integer>> ret = new EnumMap<>(Person.class);

			for (Person p : Person.values())
			{
				ResultSet resultSet = statement.executeQuery(String.format(query, p.getIDName(), p.getTableName()));
				List<Integer> ids = new ArrayList<>();

				while (resultSet.next())
					ids.add(resultSet.getInt(1));

				ret.put(p, ids);
			}

			statement.close();

			return ret;

		} catch (SQLException e)
		{
			connection.severe("Could not gather IDs: " + e);
			return null;
		}
	}


	private void addRandomPeople(int count, int startingID, Person person)
	{
		String[] names = randomNames.takeNames(count * 2);
		int lastID = startingID;

		for (int i = 0; i < names.length - 1; i += 2)
		{
			int id = lastID++;
			int titleID = Utils.RANDOM.nextInt(titleCount) + 1;
			String forename = names[i];
			String surname = names[i + 1];

			Date dob = person == Person.LECTURER ? null : new Date(MIN_DATE + (long) (Utils.RANDOM.nextFloat() * (MAX_DATE - MIN_DATE)));
			String command = person == Person.STUDENT ?
					"INSERT INTO Student (studentID, titleID, forename, familyName, dateOfBirth) VALUES (?, ?, ?, ?, ?)" :
					"INSERT INTO Lecturer (lecturerID, titleID, forename, familyName) " + "VALUES (?, ?, ?, ?)";

			addPerson(command, id, titleID, forename, surname, dob, "Added " + forename + " " + surname);
		}

	}

	private void addPerson(String preparedStatement, int id, int titleID, String forename, String surname, Date dob, String logMessage)
	{
		try
		{
			PreparedStatement ps = connection.prepareStatement(preparedStatement);
			ps.setInt(1, id);
			ps.setInt(2, titleID);
			ps.setString(3, forename);
			ps.setString(4, surname);
			if (dob != null)
				ps.setDate(5, dob);

			ps.executeUpdate();
			ps.close();

			connection.fine(logMessage);

		} catch (SQLException e)
		{
			connection.warning("Could not add person: " + e);
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
