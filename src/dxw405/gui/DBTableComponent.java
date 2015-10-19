package dxw405.gui;

import javax.swing.*;
import java.awt.*;

public class DBTableComponent extends JPanel
{
	private DBModel model;

	public DBTableComponent(DBModel model)
	{
		super(new BorderLayout());
		this.model = model;

		JTable table = new JTable(new Object[][]{}, new String[]{"ID", "Fullname", "DOB"});
		JScrollPane scrollPane = new JScrollPane(table);

		add(scrollPane, BorderLayout.CENTER);
	}
}
