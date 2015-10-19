package dxw405.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class DBTableComponent extends JPanel implements Observer
{
	private JTable table;
	private DBModel model;

	public DBTableComponent(DBModel model)
	{
		super(new BorderLayout());
		this.model = model;

		this.table = new StaticTable(new Object[][]{}, new String[]{"ID", "Fullname", "DOB"});

		JScrollPane scrollPane = new JScrollPane(this.table);
		add(scrollPane, BorderLayout.CENTER);
	}

	@Override
	public void update(Observable o, Object arg)
	{
		DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
		tableModel.setRowCount(0);

		List<PersonEntry> entries = model.getTableEntries();

		for (PersonEntry entry : entries)
			tableModel.addRow(new Object[]{entry.id, entry.fullname, entry.dob});

		table.setModel(tableModel);
		tableModel.fireTableDataChanged();
	}

	private class StaticTable extends JTable
	{
		public StaticTable(Object[][] rowData, Object[] columnNames)
		{
			setModel(new DefaultTableModel(rowData, columnNames));
		}

		@Override
		public boolean isCellEditable(int row, int column)
		{
			return false;
		}
	}
}
