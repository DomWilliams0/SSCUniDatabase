package dxw405.gui.dialog.dialogs;

import dxw405.gui.DBModel;
import dxw405.gui.PersonEntry;
import dxw405.gui.dialog.DialogType;
import dxw405.gui.dialog.UserInput;
import dxw405.gui.dialog.inputfields.InputField;
import dxw405.gui.dialog.inputfields.TextInputField;
import dxw405.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class StudentReportDialog extends BaseDialog
{
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(Utils.DATE_FORMAT);

	private PersonEntry entry;

	public StudentReportDialog(DBModel dbModel, Object... extraArgs)
	{
		super(dbModel, DialogType.REPORT_STUDENT, extraArgs);
		entry = (PersonEntry) extraArgs[0];
		model.populateStudent(entry);
	}

	@Override
	protected JPanel createInterface()
	{
		JPanel panel = new JPanel(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.PAGE_START;

		panel.add(addSectionTitle(getPersonalPanel(), "Personal"), c);
		panel.add(addSectionTitle(getCoursePanel(), "Course"), c);
		panel.add(addSectionTitle(getContactPanel(), "Contact"), c);
		panel.add(addSectionTitle(getNOKPanel(), "Emergency Contact"), c);

		return panel;
	}

	@Override
	protected void validateAndFlag(List<String> errors, UserInput input)
	{

	}

	private JPanel getNOKPanel()
	{
		JPanel panel = new JPanel();

		// name, email, address
		addLabel(panel, "nokName", "Name", entry.nokName);
		addLabel(panel, "nokEmail", "Email", entry.nokEmail);
		addLabel(panel, "nokAddress", "Address", entry.nokAddress);

		return panel;
	}

	private JPanel getContactPanel()
	{
		JPanel panel = new JPanel();

		// email and address
		addLabel(panel, "contactEmail", "Email", entry.email);
		addLabel(panel, "contactAddress", "Address", entry.address);

		return panel;
	}


	private JPanel getCoursePanel()
	{
		JPanel panel = new JPanel();

		// tutor, year, course
		addLabel(panel, "tutorName", "Tutor", entry.tutorName);
		addLabel(panel, "yearOfStudy", "Year Of Study", "Year " + entry.yearOfStudy);
		addLabel(panel, "courseType", "Course Type", entry.courseType);

		return panel;
	}

	private JPanel getPersonalPanel()
	{
		JPanel panel = new JPanel();

		// id, name, dob
		addLabel(panel, "id", "ID", String.valueOf(entry.id));
		addLabel(panel, "name", "Name", entry.getFullName());
		addLabel(panel, "dob", "DOB", DATE_FORMAT.format(entry.dob));

		return panel;
	}

	protected InputField addLabel(JPanel panel, String key, String label, String value)
	{
		return addField(panel, new TextInputField(key, label, false, -1)).setValue(value == null ? "" : value).setEditable(false);
	}


}
