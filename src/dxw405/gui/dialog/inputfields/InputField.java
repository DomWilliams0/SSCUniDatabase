package dxw405.gui.dialog.inputfields;

import javax.swing.*;

public abstract class InputField
{
	protected JComponent component;
	private JLabel label;
	private boolean mandatory;
	private String key;

	public InputField(String labelString, boolean mandatory, JComponent component, String key)
	{
		this.key = key;
		this.label = new JLabel(labelString);
		this.label.setLabelFor(component);

		this.mandatory = mandatory;
		this.component = component;
	}

	/**
	 * @return The value of the field
	 */
	public abstract Object getValue();

	/**
	 * @return If the field has a null value, eg. an empty String
	 */
	public abstract boolean hasNoValue();

	/**
	 * @return If the field must have a value
	 */
	public boolean isMandatory()
	{
		return mandatory;
	}

	/**
	 * @return The JComponent field
	 */
	public JComponent getField()
	{
		return component;
	}

	/**
	 * @return The field's label
	 */
	public JLabel getLabel()
	{
		return label;
	}

	/**
	 * @return The field's unique key in user input
	 */

	public String getKey()
	{
		return key;
	}

	public void setEditable(boolean editable)
	{
		component.setEnabled(editable);
	}
}


