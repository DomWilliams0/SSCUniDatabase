package dxw405.gui.dialog.inputfields;

import javax.swing.*;
import java.util.Date;

public class DateInputField extends InputField
{
	public DateInputField(String key, String labelString, boolean mandatory, SpinnerDateModel model)
	{
		super(labelString, mandatory, new JSpinner(model), key);
		JSpinner spinner = (JSpinner) component;
		JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "dd/MM/yyyy");
		spinner.setEditor(editor);
	}

	@Override
	public Date getValue()
	{
		return (Date) ((JSpinner) component).getValue();
	}

	@Override
	public boolean hasNoValue()
	{
		return false;
	}
}
