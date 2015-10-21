package dxw405.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DBTable extends JTable
{
	private DBTableModel tableModel;

	public DBTable()
	{
		tableModel = new DBTableModel();
		setModel(tableModel);

		// sorting
		setAutoCreateRowSorter(true);
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		return false;
	}

	public void filter(RowFilter<DBTableModel, Object> filter)
	{
		TableRowSorter<DBTableModel> sorter = new TableRowSorter<>(tableModel);
		sorter.setRowFilter(filter);
		setRowSorter(sorter);
		// todo auto resize columns
	}
}

class DBTableModel extends DefaultTableModel
{
	private final static String[] COLUMNS = {"ID", "Title", "Forename", "Surname", "Email", "Year", "Course Type", "Tutor", "DOB"};
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	private List<PersonEntry> entries;

	public DBTableModel()
	{
		this.entries = new ArrayList<>();
		setColumnIdentifiers(COLUMNS);
	}

	/**
	 * Updates the table data with the given entries
	 *
	 * @param entries The new entries
	 */
	public void setEntries(List<PersonEntry> entries)
	{
		this.entries = entries;
		fireTableDataChanged();
	}


	@Override
	public int getRowCount()
	{
		return entries == null ? 0 : entries.size();
	}

	@Override
	public int getColumnCount()
	{
		return COLUMNS.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		PersonEntry entry = entries.get(rowIndex);
		switch (columnIndex)
		{
			case 0:
				return entry.id;
			case 1:
				return entry.title + ".";
			case 2:
				return entry.forename;
			case 3:
				return entry.surname;
			case 4:
				return entry.email;
			case 5:
				return entry.yearOfStudy;
			case 6:
				return entry.courseType;
			case 7:
				return entry.tutorID;
			case 8:
				return entry.dob == null ? null : DATE_FORMAT.format(entry.dob);
			case 9:
				return entry.person;
			default:
				return "?";

		}
	}
}


