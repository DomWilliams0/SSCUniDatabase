package dxw405.gui;

import dxw405.util.PersonType;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
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

	public PersonType getPersonTypeAt(int row)
	{
		return (PersonType) tableModel.getValueAt(row, tableModel.getPersonTypeColumn());
	}

	public int getIDAt(int row)
	{
		Integer id = (Integer) tableModel.getValueAt(row, tableModel.getIDColumn());
		return id == null ? -1 : id;
	}
}

class DBTableModel extends DefaultTableModel
{
	private final static String[] COLUMNS = {"ID", "Title", "Forename", "Surname", "Email", "Office", "Year", "Course Type", "Tutor", "DOB"};
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
				return entry.getID();
			case 1:
				return entry.getTitle();
			case 2:
				return entry.getForename();
			case 3:
				return entry.getSurname();
			case 4:
				return entry.getEmail();
			case 5:
				return entry.getOffice();
			case 6:
				return entry.getYearOfStudy();
			case 7:
				return entry.getCourseType();
			case 8:
				return entry.getTutorName();
			case 9:
				return entry.getDOBFormatted();
			case 10:
				return entry.getPersonType();
			default:
				return "?";

		}
	}

	public int getPersonTypeColumn()
	{
		return COLUMNS.length;
	}

	public int getIDColumn()
	{
		return 0;
	}
}


