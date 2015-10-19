package dxw405.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class DBTableComponent extends JPanel implements Observer
{
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	private JTable table;
	private DBModel model;

	public DBTableComponent(DBModel model)
	{
		super(new BorderLayout());
		this.model = model;

		this.table = new DBTable(new Object[][]{}, model.getTableColumns());

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

		DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
		tableModel.setRowCount(0);

		for (PersonEntry entry : entries)
			tableModel.addRow(model.asRow(entry));

		table.setModel(tableModel);
		tableModel.fireTableDataChanged();
	}

	public static String formatDate(Date date)
	{
		return DATE_FORMAT.format(date);
	}

	/**
	 * A table that doesn't allow editing
	 */
	private class DBTable extends JTable
	{
		public DBTable(Object[][] rowData, Object[] columnNames)
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
