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
	}

	@Override
	protected JPanel createInterface()
	{
		JPanel panel = new JPanel(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.PAGE_START;

		// personal
		panel.add(addSectionTitle(getPersonalPanel(), "Personal"), c);

		return panel;

	}


	@Override
	protected void validateAndFlag(List<String> errors, UserInput input)
	{

	}

	private JPanel getPersonalPanel()
	{
		JPanel panel = new JPanel();

		// id
		addLabel(panel, "id", "ID", String.valueOf(entry.id));

		// name
		addLabel(panel, "name", "Name", entry.getFullName());

		// dob
		addLabel(panel, "dob", "DOB", DATE_FORMAT.format(entry.dob));


		return panel;
	}

	protected InputField addLabel(JPanel panel, String key, String label, String value)
	{
		return addField(panel, new TextInputField(key, label, false, -1)).setValue(value).setEditable(false);
	}

}
