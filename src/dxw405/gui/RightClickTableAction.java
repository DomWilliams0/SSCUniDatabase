package dxw405.gui;

public enum RightClickTableAction
{
	VIEW_REPORT("View report"),
	ADD_TUTOR("Add tutor");

	private final String buttonText;

	RightClickTableAction(String buttonText)
	{
		this.buttonText = buttonText;
	}

	@Override
	public String toString()
	{
		return buttonText;
	}
}
