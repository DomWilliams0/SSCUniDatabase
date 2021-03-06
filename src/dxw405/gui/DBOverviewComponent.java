package dxw405.gui;

import dxw405.gui.dialog.DialogType;
import dxw405.gui.dialog.ProgressDialog;
import dxw405.gui.dialog.UserInput;
import dxw405.gui.dialog.dialogs.BaseDialog;
import dxw405.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DBOverviewComponent extends JPanel
{
	private DBTableComponent table;
	private DBModel model;

	public DBOverviewComponent(DBModel model)
	{
		super(new BorderLayout());
		this.model = model;

		// data table
		table = new DBTableComponent(new TablePopupMouseListener(), model);
		add(table, BorderLayout.CENTER);

		// control panel
		DBControlPanel controlPanel = new DBControlPanel(new ControlPanelListener());
		add(controlPanel, BorderLayout.NORTH);

		// model observers
		model.addObserver(table);
	}

	/**
	 * Initiate all components
	 */
	public void init()
	{
		ProgressDialog progress = new ProgressDialog("Fetching from database...");
		progress.begin();

		model.gatherEnums();
		table.init();

		progress.destroy();
	}

	/**
	 * Creates a contextual popup at the click location
	 *
	 * @param e The mouse click event
	 */
	private void showTablePopup(MouseEvent e)
	{
		if (e.isPopupTrigger())
		{
			PersonEntry entry = table.getEntry(e.getPoint());
			if (entry == null)
				return;

			JPopupMenu popup = table.createRightClickPopup(entry);
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	/**
	 * Popup menu listener for right clicking on the table
	 */
	private class TablePopupMouseListener extends MouseAdapter
	{

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
	}

	/**
	 * Button listener for add student and visibility buttons on the top control panel
	 */
	private class ControlPanelListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			Action a = Utils.parseEnum(Action.class, e.getActionCommand());
			if (a == null)
				return;

			// filtering
			if (a.getParent() == Action.VISIBILITY)
			{
				table.filter(a);
			}

			// add student
			else if (a.getParent() == Action.ADD)
			{
				UserInput input = BaseDialog.showDialog(DialogType.ADD_STUDENT, model);
				if (input == null) return;

				// add to database
				String errorMessage = model.addStudent(input);
				boolean success = errorMessage == null;

				// popup success/failure dialog
				String fullName = model.getTitles()[input.<Integer>getValue("titleID")] + ". " +
						input.getValue("forename") + " " + input.getValue("surname");

				if (success)
					JOptionPane.showMessageDialog(DBOverviewComponent.this, "Successfully added " + fullName,
							"Success", JOptionPane.INFORMATION_MESSAGE);
				else
					JOptionPane.showMessageDialog(DBOverviewComponent.this, "Couldn't add " + fullName + "\n" + errorMessage,
							"Failure", JOptionPane.ERROR_MESSAGE);
			}
		}

	}
}


