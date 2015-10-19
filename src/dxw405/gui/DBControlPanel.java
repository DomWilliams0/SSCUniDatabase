package dxw405.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

public class DBControlPanel extends JPanel implements ActionListener
{
	public DBControlPanel(DBModel model)
	{
		super(new BorderLayout());

		// visibility
		JPanel visibilityPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		ButtonGroup group = new ButtonGroup();
		group.add(new JRadioButton("Students"));
		group.add(new JRadioButton("Lecturers"));
		group.add(new JRadioButton("Both", true));

		Enumeration<AbstractButton> elements = group.getElements();
		while (elements.hasMoreElements())
		{
			AbstractButton button = elements.nextElement();
			button.addActionListener(this);
			visibilityPanel.add(button);
		}

		// manipulation
		JPanel manipulationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton addStudent = new JButton("Add Student");
		manipulationPanel.add(addStudent);

		add(visibilityPanel, BorderLayout.EAST);
		add(manipulationPanel, BorderLayout.WEST);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{

	}
}
