package dxw405.gui.dialog;

import dxw405.gui.dialog.inputfields.InputField;

import java.util.Map;
import java.util.TreeMap;

/**
 * A simple holder for an input fields and variables, retrieved by String keys
 */
public class UserInput
{
	private boolean valid;
	private Map<String, InputField> values;
	private Map<String, Object> vars;

	public UserInput()
	{
		valid = false;
		values = new TreeMap<>();
		vars = new TreeMap<>();
	}

	public boolean isValid()
	{
		return valid;
	}

	public void setValid(boolean valid)
	{
		this.valid = valid;
	}

	/**
	 * @param key The key
	 * @return The field if it exists; an IllegalArgumentException will be thrown if it does not
	 */
	public InputField getField(String key)
	{
		InputField field = values.get(key);
		if (field == null)
			throw new IllegalArgumentException("Field not found: " + key);
		return field;
	}

	/**
	 * Gets the value of the field corresponding to the given key
	 *
	 * @param key The key
	 * @param <T> The type to cast to
	 * @return The value if the field exists; an IllegalArgumentException will be thrown if it does not
	 */
	@SuppressWarnings("unchecked")
	public <T> T getValue(String key)
	{
		return (T) getField(key).getValue();
	}

	/**
	 * Maps the given key to the given field
	 */
	public void addField(String key, InputField field)
	{
		values.put(key, field);
	}

	/**
	 * Sets the given flag to the given value
	 */
	public void setVar(String key, Object value) {vars.put(key, value);}

	/**
	 * @param key The flag's key
	 * @return The flag's value if it exists; an IllegalArgumentException will be thrown if it does not
	 */
	@SuppressWarnings("unchecked")
	public <T> T getVar(String key)
	{
		Object var = vars.get(key);
		if (var == null)
			throw new IllegalArgumentException("Flag not found: " + key);
		return (T) var;
	}
}
