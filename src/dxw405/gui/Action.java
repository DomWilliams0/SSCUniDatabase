package dxw405.gui;

/**
 * Enum for button actions
 */
public enum Action
{
	ADD_STUDENT,

	VISIBLE_ALL,
	VISIBLE_STUDENTS,
	VISIBLE_LECTURERS,

	REPORT_STUDENT,
	REPORT_TUTOR;

	public static Action parse(String s)
	{
		for (Action action : values())
			if (action.toString().equals(s))
				return action;

		return null;
	}

}
