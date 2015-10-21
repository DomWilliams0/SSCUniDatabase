package dxw405.gui.dialog.inputfields;

import dxw405.util.Utils;

import javax.swing.*;
import java.util.Date;

public class DateInputField extends InputField
{
	public DateInputField(String key, String labelString, boolean mandatory, SpinnerDateModel model)
	{
		super(labelString, mandatory, new JSpinner(model), key);
		JSpinner spinner = (JSpinner) component;
		JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, Utils.DATE_FORMAT);
		spinner.setEditor(editor);
	}

	@Override
	public Date getValue()
	{
		return (Date) ((JSpinner) component).getValue();
	}

	@Override
	public InputField setValue(Object value)
	{
		((JSpinner) component).setValue(value);
		return this;
	}

	@Override
	public boolean hasNoValue()
	{
		return false;
	}
}
