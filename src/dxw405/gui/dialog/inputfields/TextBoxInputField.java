package dxw405.gui.dialog.inputfields;

import dxw405.gui.util.LimitedLengthDocument;

import javax.swing.*;

public class TextBoxInputField extends StringInputField
{
	private JTextArea textArea;

	public TextBoxInputField(String key, String labelString, boolean mandatory, int maxLength)
	{
		super(key, labelString, mandatory, null);
		textArea = new JTextArea(4, 18);

		if (maxLength > 0)
			textArea.setDocument(new LimitedLengthDocument(maxLength));

		component = new JScrollPane(textArea);
	}

	@Override
	public String getValue()
	{
		return textArea.getText().trim();
	}
}
