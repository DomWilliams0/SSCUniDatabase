package dxw405.gui;

import dxw405.DBConnection;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DBGui
{
	public DBGui(DBConnection connection)
	{
		// make pretty
		if (connection.getBooleanFromConfig("gui-os-skin"))
			prettifyUI(connection);

		// create components
		DBModel model = new DBModel(connection);
		DBOverviewComponent controller = new DBOverviewComponent(model);

		// create frame
		JFrame frame = new JFrame("University Database Management");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				connection.close();
			}
		});

		frame.setSize(connection.getIntFromConfig("gui-width"), connection.getIntFromConfig("gui-height"));
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);

		frame.add(controller);
		frame.setVisible(true);

		controller.init();
	}

	private void prettifyUI(DBConnection connection)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
		{
			connection.warning("Could not prettify the UI :( (" + e.getMessage() + ")");
		}
	}
}
