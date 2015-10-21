package dxw405.gui.dialog.dialogs;

import dxw405.gui.DBModel;
import dxw405.gui.PersonEntry;
import dxw405.gui.dialog.DialogType;
import dxw405.gui.dialog.UserInput;
import dxw405.gui.dialog.inputfields.ChoiceInputField;
import dxw405.gui.dialog.inputfields.IDInputField;
import dxw405.gui.dialog.inputfields.InputField;

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

		InputField studentID = new IDInputField("studentID", "Student ID", true, student.id);
		studentID.setEditable(false);
		addField(panel, studentID);

		addField(panel, new ChoiceInputField("lecturerID", "Tutor", true, model.getLecturerNames(), 0));

		return panel;
	}

	@Override
	protected void validateAndFlag(List<String> errors, UserInput input)
	{
		// add student id
		input.setVar("studentID", student.id);

		// same tutor
		if (model.getTutorID(input.getValue("studentID")) == input.getValue("lecturerID"))
			errors.add("That lecturer is already that student's tutor");
	}
}
