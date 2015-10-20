package dxw405.gui.dialog;

public enum DialogType
{
	ADD_STUDENT("Add Student"),
	ADD_TUTOR("Add Tutor"),
	REPORT_STUDENT("Student Report"),
	REPORT_LECTURER("Lecturer Report");

	private String title;

	DialogType(String title) {this.title = title;}

	public String getTitle()
	{
		return title;
	}

}
