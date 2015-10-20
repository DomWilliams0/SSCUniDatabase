package dxw405.gui.dialog.inputfields;

import javax.swing.*;

public abstract class StringInputField extends InputField
{
	public StringInputField(String key, String labelString, boolean mandatory, JComponent component)
	{
		super(labelString, mandatory, component, key);
	}

	@Override
	public boolean hasNoValue()
	{
		return ((String) getValue()).isEmpty();
	}
}
