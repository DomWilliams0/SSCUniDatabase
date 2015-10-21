package dxw405.gui.dialog.dialogs;

import dxw405.gui.DBModel;
import dxw405.gui.PersonEntry;
import dxw405.gui.dialog.DialogType;
import dxw405.gui.dialog.UserInput;
import dxw405.gui.dialog.inputfields.InputField;
import dxw405.gui.dialog.inputfields.TextInputField;
import dxw405.util.Utils;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.List;

public abstract class ReportDialog extends BaseDialog
{
	protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(Utils.DATE_FORMAT);

	protected PersonEntry entry;

	public ReportDialog(DBModel dbModel, DialogType dialogType, Object... extraArgs)
	{
		super(dbModel, dialogType);
		entry = (PersonEntry) extraArgs[0];
		model.populateStudent(entry);
	}

	@Override
	protected void validateAndFlag(List<String> errors, UserInput input)
	{
		// no validation needed
	}

	protected InputField addLabel(JPanel panel, String label, String value)
	{
		return addField(panel, new TextInputField(label, label, false, -1)).setValue(value == null ? "" : value).setEditable(false);
	}
}
