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
		addLabel(panel, "Name", entry.nokName);
		addLabel(panel, "Email", entry.nokEmail);
		addLabel(panel, "Address", entry.nokAddress);

		return panel;
	}

	private JPanel getContactPanel()
	{
		JPanel panel = new JPanel();

		// email and address
		addLabel(panel, "Email", entry.email);
		addLabel(panel, "Address", entry.address);

		return panel;
	}


	private JPanel getCoursePanel()
	{
		JPanel panel = new JPanel();

		// tutor, year, course
		addLabel(panel, "Tutor", entry.tutorName);
		addLabel(panel, "Year Of Study", "Year " + entry.yearOfStudy);
		addLabel(panel, "Course Type", entry.courseType);

		return panel;
	}

	private JPanel getPersonalPanel()
	{
		JPanel panel = new JPanel();

		// id, name, dob
		addLabel(panel, "ID", String.valueOf(entry.id));
		addLabel(panel, "Name", entry.getFullName());
		addLabel(panel, "DOB", DATE_FORMAT.format(entry.dob));

		return panel;
	}


}
