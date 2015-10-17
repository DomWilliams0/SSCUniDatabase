package dxw405;

import dxw405.util.Utils;

import java.io.*;
import java.sql.Date;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

/**
 * Creates and populates the database
 */
public class DBCreation
{
	private DBConnection connection;

	public DBCreation(DBConnection connection)
	{
		this.connection = connection;
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

	}

	/**
	 * Populates the tables with randomly generated ata
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

		String[] randomNames = new String[totalRandomNames];
		getRandomNames(randomNames, connection.getResourcePath("res-random-names"));

		// create students
		addRandomStudents(studentCount, randomNames);

		//		// first : second names
		//		for (int i = 0; i < names.length - 1; i += 2)
		//		{
		//			String forename = names[i];
		//			String surname = names[i + 1];
		//
		//			System.out.println(forename + " " + surname);
		//		}
	}

	private void addRandomStudents(int count, String[] randomNames)
	{

	}

	private void addStudent(int studentID, int titleID, String forename, String surname, Date dob)
	{
		// todo
	}

	private void addLecturer(int lecturerID, int titleID, String forename, String surname)
	{
		// todo
	}


	/**
	 * Reads n random names from the given file
	 *
	 * @param names         Array to fill with random names
	 * @param namesFilePath Path to the file from which to load random names
	 * @return If the operation succeeded
	 */
	private boolean getRandomNames(String[] names, String namesFilePath)
	{
		File namesFile = new File(namesFilePath);

		if (!Utils.validateFile(connection, namesFile))
		{
			connection.severe("Could not populate the database with random names");
			return false;
		}

		try
		{
			// count line length
			final long fileLength = countLines(namesFile);

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

			return true;


		} catch (IOException e)
		{
			connection.severe("Could not load random names from " + namesFile.getPath() + ": " + e);
			return false;
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


}
