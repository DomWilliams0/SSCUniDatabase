package dxw405.gui;

import dxw405.util.Person;

import javax.swing.*;
import java.awt.*;
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

	public int selectRow(Point point)
	{
		int row = table.rowAtPoint(point);
		if (row >= 0 && row < table.getRowCount())
			table.setRowSelectionInterval(row, row);
		else
			table.clearSelection();

		return table.getSelectedRow();
	}
}
