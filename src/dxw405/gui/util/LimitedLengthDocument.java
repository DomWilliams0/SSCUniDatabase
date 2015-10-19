package dxw405.gui.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class LimitedLengthDocument extends PlainDocument
{
	private int limit;

	public LimitedLengthDocument(int limit)
	{
		this.limit = limit;
	}

	@Override
	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException
	{
		if (str != null && getLength() + str.length() <= limit)
			super.insertString(offs, str, a);
	}
}
