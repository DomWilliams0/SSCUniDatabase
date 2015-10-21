package dxw405.gui;

import dxw405.gui.dialog.DialogType;
import dxw405.gui.dialog.UserInput;
import dxw405.gui.dialog.dialogs.BaseDialog;
import dxw405.util.Person;
import dxw405.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class DBTableComponent extends JPanel implements Observer
{
	private DBTable table;
	private DBModel model;

	public DBTableComponent(MouseListener mouseListener, DBModel model)
	{
		super(new BorderLayout());
		this.model = model;

		table = new DBTable();
		table.addMouseListener(mouseListener);

		JScrollPane scrollPane = new JScrollPane(this.table);
		add(scrollPane, BorderLayout.CENTER);
	}

	@Override
	public void update(Observable o, Object arg)
	{
		// failure
		if (arg != null)
			JOptionPane.showMessageDialog(this, arg, "Failed to retrieve data", JOptionPane.ERROR_MESSAGE);

		List<PersonEntry> entries = model.getTableEntries();
		DBTableModel tableModel = (DBTableModel) table.getModel();

		tableModel.setEntries(entries);
	}

	public void filter(Action visibility)
	{
		String desired;
		switch (visibility)
		{
			case VISIBLE_STUDENTS:
				desired = Person.STUDENT.toString().toUpperCase();
				break;
			case VISIBLE_LECTURERS:
				desired = Person.LECTURER.toString().toUpperCase();
				break;
			default:
				desired = null;
		}


		RowFilter<DBTableModel, Object> typeFilter = new RowFilter<DBTableModel, Object>()
		{
			@Override
			public boolean include(Entry<? extends DBTableModel, ?> entry)
			{
				return desired == null || entry.getStringValue(entry.getValueCount()).equals(desired);
			}
		};

		table.filter(typeFilter);
	}

	/**
	 * Gets the PersonEntry for the row that is under the given point
	 *
	 * @param point The point
	 * @return The corresponding PersonEntry, or null if invalid
	 */
	public PersonEntry getEntry(Point point)
	{
		// todo doesn't update when table is shuffled
		int row = table.rowAtPoint(point);
		if (row >= 0 && row < table.getRowCount())
			table.setRowSelectionInterval(row, row);
		else
			table.clearSelection();

		row = table.getSelectedRow();
		if (row < 0)
			return null;

		int actualRow = table.convertRowIndexToModel(row);
		return model.getTableEntries().get(actualRow);
	}

	/**
	 * Creates a popup menu for the given entry
	 *
	 * @param entry The entry
	 * @return A contextual popup menu
	 */
	public JPopupMenu createRightClickPopup(PersonEntry entry)
	{
		JPopupMenu popup = new JPopupMenu();

		// "title"
		popup.add(new JMenuItem("<html><u>" + entry.getPerson().getTableName() + "</u></html>", null)
		{
			@Override
			public void menuSelectionChanged(boolean isIncluded)
			{
				super.menuSelectionChanged(false);
			}

		});
		TablePopupMenuListener listener = new TablePopupMenuListener(entry);

		// add buttons
		JMenuItem report = new JMenuItem(RightClickTableAction.VIEW_REPORT.toString());
		report.addActionListener(listener);
		popup.add(report);
		if (entry.getPerson() == Person.STUDENT)
		{
			JMenuItem tutor = new JMenuItem(RightClickTableAction.ADD_TUTOR.toString());
			tutor.addActionListener(listener);
			popup.add(tutor);
		}

		return popup;
	}


	private class TablePopupMenuListener implements ActionListener
	{
		private final PersonEntry entry;

		public TablePopupMenuListener(PersonEntry entry)
		{
			this.entry = entry;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			RightClickTableAction action = Utils.parseEnum(RightClickTableAction.class, ((AbstractButton) e.getSource()).getText(), false);
			if (action == null) return;

			// add tutor
			if (action == RightClickTableAction.ADD_TUTOR)
			{
				UserInput input = BaseDialog.showDialog(DialogType.ADD_TUTOR, model, entry);
				if (input == null)
					return;

				// add to database
				String errorMessage = model.addTutor(entry, input);

				showResultPopup(null, errorMessage, "Added tutor for " + input.getVar("studentID"),
						"Couldn't add tutor for " + input.getVar("studentID") + ": " + errorMessage);
			}

			// view report
			else if (action == RightClickTableAction.VIEW_REPORT)
			{
				DialogType reportType;
				switch (entry.getPerson())
				{
					case STUDENT:
						reportType = DialogType.REPORT_STUDENT;
						break;
					case LECTURER:
						reportType = DialogType.REPORT_LECTURER;
						break;
					default:
						return;
				}

				BaseDialog.showDialog(reportType, model, entry);
			}
		}

		void showResultPopup(Component dialogParent, String errorMessage, String successMessage, String failureMessage)
		{

			boolean success = errorMessage == null;
			if (success)
				JOptionPane.showMessageDialog(dialogParent, successMessage,
						"Success", JOptionPane.INFORMATION_MESSAGE);
			else
				JOptionPane.showMessageDialog(dialogParent, failureMessage,
						"Failure", JOptionPane.ERROR_MESSAGE);

		}
	}
}