package dxw405.gui.dialog.dialogs;

import dxw405.gui.DBModel;
import dxw405.gui.PersonEntry;
import dxw405.gui.dialog.DialogType;
import dxw405.gui.dialog.inputfields.ChoiceInputField;
import dxw405.util.PersonType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LecturerReportDialog extends ReportDialog implements ActionListener
{
	private JPanel cardPanel;
	private CardLayout cardLayout;
	private Map<Integer, List<PersonEntry>> tutees;

	public LecturerReportDialog(DBModel dbModel, Object... extraArgs)
	{
		super(dbModel, DialogType.REPORT_LECTURER, extraArgs);
		gatherTuteeYears();
	}

	@Override
	protected JPanel createInterface()
	{
		JPanel container = new JPanel(new BorderLayout());

		container.add(getHeaderPanel(), BorderLayout.PAGE_START);
		container.add(getTuteePanel(), BorderLayout.CENTER);

		return container;
	}

	private JPanel getTuteePanel()
	{
		cardLayout = new CardLayout();
		cardPanel = new JPanel(cardLayout);

		for (Map.Entry<Integer, List<PersonEntry>> tuteeEntry : tutees.entrySet())
			cardPanel.add(createTuteeReports(tuteeEntry.getValue()), String.valueOf(tuteeEntry.getKey()));

		return cardPanel;
	}

	private void gatherTuteeYears()
	{
		tutees = new HashMap<>();

		Map<Integer, Integer> tutors = model.getTutors();
		for (Map.Entry<Integer, Integer> tutorEntry : tutors.entrySet())
		{
			if (tutorEntry.getValue() != entry.getID())
				continue;

			PersonEntry studentEntry = model.getEntry(PersonType.STUDENT, tutorEntry.getKey());
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
		JPanel parent = new JPanel(new BorderLayout());

		JPanel scrollPanel = new JPanel();
		scrollPanel.setLayout(new BoxLayout(scrollPanel, BoxLayout.Y_AXIS));

		for (int i = 0, tuteesSize = tutees.size(); i < tuteesSize; i++)
		{
			PersonEntry tutee = tutees.get(i);
			JPanel tuteeReport = new JPanel(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(2, 2, 2, 2);
			c.fill = GridBagConstraints.HORIZONTAL;

			c.gridy = 0;
			addLabel(tuteeReport, "Name", tutee.getFullName(), c);
			addLabel(tuteeReport, "ID", tutee.getIDString(), c);
			c.gridy++;
			addLabel(tuteeReport, "DOB", tutee.getDOBFormatted(), c);
			addLabel(tuteeReport, "Address", tutee.getAddress(), c);

			c.gridy++;
			addLabel(tuteeReport, "Year", tutee.getYearOfStudy().toString(), c);
			addLabel(tuteeReport, "Course Type", tutee.getCourseType(), c);

			c.gridy++;
			addLabel(tuteeReport, "Email", tutee.getEmail(), c);
			addLabel(tuteeReport, "Emergency Contact", tutee.getNOKName(), c);

			c.gridy++;
			addLabel(tuteeReport, "Emergency Email", tutee.getNOKEmail(), c);
			addLabel(tuteeReport, "Emergency Address", tutee.getNOKAddress(), c);

			addSectionTitle(tuteeReport, Integer.toString(i + 1));

			scrollPanel.add(tuteeReport);

			if (i != tuteesSize - 1)
				scrollPanel.add(Box.createVerticalStrut(5));
		}

		JScrollPane scrollPane = new JScrollPane(scrollPanel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		scrollPane.setPreferredSize(new Dimension(800, 400));
		parent.add(scrollPane, BorderLayout.CENTER);

		return parent;
	}

	private JPanel getHeaderPanel()
	{
		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

		top.add(getPersonalPanel());
		top.add(getYearChoicePanel());

		return top;
	}

	private JPanel getPersonalPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		addLabel(panel, "ID", entry.getIDString());
		addLabel(panel, "Name", entry.getFullName());
		addLabel(panel, "Office", entry.getOffice());

		return addSectionTitle(panel, "Tutor");
	}

	private JPanel getYearChoicePanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		// gather year choices
		Object[] intChoices = tutees.keySet().toArray();
		String[] yearChoices = new String[intChoices.length];
		for (int i = 0; i < intChoices.length; i++)
			yearChoices[i] = intChoices[i].toString();

		JComboBox choice = (JComboBox) addField(panel, new ChoiceInputField("yos", "Year Of Study", false, yearChoices, 0)).getField();
		choice.addActionListener(this);


		return panel;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Integer year = Integer.parseInt((String) ((JComboBox) e.getSource()).getSelectedItem());
		cardLayout.show(cardPanel, year.toString());
	}
}
