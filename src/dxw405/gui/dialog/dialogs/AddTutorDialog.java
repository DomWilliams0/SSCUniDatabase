package dxw405.gui.dialog.dialogs;

import dxw405.gui.DBModel;
import dxw405.gui.PersonEntry;
import dxw405.gui.dialog.DialogType;
import dxw405.gui.dialog.UserInput;
import dxw405.gui.dialog.inputfields.ChoiceInputField;
import dxw405.gui.dialog.inputfields.IDInputField;

import javax.swing.*;
import java.util.List;
import java.util.Objects;

public class AddTutorDialog extends BaseDialog
{
	private PersonEntry student;
	private List<String> lecturerNames;

	public AddTutorDialog(DBModel dbModel, Object... extraArgs)
	{
		super(dbModel, DialogType.ADD_TUTOR, extraArgs);
		lecturerNames = model.getLecturerNames();
	}

	@Override
	protected void parseExtraArgs(Object[] extraArgs)
	{
		student = (PersonEntry) extraArgs[0];
	}

	@Override
	protected JPanel createInterface()
	{
		JPanel panel = new JPanel();
		BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(layout);

		addField(panel, new IDInputField("studentID", "Student ID", true, student.getID())).setEditable(false);

		addField(panel, new ChoiceInputField("lecturerID", "Tutor", true, lecturerNames.toArray(new String[lecturerNames.size()]), 0));

		return panel;
	}

	@Override
	protected void validateAndFlag(List<String> errors, UserInput input)
	{
		// add student id
		input.setVar("studentID", student.getID());

		// get tutor id
		String lecturerChoice = lecturerNames.get(input.getValue("lecturerID"));
		Integer lecturerID = splitChoice(lecturerChoice);

		input.setVar("lecturerID", lecturerID);

		if (lecturerID == null)
			errors.add("Invalid lecturer chosen");

		else
		{
			// same tutor check
			Integer currentTutorID = model.getTutorID(input.getValue("studentID"));
			if (currentTutorID != null && Objects.equals(currentTutorID, lecturerID))
				errors.add("That lecturer is already that student's tutor");

		}
	}

	private Integer splitChoice(String choice)
	{
		String[] split = choice.split(":");
		if (split.length != 2)
			return null;

		try
		{
			return Integer.parseInt(split[0]);
		} catch (NumberFormatException e)
		{
			return null;
		}
	}
}
