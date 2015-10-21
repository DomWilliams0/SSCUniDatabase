package dxw405.gui.dialog;

import dxw405.gui.dialog.dialogs.AddStudentDialog;
import dxw405.gui.dialog.dialogs.AddTutorDialog;
import dxw405.gui.dialog.dialogs.BaseDialog;
import dxw405.gui.dialog.dialogs.StudentReportDialog;

public enum DialogType
{
	ADD_STUDENT("Add Student", AddStudentDialog.class),
	ADD_TUTOR("Add Tutor", AddTutorDialog.class),
	REPORT_STUDENT("Student Report", StudentReportDialog.class),
	REPORT_LECTURER("Lecturer Report", null);

	private final Class<? extends BaseDialog> dialogClass;
	private String title;

	DialogType(String title, Class<? extends BaseDialog> dialogClass)
	{
		this.title = title;
		this.dialogClass = dialogClass;
	}

	public String getTitle()
	{
		return title;
	}

	public Class<? extends BaseDialog> getDialogClass()
	{
		return dialogClass;
	}
}
