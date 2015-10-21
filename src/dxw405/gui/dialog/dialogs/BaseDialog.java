package dxw405.gui.dialog.dialogs;

import dxw405.gui.DBModel;
import dxw405.gui.dialog.DialogType;
import dxw405.gui.dialog.UserInput;
import dxw405.gui.dialog.inputfields.InputField;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDialog extends JDialog
{
	protected DBModel model;
	private DialogType type;
	private List<InputField> inputFields;

	public BaseDialog(DBModel dbModel, DialogType dialogType, Object... extraArgs)
	{
		model = dbModel;
		type = dialogType;
		inputFields = new ArrayList<>();
		if (extraArgs.length > 0)
			parseExtraArgs(extraArgs);
	}

	/**
	 * Shows a dialog with the given type, and blocks until it is closed
	 *
	 * @param dialogType The type of dialog box to open
	 * @param model      The database model
	 * @param extraArgs  Any extra arguments to pass to the dialog box
	 * @return The user's input
	 */
	public static UserInput showDialog(DialogType dialogType, DBModel model, Object... extraArgs)
	{
		Class<? extends BaseDialog> dialogClass = dialogType.getDialogClass();
		if (dialogClass == null)
			throw new UnsupportedOperationException("Dialog not implemented");

		try
		{
			Constructor<?> constructor = dialogClass.getConstructor(DBModel.class, Object[].class);
			BaseDialog dialog = (BaseDialog) constructor.newInstance(model, extraArgs);
			return dialog.display();

		} catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e)
		{
			model.logSevere("Could not instantiate dialog of type " + dialogType + ": " + e);
			model.logStackTrace(e);
			return null;

		}
	}

	protected void parseExtraArgs(Object[] extraArgs)
	{
	}

	/**
	 * Displays the dialog box
	 *
	 * @return The user's input
	 */

	protected UserInput display()
	{
		// create and show dialog
		JOptionPane pane = new JOptionPane(createInterface(), JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
		setTitle(type.getTitle());
		setContentPane(pane);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);

		setModal(true);
		pack();
		setLocationRelativeTo(null);

		UserInput input = new UserInput();

		pane.addPropertyChangeListener(e -> {
			String prop = e.getPropertyName();
			if (!isVisible() || (e.getSource() != pane) || (!JOptionPane.VALUE_PROPERTY.equals(prop) && !JOptionPane.INPUT_VALUE_PROPERTY.equals(prop)))
				return;

			Object value = pane.getValue();
			if (value == JOptionPane.UNINITIALIZED_VALUE) return;

			// allow repeated clicking
			pane.setValue(JOptionPane.UNINITIALIZED_VALUE);

			// ok: validate
			if ((int) value == JOptionPane.OK_OPTION)
			{
				// populate input
				for (InputField inputField : inputFields)
					input.addField(inputField.getKey(), inputField);

				boolean valid = validateInput(input);
				input.setValid(valid);

				// all done
				if (valid)
					dispose();
			}

			// cancel/close: dispose
			else
			{
				input.setValid(false);
				dispose();
			}
		});

		// display
		setVisible(true);

		return input.isValid() ? input : null;
	}


	/**
	 * @return A panel containing the entire dialog's interface
	 */
	protected abstract JPanel createInterface();

	/**
	 * Ensures the user's input is valid (ie. all mandatory fields are given)
	 *
	 * @param input The user's input
	 * @return If the input is valid
	 */
	protected boolean validateInput(UserInput input)
	{
		List<String> errors = new ArrayList<>();

		// check mandatories
		for (InputField field : inputFields)
			if (field.isMandatory() && field.hasNoValue())
				errors.add(field.getLabel().getText() + " must be set");

		// check specifics
		validateAndFlag(errors, input);

		boolean success = errors.isEmpty();

		// popup
		if (!success)
		{
			String delimiter = "\n -";
			String errorMessage = "Please fix the following issue(s):" + delimiter + String.join(delimiter, errors);
			JOptionPane.showMessageDialog(this, errorMessage, "Uh oh", JOptionPane.ERROR_MESSAGE);
		}

		return success;
	}

	/**
	 * Validation specific to the dialog box
	 *
	 * @param errors A list of error strings to add to in case of an error
	 * @param input  The user's input
	 */
	protected abstract void validateAndFlag(List<String> errors, UserInput input);

	protected JPanel addSectionTitle(JPanel panel, String title)
	{
		TitledBorder border = new TitledBorder(title);
		border.setTitlePosition(TitledBorder.TOP);
		panel.setBorder(border);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		return panel;
	}

	/**
	 * Adds the given field to the given panel along with some spacing, and registers the field with the dialog box
	 *
	 * @param panel The panel to add to
	 * @param field The field
	 */
	protected void addField(JPanel panel, InputField field)
	{
		// centre the label
		Box box = Box.createHorizontalBox();
		box.add(field.getLabel());
		box.add(Box.createHorizontalBox());
		panel.add(box);

		// add field
		panel.add(field.getField());

		// add spacing
		panel.add(Box.createVerticalStrut(4));

		inputFields.add(field);
	}

}

