package dxw405.gui.dialog.dialogs;

import dxw405.gui.DBModel;
import dxw405.gui.dialog.DialogType;
import dxw405.gui.dialog.UserInput;
import dxw405.gui.dialog.inputfields.*;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class AddStudentDialog extends BaseDialog
{
	// thank you stackoverflow http://stackoverflow.com/a/719543
	private static Pattern EMAIL_REGEX = Pattern.compile("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$");

	public AddStudentDialog(DBModel dbModel, Object... extraArgs)
	{
		super(dbModel, DialogType.ADD_STUDENT, extraArgs);
	}

	@Override
	protected JPanel createInterface()
	{
		JPanel panel = new JPanel(new GridBagLayout());

		JPanel personalDetails = addSectionTitle(getDetailsPanel(), "Personal");
		JPanel contactDetails = addSectionTitle(getContactsPanel(), "Contact");
		JPanel courseDetails = addSectionTitle(getCourseDetailsPanel(), "Course");
		JPanel nextOfKin = addSectionTitle(getNextOfKinPanel(), "Emergency Contact");

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.PAGE_START;

		c.gridx = 0;
		c.gridy = 0;
		panel.add(personalDetails, c);

		c.gridx = 0;
		c.gridy = 1;
		panel.add(contactDetails, c);

		c.gridx = 1;
		c.gridy = 0;
		panel.add(courseDetails, c);

		c.gridx = 1;
		c.gridy = 1;
		panel.add(nextOfKin, c);

		return panel;
	}

	@Override
	protected void validateAndFlag(List<String> errors, UserInput i)
	{
		// student id already exists
		if (model.personExists(i.getValue("studentID")))
			errors.add("A student already exists with that ID");

		// flags
		i.setVar("hasContact", !i.getField("contactEmail").hasNoValue() ||
				!i.getField("contactAddress").hasNoValue());
		i.setVar("hasNOK", !i.getField("nokName").hasNoValue() ||
				!i.getField("nokEmail").hasNoValue() ||
				!i.getField("nokAddress").hasNoValue());

		// anomalies
		int yearOfStudy = i.<Integer>getValue("yearOfStudy") + 1;
		int courseType = i.getValue("courseTypeID");
		if (yearOfStudy < 1 || yearOfStudy > 5) errors.add("Year of study is out of range");
		if (courseType < 0 || courseType >= model.getRegistrationTypes().length) errors.add("Course type is invalid");

		// emails
		if (!isValidEmail(i.getField("contactEmail")))
			errors.add("Contact email is not an email address");
		if (!isValidEmail(i.getField("nokEmail")))
			errors.add("Emergency email is not an email address");
	}

	private boolean isValidEmail(InputField emailField)
	{
		return emailField.hasNoValue() || EMAIL_REGEX.matcher((CharSequence) emailField.getValue()).matches();
	}

	/**
	 * @return A panel with title, name and DOB input fields
	 */
	private JPanel getDetailsPanel()
	{
		JPanel panel = new JPanel();

		// name
		addField(panel, new ChoiceInputField("titleID", "Title", true, model.getTitles(), 0));
		addField(panel, new TextInputField("forename", "Forename", true, 32, true));
		addField(panel, new TextInputField("surname", "Surname", true, 32, true));

		// dob
		Calendar cal = Calendar.getInstance();
		cal.set(1900, Calendar.JANUARY, 1);
		Date minDate = cal.getTime();
		Date maxDate = new Date();

		addField(panel, new DateInputField("dob", "DOB", false, new SpinnerDateModel(minDate, minDate, maxDate, Calendar.DAY_OF_MONTH)));

		return panel;
	}

	/**
	 * @return A panel with student ID, email and address input fields
	 */
	private JPanel getContactsPanel()
	{
		JPanel panel = new JPanel();

		addField(panel, new IDInputField("studentID", "Student ID", true, 0));
		addField(panel, new TextInputField("contactEmail", "Email", false, 320));
		addField(panel, new TextBoxInputField("contactAddress", "Address", false, 512));

		return panel;
	}


	/**
	 * @return A panel with year of study and course type input fields
	 */
	private JPanel getCourseDetailsPanel()
	{
		JPanel panel = new JPanel();

		addField(panel, new ChoiceInputField("yearOfStudy", "Year of Study", false, range(1, 6), 0));
		addField(panel, new ChoiceInputField("courseTypeID", "Course Type", false, model.getRegistrationTypes(), 0));

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

		addField(panel, new TextInputField("nokName", "Name", false, 64));
		addField(panel, new TextInputField("nokEmail", "Email", false, 320));
		addField(panel, new TextBoxInputField("nokAddress", "Address", false, 512));

		return panel;
	}
}
