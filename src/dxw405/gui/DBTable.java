package dxw405.gui;

import dxw405.util.Utils;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DBTable extends JTable
{
	private static final int MIN_COL_WIDTH = 1;
	private static final int MAX_COL_WIDTH = 150;

	private DBTableModel tableModel;

	public DBTable()
	{
		tableModel = new DBTableModel(this);
		setModel(tableModel);

		// sorting
		setAutoCreateRowSorter(true);
	}

	public void autofit()
	{
		TableColumnModel columnModel = getColumnModel();

		for (int i = 0; i < columnModel.getColumnCount(); i++)
		{
			int width = MIN_COL_WIDTH;
			for (int j = 0; j < getRowCount(); j++)
			{
				TableCellRenderer renderer = getCellRenderer(j, i);
				Component comp = prepareRenderer(renderer, j, i);

				int widthPref = comp.getPreferredSize().width;
				width = Math.max(widthPref + 1, width);
			}

			TableColumn column = columnModel.getColumn(i);
			column.setMinWidth(MIN_COL_WIDTH);
			column.setPreferredWidth(width);
			column.setMaxWidth(MAX_COL_WIDTH);
		}
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
	}
}

class DBTableModel extends DefaultTableModel
{
	private final static String[] COLUMNS = {"ID", "Title", "Forename", "Surname", "Email", "Office", "Year", "Course Type", "Tutor", "DOB"};
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(Utils.DATE_FORMAT);
	private DBTable table;

	private List<PersonEntry> entries;

	public DBTableModel(DBTable table)
	{
		this.table = table;
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
		table.autofit();
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
				return entry.office;
			case 6:
				return entry.yearOfStudy;
			case 7:
				return entry.courseType;
			case 8:
				return entry.tutorName;
			case 9:
				return entry.dob == null ? null : DATE_FORMAT.format(entry.dob);
			case 10:
				return entry.person;
			default:
				return "?";

		}
	}
}


