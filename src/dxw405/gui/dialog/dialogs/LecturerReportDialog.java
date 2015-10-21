package dxw405.gui.dialog.dialogs;

import dxw405.gui.DBModel;
import dxw405.gui.PersonEntry;
import dxw405.gui.dialog.DialogType;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LecturerReportDialog extends ReportDialog
{
	private CardLayout cardLayout;

	public LecturerReportDialog(DBModel dbModel, Object... extraArgs)
	{
		super(dbModel, DialogType.REPORT_LECTURER, extraArgs);
	}

	@Override
	protected JPanel createInterface()
	{
		// todo personal details at top, year combobox, scrollpane of report-like panels for students

		JPanel container = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHEAST;

		// personal details at top
		container.add(getPersonalPanel(), c);

		c.gridy = 1;
		c.gridheight = 2;
		container.add(getTuteePanel(), c);

		return container;
	}

	private JPanel getTuteePanel()
	{
		// gather years
		Map<Integer, List<PersonEntry>> tutees = new HashMap<>();
		gatherTuteeYears(tutees);

		cardLayout = new CardLayout();
		JPanel panel = new JPanel(cardLayout);

		for (Map.Entry<Integer, List<PersonEntry>> tuteeEntry : tutees.entrySet())
			panel.add(createTuteeReports(tuteeEntry.getValue()), String.valueOf(tuteeEntry.getKey()));


		return panel;
	}

	private void gatherTuteeYears(Map<Integer, List<PersonEntry>> tutees)
	{
		Map<Integer, Integer> tutors = model.getTutors();
		for (Map.Entry<Integer, Integer> tutorEntry : tutors.entrySet())
		{
			if (tutorEntry.getValue() != entry.getID())
				continue;

			PersonEntry studentEntry = model.getEntryFromID(tutorEntry.getKey());
			if (studentEntry == null)
				continue;

			List<PersonEntry> entries = tutees.get(studentEntry.getYearOfStudy());
			if (entries == null)
			{
				entries = new ArrayList<>();
				entries.add(studentEntry);
				tutees.put(studentEntry.getYearOfStudy(), entries);
			} else
				entries.add(studentEntry);
		}
	}

	private JPanel createTuteeReports(List<PersonEntry> tutees)
	{
		JPanel parent = new JPanel();
		parent.setLayout(new BoxLayout(parent, BoxLayout.Y_AXIS));

		// id
		// name
		// dob
		// email
		// address
		// nok name
		// nok email
		// nok

		for (PersonEntry tutee : tutees)
		{
			JPanel tuteeReport = new JPanel(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();

			addLabel(tuteeReport, "ID", tutee.getIDString());

			parent.add(tuteeReport);
		}

		return parent;
	}

	private JPanel getPersonalPanel()
	{
		JPanel panel = new JPanel();

		addLabel(panel, "ID", entry.getIDString());
		addLabel(panel, "Name", entry.getFullName());
		addLabel(panel, "Office", entry.getOffice());

		return panel;
	}


}
