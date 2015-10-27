package dxw405.gui.dialog.dialogs;

import dxw405.gui.DBModel;
import dxw405.gui.PersonEntry;
import dxw405.gui.dialog.DialogType;
import dxw405.gui.dialog.UserInput;
import dxw405.gui.dialog.inputfields.InputField;
import dxw405.gui.dialog.inputfields.TextInputField;

import javax.swing.*;
import java.util.List;

public abstract class ReportDialog extends BaseDialog
{
	protected PersonEntry entry;

	public ReportDialog(DBModel dbModel, DialogType dialogType, Object... extraArgs)
	{
		super(dbModel, dialogType);
		entry = (PersonEntry) extraArgs[0];
	}

	@Override
	protected void validateAndFlag(List<String> errors, UserInput input)
	{
		// no validation needed
	}

	protected InputField addLabel(JPanel panel, String label, String value, Object constraints)
	{
		return addField(panel, new TextInputField(label, label, false, -1), constraints).setValue(value == null ? "" : value).setEditable(false);

	}

	protected InputField addLabel(JPanel panel, String label, String value)
	{
		return addLabel(panel, label, value, null);
	}
}
