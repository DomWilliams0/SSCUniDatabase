package dxw405.gui.dialog.inputfields;

import dxw405.gui.util.LimitedLengthDocument;
import dxw405.util.Utils;

import javax.swing.*;
import java.awt.*;

public class TextInputField extends StringInputField
{
	protected static final Dimension SIZE = new Dimension(180, 20);
	private boolean capitalise;

	public TextInputField(String key, String labelString, boolean mandatory, int maxLength)
	{
		this(key, labelString, mandatory, maxLength, false);
	}
	public TextInputField(String key, String labelString, boolean mandatory, int maxLength, boolean autoCapitalise)
	{
		super(key, labelString, mandatory, new JTextField());
		capitalise = autoCapitalise;
		if (maxLength > 0)
			((JTextField) component).setDocument(new LimitedLengthDocument(maxLength));

		component.setPreferredSize(SIZE);
	}

	@Override
	public String getValue()
	{
		String value = ((JTextField) component).getText().trim();
		return capitalise ? Utils.capitalise(value) : value;
	}

	@Override
	public InputField setValue(Object value)
	{
		((JTextField) component).setText(String.valueOf(value));
		return this;
	}
}
