package dxw405.gui;

import dxw405.gui.util.LimitedLengthDocument;
import dxw405.util.Utils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddStudentDialog extends JDialog
{
	private JPanel dialogContent;
	private DBModel model;

	private JTextComponent forename;
	private JTextComponent surname;
	private JTextComponent email;
	private JTextComponent address;
	private JTextComponent nokName;
	private JTextComponent nokEmail;
	private JTextComponent nokAddress;

	private JSpinner studentID;
	private JComboBox yearOfStudy;
	private JComboBox courseTypeID;
	private JComboBox titleID;
	private JSpinner dob;

	private AddStudentDialog(DBModel model)
	{
		this.model = model;
		initDialog();
	}

	private void initDialog()
	{
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

	public static AddStudentInput showPopup(DBModel model)
	{
		return new AddStudentDialog(model).display();
	}

	/**
	 * Displays the dialog
	 *
	 * @return True if valid information has been submitted, false if invalid information or cancel/close was pressed
	 */
	private AddStudentInput display()
	{
		// todo reset all fields?

		// create and show dialog
		JOptionPane pane = new JOptionPane(dialogContent, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
		setTitle("Add Student");
		setContentPane(pane);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setModal(true);
		pack();
		setLocationRelativeTo(null);

		AddStudentInput input = new AddStudentInput();

		pane.addPropertyChangeListener(e -> {
			String prop = e.getPropertyName();
			if (!isVisible() || (e.getSource() != pane) || (!JOptionPane.VALUE_PROPERTY.equals(prop) && !JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) return;

			Object value = pane.getValue();
			if (value == JOptionPane.UNINITIALIZED_VALUE) return;

			// allow repeated clicking
			pane.setValue(JOptionPane.UNINITIALIZED_VALUE);

			// ok: validate
			if ((int) value == JOptionPane.OK_OPTION)
			{
				fillInput(input);

				// all good
				if (validateInput(input)) dispose();
			}

			// cancel/close: dispose
			else
			{
				input.isValid = false;
				dispose();
			}
		});

		// display
		setVisible(true);

		return input.isValid ? input : null;
	}

	/**
	 * Validates the given input, and opens a dialog box warning of the invalid fields if invalid
	 *
	 * @param input The input to validate
	 * @return True if validated, false otherwise
	 */
	private boolean validateInput(AddStudentInput input)
	{
		java.util.List<String> errors = new ArrayList<>();

		// mandatory fields
		if (input.forename.isEmpty()) errors.add("Forename cannot be empty");
		if (input.surname.isEmpty()) errors.add("Surname cannot be emptry");
		if (input.studentID <= 0) errors.add("Student ID cannot be 0");

		// todo emails format

		// set flags
		input.hasContact = !input.email.isEmpty() && !input.address.isEmpty();
		input.hasCourseDetails = input.yearOfStudy > 0 && input.courseTypeID >= 0;
		input.hasNOK = !input.nokName.isEmpty() && !input.nokEmail.isEmpty() && !input.nokAddress.isEmpty();

		boolean success = errors.isEmpty();

		// popup
		if (!success)
		{
			String delimiter = "\n -";
			String errorMessage = "Please fix the following issue(s):" + delimiter + String.join(delimiter, errors);
			JOptionPane.showMessageDialog(this, errorMessage, "Uh oh", JOptionPane.ERROR_MESSAGE);
		}

		return success;

	}

	/**
	 * Fills the given input with the contents of all fields
	 *
	 * @param input The input to fill
	 */
	private void fillInput(AddStudentInput input)
	{
		// todo abstract all fields to allow mass getText/value, and specify if mandatory
		input.forename = Utils.capitalise(forename.getText().trim());
		input.surname = Utils.capitalise(surname.getText().trim());
		input.email = email.getText().trim();
		input.address = address.getText().trim();
		input.nokName = nokName.getText().trim(); // todo capitalise every word
		input.nokEmail = nokEmail.getText().trim();
		input.nokAddress = nokAddress.getText().trim();
		input.studentID = (int) studentID.getValue();
		input.yearOfStudy = yearOfStudy.getSelectedIndex() + 1;
		input.courseTypeID = courseTypeID.getSelectedIndex() + 1;
		input.titleID = titleID.getSelectedIndex() + 1;
		input.dob = (Date) dob.getValue();
		input.isValid = true;
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
		titleID = (JComboBox) addChoiceInput("Title", model.getTitles(), 0, panel);
		forename = (JTextComponent) addTextInput("Forename", 32, panel);
		surname = (JTextComponent) addTextInput("Surname", 32, panel);

		// dob
		Calendar cal = Calendar.getInstance();
		cal.set(1900, Calendar.JANUARY, 1);
		Date minDate = cal.getTime();
		Date maxDate = new Date();

		// dob
		dob = (JSpinner) addDateInput("DOB", minDate, maxDate, panel);

		return panel;
	}

	/**
	 * @return A panel with student ID, email and address input fields
	 */
	private JPanel getContactsPanel()
	{
		JPanel panel = new JPanel();

		// id
		studentID = (JSpinner) addNumberInput("Student ID", 0, Integer.MAX_VALUE, panel);

		// email
		email = (JTextComponent) addTextInput("Email", 320, panel);

		// postal
		address = (JTextComponent) addTextArea("Address", 512, panel);

		return panel;
	}


	/**
	 * @return A panel with year of study and course type input fields
	 */
	private JPanel getCouseDetailsPanel()
	{
		JPanel panel = new JPanel();

		// year of study
		yearOfStudy = (JComboBox) addChoiceInput("Year of Study", range(1, 6), 0, panel);

		// registration type
		courseTypeID = (JComboBox) addChoiceInput("Course Type", model.getRegistrationTypes(), 0, panel);

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
		nokName = (JTextComponent) addTextInput("Name", 64, panel);

		// email
		nokEmail = (JTextComponent) addTextInput("Email", 320, panel);

		// postal
		nokAddress = (JTextComponent) addTextArea("Address", 512, panel);

		return panel;
	}

	private JComponent addChoiceInput(String labelString, String[] choices, int initialChoice, JPanel parent)
	{
		JComboBox<String> comboBox = new JComboBox<>(choices);
		comboBox.setSelectedIndex(initialChoice);

		return addInput(labelString, comboBox, parent);
	}

	private JComponent addDateInput(String labelString, Date minDate, Date maxDate, JPanel parent)
	{
		JSpinner spinner = new JSpinner(new SpinnerDateModel(maxDate, minDate, maxDate, Calendar.DAY_OF_MONTH));
		JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "dd/MM/yyyy");
		spinner.setEditor(editor);

		return addInput(labelString, spinner, parent);
	}

	private JComponent addNumberInput(String labelString, int min, int max, JPanel parent)
	{
		JSpinner spinner = new JSpinner(new SpinnerNumberModel(min, min, max, 1));

		JSpinner.NumberEditor editor = (JSpinner.NumberEditor) spinner.getEditor();
		((NumberFormatter) editor.getTextField().getFormatter()).setAllowsInvalid(false);
		editor.getFormat().setGroupingUsed(false);

		return addInput(labelString, spinner, parent);

	}

	private JComponent addTextInput(String labelString, int maxLength, JPanel parent)
	{
		return addTextComponent(labelString, maxLength, false, parent);
	}

	private JComponent addTextArea(String labelString, int maxLength, JPanel parent)
	{
		return addTextComponent(labelString, maxLength, true, parent);
	}

	private JComponent addTextComponent(String labelString, int maxLength, boolean textBox, JPanel parent)
	{
		JTextComponent text = textBox ? new JTextArea(4, 18) : new JTextField();
		if (maxLength > 0) text.setDocument(new LimitedLengthDocument(maxLength));

		if (textBox)
		{
			JScrollPane scrollPane = new JScrollPane(text);
			addInput(labelString, scrollPane, parent);
			return text;
		} else
		{
			text.setPreferredSize(new Dimension(180, 20));
			return addInput(labelString, text, parent);
		}

	}

	private JComponent addInput(String labelString, JComponent component, JPanel parent)
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

		return component;
	}

}

class AddStudentInput
{
	public String forename, surname, email, address, nokName, nokEmail, nokAddress;
	public int studentID, yearOfStudy, courseTypeID, titleID;
	public Date dob;

	public boolean hasContact, hasNOK, hasCourseDetails, isValid;

	@Override
	public String toString()
	{
		return "AddStudentInput{" +
				"forename='" + forename + '\'' +
				", surname='" + surname + '\'' +
				", email='" + email + '\'' +
				", address='" + address + '\'' +
				", nokName='" + nokName + '\'' +
				", nokEmail='" + nokEmail + '\'' +
				", nokAddress='" + nokAddress + '\'' +
				", studentID=" + studentID +
				", yearOfStudy=" + yearOfStudy +
				", courseTypeID=" + courseTypeID +
				", titleID=" + titleID +
				", dob=" + dob +
				", hasContact=" + hasContact +
				", hasNOK=" + hasNOK +
				", hasCourseDetails=" + hasCourseDetails +
				", isValid=" + isValid +
				'}';
	}
}
