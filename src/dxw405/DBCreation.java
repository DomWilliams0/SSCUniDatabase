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
			while (resultSet.next()) ret = resultSet.getInt(1);

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
		// drop tables first
		boolean success = connection.executeUpdateFromFile(new File(connection.getSQLPath("sql-drop-tables")));
		if (success) connection.info("Dropped all tables");

		// load and execute table creation commands
		String inputFile = connection.getSQLPath("sql-create-tables");

		success = connection.executeUpdateFromFile(new File(inputFile), Level.INFO);
		if (success) connection.info("Created tables successfully from (" + inputFile + ")");

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
		if (randomNames == null) return;

		connection.fine("Loaded " + randomNames.size() + " random names");


		try
		{
			// create students and lecturers
			addRandomPeople(Person.STUDENT, studentCount, 1433000);
			connection.info("Created " + studentCount + " random students");
			addRandomPeople(Person.LECTURER, lecturerCount, 1252000);
			connection.info("Created " + lecturerCount + " random lecturers");

			// student registrations
			addStudentRegistrations();

			// contacts and kin
			addContacts();
			addNextOfKins();

			// tutors
			addTutors();

		} catch (SQLException e)
		{
			connection.severe("Could not populate tables: " + e);
			e.printStackTrace();
		}


	}

	/**
	 * Assigns a lecturer at random to every student
	 */
	private void addTutors() throws SQLException
	{
		List<Integer> studentIDs = personIDs.get(Person.STUDENT);
		List<Integer> lecturerIDs = personIDs.get(Person.LECTURER);
		final int studentCount = studentIDs.size();
		final int lecturerCount = lecturerIDs.size();

		int classSize = studentCount / lecturerCount;

		Map<Integer, List<Integer>> tutorGroups = new HashMap<>();

		// add equal sized classes
		for (int i = 0; i < lecturerCount; i++)
		{
			Integer lecturerID = lecturerIDs.get(i);
			List<Integer> students = new ArrayList<>(classSize);

			for (int j = 0; j < classSize; j++)
			{
				Integer studentID = studentIDs.get((i * classSize) + j);
				students.add(studentID);
			}

			tutorGroups.put(lecturerID, students);
		}

		// put remaining in random classes
		int remaining = (studentCount - (classSize * lecturerCount));
		if (remaining != 0)
		{
			connection.fine("Adding " + remaining + " overflow students to random tutor groups");

			for (int i = 0; i < remaining; i++)
			{
				Integer studentID = studentIDs.get(studentCount - i - 1);
				Integer lecturerID = lecturerIDs.get(Utils.RANDOM.nextInt(lecturerCount));

				List<Integer> students = tutorGroups.get(lecturerID);
				boolean replaceCurrent = students == null;
				if (replaceCurrent) students = new ArrayList<>(1);

				students.add(studentID);

				if (replaceCurrent) tutorGroups.put(lecturerID, students);
			}

		}

		// execute statements
		PreparedStatement ps = connection.prepareStatement("INSERT INTO Tutor (studentID, lecturerID) VALUES (?, ?)");
		for (Map.Entry<Integer, List<Integer>> entry : tutorGroups.entrySet())
		{
			Integer lecturerID = entry.getKey();
			ps.setInt(2, lecturerID);

			List<Integer> students = entry.getValue();
			Collections.shuffle(students);

			for (Integer studentID : students)
			{
				ps.setInt(1, studentID);
				ps.addBatch();
			}
		}

		ps.executeBatch();
		ps.close();
	}

	private void addNextOfKins() throws SQLException
	{
		List<Integer> studentIDs = personIDs.get(Person.STUDENT);
		String[] names = randomNames.takeNames(studentIDs.size() * 2);

		String cmd = "INSERT INTO NextOfKin (studentID, name, eMailAddress, postalAddress) VALUES (?, ?, ?, ?)";
		PreparedStatement ps = connection.prepareStatement(cmd);

		int nameIndex = 0;
		for (Integer studentID : studentIDs)
		{
			String name = names[nameIndex] + " " + names[nameIndex + 1];
			String email = RandomGenerator.GEN_EMAIL.generate();
			String address = RandomGenerator.GEN_POSTAL.generate();

			ps.setInt(1, studentID);
			ps.setString(2, name);
			ps.setString(3, email);
			ps.setString(4, address);

			ps.addBatch();

			connection.fine("Added next of kin for " + studentID);

			nameIndex += 2;
		}

		ps.executeBatch();
		ps.close();

		connection.info("Added " + studentIDs.size() + " next-of-kins");

	}

	/**
	 * Creates random Student/LecturerContacts for every person
	 */
	private void addContacts() throws SQLException
	{
		addContacts(Person.STUDENT, RandomGenerator.GEN_EMAIL_BHAM, RandomGenerator.GEN_POSTAL);
		addContacts(Person.LECTURER, RandomGenerator.GEN_OFFICE, RandomGenerator.GEN_EMAIL_BHAM);
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

		List<Integer> ids = personIDs.get(person);
		for (Integer id : ids)
		{
			ps.setInt(1, id);
			ps.setString(2, secondValue.generate());
			ps.setString(3, thirdValue.generate());

			ps.addBatch();

			connection.fine("Added contact for " + id);
		}

		ps.executeBatch();
		ps.close();

		connection.info("Added " + ids.size() + " " + person.getTableName().toLowerCase() + "s");
	}


	/**
	 * Generates a random StudentRegistration for every student
	 */
	private void addStudentRegistrations() throws SQLException
	{
		List<Integer> studentIDs = personIDs.get(Person.STUDENT);
		String cmd = "INSERT INTO StudentRegistration (studentID, yearOfStudy, registrationTypeID) VALUES (?, ?, ?)";
		PreparedStatement ps = connection.prepareStatement(cmd);

		for (Integer studentID : studentIDs)
		{
			int yearOfStudy = Utils.RANDOM.nextInt(5) + 1;
			int regType = Utils.RANDOM.nextInt(registrationTypeCount) + 1;

			ps.setInt(1, studentID);
			ps.setInt(2, yearOfStudy);
			ps.setInt(3, regType);

			ps.addBatch();

			connection.fine("Added StudentRegistration for " + studentID);

		}

		ps.executeBatch();
		ps.close();


		connection.info("Registered " + studentIDs.size() + " students");
	}

	/**
	 * Generates random people
	 *
	 * @param person     Person type
	 * @param count      Number of people to generate
	 * @param startingID Person ID to start at
	 */
	private void addRandomPeople(Person person, int count, int startingID) throws SQLException
	{
		String command = person == Person.STUDENT ? "INSERT INTO Student (studentID, titleID, forename, familyName, dateOfBirth) VALUES (?, ?, ?, ?, ?)" : "INSERT INTO Lecturer (lecturerID, titleID, forename, familyName) " + "VALUES (?, ?, ?, ?)";
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
			if (dob != null) ps.setDate(5, dob);

			ps.addBatch();
			ids.add(id);

			connection.fine("Added " + person.getTableName().toLowerCase() + " " + forename + " " + surname);
		}

		ps.executeBatch();
		ps.close();

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
				if (line != null) names[nameIndex++] = line;
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
			if (index + count >= names.length) throw new RuntimeException("All " + names.length + " random names have been used");

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
