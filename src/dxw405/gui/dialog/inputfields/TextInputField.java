package dxw405.gui.dialog.inputfields;

import dxw405.gui.util.LimitedLengthDocument;

import javax.swing.*;
import java.awt.*;

public class TextInputField extends StringInputField
{
	protected static final Dimension SIZE = new Dimension(180, 20);

	public TextInputField(String key, String labelString, boolean mandatory, int maxLength)
	{
		super(key, labelString, mandatory, new JTextField());
		if (maxLength > 0)
			((JTextField) component).setDocument(new LimitedLengthDocument(maxLength));

		component.setPreferredSize(SIZE);
	}

	@Override
	public String getValue()
	{
		return ((JTextField) component).getText().trim();
	}
}
