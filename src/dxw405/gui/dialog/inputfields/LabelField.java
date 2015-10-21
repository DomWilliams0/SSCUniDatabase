package dxw405.gui.dialog.inputfields;

import javax.swing.*;

public class LabelField extends StringInputField
{

	public LabelField(String key, String s)
	{
		super(key, s, false, new JLabel());
		component.setPreferredSize(TextInputField.SIZE);
	}

	@Override
	public Object getValue()
	{
		return ((JLabel) component).getText();
	}

	@Override
	public InputField setValue(Object value)
	{
		((JLabel) component).setText(String.valueOf(value));
		return this;
	}
}