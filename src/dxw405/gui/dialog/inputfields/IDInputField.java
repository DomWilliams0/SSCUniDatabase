package dxw405.gui.dialog.inputfields;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class IDInputField extends InputField
{
	public IDInputField(String key, String labelString, boolean mandatory, int startingID)
	{
		super(labelString, mandatory, new JTextField(), key);

		JTextField textField = (JTextField) component;
		textField.setDocument(new PlainDocument()
		{
			@Override
			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException
			{
				if (getLength() > 10)
					return;

				for (int i = 0; i < str.length(); i++)
					if (!Character.isDigit(str.charAt(i)))
						return;

				super.insertString(offs, str, a);
			}
		});
		textField.setText(String.valueOf(startingID));
		textField.setPreferredSize(TextInputField.SIZE);
	}

	@Override
	public Integer getValue()
	{
		String value = ((JTextField) component).getText().trim();
		if (value.isEmpty())
			return 0;
		return Integer.parseInt(value);
	}

	@Override
	public InputField setValue(Object value)
	{
		((JTextField) component).setText(String.valueOf(value));
		return this;
	}

	@Override
	public boolean hasNoValue()
	{
		return getValue() <= 0;
	}
}
