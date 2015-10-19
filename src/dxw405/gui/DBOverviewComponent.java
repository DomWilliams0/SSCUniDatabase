package dxw405.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DBOverviewComponent extends JPanel implements ActionListener
{
	public DBOverviewComponent(DBModel model)
	{
		super(new BorderLayout());

		// data table
		DBTableComponent tableComponent = new DBTableComponent(model);
		add(tableComponent, BorderLayout.CENTER);

		// control panel
		DBControlPanel controlPanel = new DBControlPanel(model, this);
		add(controlPanel, BorderLayout.NORTH);

		// model observers
		model.addObserver(tableComponent);

		// populate table initially
		model.populateTable();
	}


	@Override
	public void actionPerformed(ActionEvent e)
	{
		Action a = Action.parse(e.getActionCommand());
		if (a == null)
			return;

		System.out.println("a = " + a);
	}
}
