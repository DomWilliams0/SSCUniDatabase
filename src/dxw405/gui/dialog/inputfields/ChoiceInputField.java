package dxw405.gui.dialog.inputfields;

import javax.swing.*;

public class ChoiceInputField extends InputField
{
	public ChoiceInputField(String key, String labelString, boolean mandatory, String[] choices, int initialChoice)
	{
		super(labelString, mandatory, new JComboBox<>(choices), key);

		int choice = choices.length == 0 ? -1 : initialChoice;
		((JComboBox) component).setSelectedIndex(choice);
	}

	@Override
	public Integer getValue()
	{
		return ((JComboBox) component).getSelectedIndex();
	}

	@Override
	public InputField setValue(Object value)
	{
		throw new UnsupportedOperationException("setting choice input value");
	}

	@Override
	public boolean hasNoValue()
	{
		return false;
	}


}
