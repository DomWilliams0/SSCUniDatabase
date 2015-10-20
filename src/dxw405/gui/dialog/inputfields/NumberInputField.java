package dxw405.gui.dialog.inputfields;

import javax.swing.*;
import javax.swing.text.NumberFormatter;

public class NumberInputField extends InputField
{
	public NumberInputField(String key, String labelString, boolean mandatory, SpinnerNumberModel model)
	{
		super(labelString, mandatory, new JSpinner(model), key);
		JSpinner.NumberEditor editor = (JSpinner.NumberEditor) ((JSpinner) component).getEditor();
		((NumberFormatter) editor.getTextField().getFormatter()).setAllowsInvalid(false);
		editor.getFormat().setGroupingUsed(false);
	}

	@Override
	public Object getValue()
	{
		return ((JSpinner) component).getValue();
	}

	@Override
	public boolean hasNoValue()
	{
		return false;
	}


}
