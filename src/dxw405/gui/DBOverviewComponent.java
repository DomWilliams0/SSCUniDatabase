package dxw405.gui;

import javax.swing.*;
import java.awt.*;

public class DBOverviewComponent extends JPanel
{
	public DBOverviewComponent(DBModel model)
	{
		super(new BorderLayout());

		// data table
		add(new DBTableComponent(model), BorderLayout.CENTER);

		// control panel
		add(new DBControlPanel(model), BorderLayout.NORTH);
	}


}
