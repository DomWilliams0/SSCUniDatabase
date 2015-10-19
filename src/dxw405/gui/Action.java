package dxw405.gui;

/**
 * Enum for button actions
 */
public enum Action
{
	ADD,
	ADD_STUDENT(ADD),

	VISIBILITY,
	VISIBLE_ALL(VISIBILITY),
	VISIBLE_STUDENTS(VISIBILITY),
	VISIBLE_LECTURERS(VISIBILITY),

	REPORT,
	REPORT_STUDENT(REPORT),
	REPORT_TUTOR(REPORT);

	private final Action parent;

	Action()
	{
		this(null);
	}

	Action(Action parent)
	{
		this.parent = parent;
	}

	public Action getParent()
	{
		return parent;
	}
}
