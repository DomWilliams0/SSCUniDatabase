package dxw405.gui;

import dxw405.gui.util.LimitedLengthDocument;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;

public class AddStudentDialog extends JDialog
{
	private final JPanel dialogContent;
	private DBModel model;

	private String forename, surname, email, address, nokName, nokEmail, nokAddress;
	private int studentID, yearOfStudy, courseTypeID, titleID;
	private Date dob;

	public AddStudentDialog(DBModel model)
	{
		this.model = model;
		reset();

		dialogContent = new JPanel(new GridBagLayout());

		JPanel personalDetails = addSubsection(getDetailsPanel(), "Personal");
		JPanel contactDetails = addSubsection(getContactsPanel(), "Contact");
		JPanel courseDetails = addSubsection(getCouseDetailsPanel(), "Course");
		JPanel nextOfKin = addSubsection(getNextOfKinPanel(), "Emergency Contact");

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.PAGE_START;

		c.gridx = 0;
		c.gridy = 0;
		dialogContent.add(personalDetails, c);

		c.gridx = 0;
		c.gridy = 1;
		dialogContent.add(contactDetails, c);

		c.gridx = 1;
		c.gridy = 0;
		dialogContent.add(courseDetails, c);

		c.gridx = 1;
		c.gridy = 1;
		dialogContent.add(nextOfKin, c);
	}

	/**
	 * Displays the dialog
	 *
	 * @return True if valid information has been submitted, false if invalid information or cancel/close was pressed
	 */
	public boolean display()
	{
		// reset all fields
		reset();

		// create and show dialog
		JOptionPane pane = new JOptionPane(dialogContent, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
		setTitle("Add Student");
		setContentPane(pane);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		pack();
		setModal(true);
		setLocationRelativeTo(null);
		setVisible(true);
		dispose();


		// todo listeners for closing/cancel and clicking ok (somehow)

		// cancel
		//		if (choice != JOptionPane.OK_OPTION)
		//			return false;

		return true;
	}

	private void reset()
	{
		forename = surname = email = address = nokName = nokEmail = nokAddress = null;
		dob = null;
		studentID = yearOfStudy = courseTypeID = titleID = -1;
	}


	/**
	 * Borders the given panel with the given title
	 *
	 * @param panel The panel
	 * @param title The title
	 * @return The same panel, but with a border
	 */
	private JPanel addSubsection(JPanel panel, String title)
	{
		TitledBorder border = new TitledBorder(title);
		border.setTitlePosition(TitledBorder.TOP);
		panel.setBorder(border);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		return panel;
	}

	/**
	 * @return A panel with title, name and DOB input fields
	 */
	private JPanel getDetailsPanel()
	{
		JPanel panel = new JPanel();

		// name
		addChoiceInput("Title", model.getTitles(), 0, panel);
		addTextInput("Forename", 32, panel);
		addTextInput("Surname", 32, panel);

		// dob
		Calendar cal = Calendar.getInstance();
		cal.set(1900, Calendar.JANUARY, 1);
		Date minDate = cal.getTime();
		Date maxDate = new Date();

		// dob
		addDateInput("DOB", minDate, maxDate, panel);

		return panel;
	}

	/**
	 * @return A panel with student ID, email and address input fields
	 */
	private JPanel getContactsPanel()
	{
		JPanel panel = new JPanel();

		// id
		addNumberInput("Student ID", 0, Integer.MAX_VALUE, panel);

		// email
		addTextInput("Email", 320, panel);

		// postal
		addTextArea("Address", 512, panel);

		return panel;
	}


	/**
	 * @return A panel with year of study and course type input fields
	 */
	private JPanel getCouseDetailsPanel()
	{
		JPanel panel = new JPanel();

		// year of study
		addChoiceInput("Year of Study", range(1, 6), 0, panel);

		// registration type
		addChoiceInput("Course Type", model.getRegistrationTypes(), 0, panel);

		return panel;
	}

	/**
	 * Helper function to create an array of ints as Strings
	 *
	 * @param min Start
	 * @param max End (exclusive)
	 * @return Array of the String values of the given range
	 */
	private String[] range(int min, int max)
	{
		String[] s = new String[max - min];
		for (int i = min; i < max; i++)
			s[i - min] = String.valueOf(i);

		return s;
	}

	/**
	 * @return A panel with emergency contact name, email and address fields
	 */
	private JPanel getNextOfKinPanel()
	{
		JPanel panel = new JPanel();

		// next of kin
		addTextInput("Name", 64, panel);

		// email
		addTextInput("Email", 320, panel);

		// postal
		addTextArea("Address", 512, panel);

		return panel;
	}

	private void addChoiceInput(String labelString, String[] choices, int initialChoice, JPanel parent)
	{
		JComboBox<String> comboBox = new JComboBox<>(choices);
		comboBox.setSelectedIndex(initialChoice);

		addInput(labelString, comboBox, parent);
	}

	private void addDateInput(String labelString, Date minDate, Date maxDate, JPanel parent)
	{
		JSpinner spinner = new JSpinner(new SpinnerDateModel(maxDate, minDate, maxDate, Calendar.DAY_OF_MONTH));
		JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "dd/MM/yyyy");
		spinner.setEditor(editor);

		addInput(labelString, spinner, parent);
	}

	private void addNumberInput(String labelString, int min, int max, JPanel parent)
	{
		JSpinner spinner = new JSpinner(new SpinnerNumberModel(min, min, max, 1));

		JSpinner.NumberEditor editor = (JSpinner.NumberEditor) spinner.getEditor();
		((NumberFormatter) editor.getTextField().getFormatter()).setAllowsInvalid(false);
		editor.getFormat().setGroupingUsed(false);

		addInput(labelString, spinner, parent);

	}

	private void addTextInput(String labelString, int maxLength, JPanel parent)
	{
		addTextComponent(labelString, maxLength, false, parent);
	}

	private void addTextArea(String labelString, int maxLength, JPanel parent)
	{
		addTextComponent(labelString, maxLength, true, parent);
	}

	private void addTextComponent(String labelString, int maxLength, boolean textBox, JPanel parent)
	{
		JTextComponent text = textBox ? new JTextArea(4, 18) : new JTextField();
		if (maxLength > 0)
			text.setDocument(new LimitedLengthDocument(maxLength));

		if (textBox)
		{
			JScrollPane scrollPane = new JScrollPane(text);
			addInput(labelString, scrollPane, parent);
		} else
		{
			text.setPreferredSize(new Dimension(180, 20));
			addInput(labelString, text, parent);
		}

	}

	private void addInput(String labelString, Component component, JPanel parent)
	{
		JLabel label = new JLabel(labelString);
		label.setLabelFor(component);

		// centre the label
		Box box = Box.createHorizontalBox();
		box.add(label);
		box.add(Box.createHorizontalBox());
		parent.add(box);

		parent.add(component);

		parent.add(Box.createVerticalStrut(4));
	}

}

