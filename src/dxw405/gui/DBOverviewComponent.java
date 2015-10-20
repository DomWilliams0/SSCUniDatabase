package dxw405.gui;

import dxw405.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DBOverviewComponent extends JPanel implements ActionListener
{
	private DBTableComponent table;
	private DBModel model;

	public DBOverviewComponent(DBModel model)
	{
		super(new BorderLayout());
		this.model = model;

		// data table
		table = new DBTableComponent(model);
		add(table, BorderLayout.CENTER);

		// control panel
		DBControlPanel controlPanel = new DBControlPanel(model, this);
		add(controlPanel, BorderLayout.NORTH);

		// model observers
		model.addObserver(table);
	}

	/**
	 * Initiate all components
	 */
	public void init()
	{
		model.gatherEnums();
		model.populateTable();
	}


	@Override
	public void actionPerformed(ActionEvent e)
	{
		Action a = Utils.parseEnum(Action.class, e.getActionCommand());
		if (a == null)
			return;

		// filtering
		if (a.getParent() == Action.VISIBILITY)
			table.filter(a);

		else if (a.getParent() == Action.ADD)
		{
			AddStudentInput input = AddStudentDialog.showPopup(model);
			if (input == null) return;

			// add to database
			String errorMessage = model.addStudent(input);
			boolean success = errorMessage == null;

			// popup success/failure dialog
			String fullName = model.getTitles()[input.titleID] + ". " + input.forename + " " + input.surname;
			if (success)
				JOptionPane.showMessageDialog(this, "Successfully added " + fullName, "Success", JOptionPane.INFORMATION_MESSAGE);
			else
				JOptionPane.showMessageDialog(this, "Couldn't add " + fullName + ": " + errorMessage, "Failure", JOptionPane.ERROR_MESSAGE);
		}
	}
}
