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
		model.populateTable();
		model.gatherEnums();
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
			AddStudentDialog addStudent = new AddStudentDialog(model);
			if (!addStudent.display(this))
				return;

			// todo parse arguments

		}
	}
}
