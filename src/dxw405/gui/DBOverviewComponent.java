package dxw405.gui;

import dxw405.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DBOverviewComponent extends JPanel implements ActionListener
{
	private DBModel model;

	public DBOverviewComponent(DBModel model)
	{
		super(new BorderLayout());
		this.model = model;

		// data table
		DBTableComponent tableComponent = new DBTableComponent(model);
		add(tableComponent, BorderLayout.CENTER);

		// control panel
		DBControlPanel controlPanel = new DBControlPanel(model, this);
		add(controlPanel, BorderLayout.NORTH);

		// model observers
		model.addObserver(tableComponent);
	}

	/**
	 * Initiate all components
	 */
	public void init()
	{
		model.populateTable();
	}


	@Override
	public void actionPerformed(ActionEvent e)
	{
		Action a = Utils.parseEnum(Action.class, e.getActionCommand());
		if (a == null)
			return;

		System.out.println("a = " + a);
	}
}
