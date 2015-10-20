package dxw405.gui;

import dxw405.util.Person;
import dxw405.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class DBOverviewComponent extends JPanel implements ActionListener, MouseListener
{
	private DBTableComponent table;
	private DBModel model;

	public DBOverviewComponent(DBModel model)
	{
		super(new BorderLayout());
		this.model = model;

		// data table
		table = new DBTableComponent(this, model);
		add(table, BorderLayout.CENTER);

		// control panel
		DBControlPanel controlPanel = new DBControlPanel(this);
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

	private void showTablePopup(MouseEvent e)
	{

		int row = table.selectRow(e.getPoint());
		if (row < 0)
			return;

		if (e.isPopupTrigger())
		{
			PersonEntry entry = model.getTableEntries().get(row);
			JPopupMenu popup = new JPopupMenu();

			// "title"
			popup.add(new JMenuItem("<html><u>" + entry.person.getTableName() + "</u></html>", null)
			{
				@Override
				public void menuSelectionChanged(boolean isIncluded)
				{
					super.menuSelectionChanged(false);
				}

			});
			popup.add(new JMenuItem("View report"));
			if (entry.person == Person.STUDENT)
				popup.add(new JMenuItem("Add tutor"));

			// todo separate action listeners for both buttons

			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		showTablePopup(e);
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		showTablePopup(e);
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{

	}

	@Override
	public void mouseExited(MouseEvent e)
	{

	}

}
