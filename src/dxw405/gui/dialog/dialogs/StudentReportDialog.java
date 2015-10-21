package dxw405.gui.dialog.dialogs;

import dxw405.gui.DBModel;
import dxw405.gui.dialog.DialogType;

import javax.swing.*;
import java.awt.*;

public class StudentReportDialog extends ReportDialog
{
	public StudentReportDialog(DBModel dbModel, Object... extraArgs)
	{
		super(dbModel, DialogType.REPORT_STUDENT, extraArgs);
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


}
