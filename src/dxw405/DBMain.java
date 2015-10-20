package dxw405;

import dxw405.gui.DBGui;
import dxw405.util.Utils;

public class DBMain
{
	public static void main(String[] args)
	{
		new DBMain().run(args);
	}

	public void run(String[] args)
	{
		// wrong amount of args
		if (args.length != 1)
			exit(getUsage());

		Command cmd = Utils.parseEnum(Command.class, args[0]);

		// invalid command
		if (cmd == null)
		{
			System.err.println("Unknown command");
			exit(getUsage());
			return; // to make intellij happy
		}

		// open connection to database
		DBConnection connection = new DBConnection("res/config.properties");

		switch (cmd)
		{
			case CREATE:
				createTables(connection);
				break;
			case GUI:
				startGUI(connection);
				break;
		}

		// commands must close the DB connection themselves
	}

	/**
	 * Drops and recreates all tables, populating them with random data
	 */
	private void createTables(DBConnection connection)
	{
		DBCreation creation = new DBCreation(connection);
		creation.createTables();
		creation.populateTables();
		connection.close();
	}


	/**
	 * Opens the GUI for database management
	 */
	private void startGUI(DBConnection connection)
	{
		new DBGui(connection);
	}


	/**
	 * @return A list of all possible commands in the format [cmd1 | cmd2...]
	 */
	private String getUsage()
	{
		StringBuilder sb = new StringBuilder("Usage: [");

		final Command[] commands = Command.values();
		for (int i = 0; i < commands.length; i++)
		{
			sb.append(commands[i].toString().toLowerCase());
			if (i != commands.length - 1)
				sb.append(" | ");
		}

		sb.append("]");
		return sb.toString();
	}

	/**
	 * Exits the program after printing the given message to stderr
	 *
	 * @param msg The error message
	 */
	private void exit(String msg)
	{
		System.err.println(msg);
		System.exit(1);
	}

	enum Command
	{
		CREATE,
		GUI
	}
}
