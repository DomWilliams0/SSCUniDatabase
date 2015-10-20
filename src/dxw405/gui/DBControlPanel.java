package dxw405.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class DBControlPanel extends JPanel
{
	public DBControlPanel(ActionListener buttonListener)
	{
		super(new BorderLayout());

		// visibility
		JPanel visibilityPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		ButtonGroup group = new ButtonGroup();
		addButton("Students", Action.VISIBLE_STUDENTS, false, group, buttonListener, visibilityPanel);
		addButton("Lecturers", Action.VISIBLE_LECTURERS, false, group, buttonListener, visibilityPanel);
		addButton("Both", Action.VISIBLE_ALL, true, group, buttonListener, visibilityPanel);

		// manipulation
		JPanel manipulationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton addStudent = new JButton("Add Student");
		addStudent.setActionCommand(Action.ADD_STUDENT.toString());
		addStudent.addActionListener(buttonListener);
		manipulationPanel.add(addStudent);

		add(visibilityPanel, BorderLayout.EAST);
		add(manipulationPanel, BorderLayout.WEST);
	}

	/**
	 * Helper to add a radio button with the given properties
	 *
	 * @param text           Button text
	 * @param command        Action for this button
	 * @param selected       Initially selected or not
	 * @param group          ButtonGroup to add to
	 * @param buttonListener Listener for button presses
	 * @param panel          Panel to add button to
	 */
	private void addButton(String text, Action command, boolean selected, ButtonGroup group, ActionListener buttonListener, JPanel panel)
	{
		JRadioButton button = new JRadioButton(text, selected);
		button.setActionCommand(command.toString());
		button.addActionListener(buttonListener);
		panel.add(button);
		group.add(button);
	}

}
