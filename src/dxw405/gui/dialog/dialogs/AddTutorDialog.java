package dxw405.gui.dialog.dialogs;

import dxw405.gui.DBModel;
import dxw405.gui.PersonEntry;
import dxw405.gui.dialog.DialogType;
import dxw405.gui.dialog.UserInput;
import dxw405.gui.dialog.inputfields.ChoiceInputField;
import dxw405.gui.dialog.inputfields.IDInputField;
import dxw405.util.Person;

import javax.swing.*;
import java.util.List;

public class AddTutorDialog extends BaseDialog
{
	private PersonEntry student;

	public AddTutorDialog(DBModel dbModel, Object... extraArgs)
	{
		super(dbModel, DialogType.ADD_TUTOR, extraArgs);
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

		addField(panel, new IDInputField("studentID", "Student ID", true, student.id)).setEditable(false);
		addField(panel, new ChoiceInputField("lecturerID", "Tutor", true, model.getLecturerNames(), 0));

		return panel;
	}

	@Override
	protected void validateAndFlag(List<String> errors, UserInput input)
	{
		// add student id
		input.setVar("studentID", student.id);

		// get tutor id
		String lecturerName = model.getLecturerNames()[((int) input.getValue("lecturerID"))];
		PersonEntry chosenTutor = model.getEntryFromFullName(lecturerName, Person.LECTURER);
		input.setVar("lecturerEntry", chosenTutor);

		if (chosenTutor == null)
			errors.add("Invalid lecturer chosen");

		else
		{
			// same tutor check
			Integer currentTutorID = model.getTutorID(input.getValue("studentID"));
			if (currentTutorID == chosenTutor.id)
				errors.add("That lecturer is already that student's tutor");

		}
	}
}
