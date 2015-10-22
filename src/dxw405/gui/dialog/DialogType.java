package dxw405.gui.dialog;

import dxw405.gui.dialog.dialogs.*;

import javax.swing.*;

public enum DialogType
{
	ADD_STUDENT("Add Student", AddStudentDialog.class, JOptionPane.OK_CANCEL_OPTION),
	ADD_TUTOR("Add Tutor", AddTutorDialog.class, JOptionPane.OK_CANCEL_OPTION),
	REPORT_STUDENT("Student Report", StudentReportDialog.class, JOptionPane.DEFAULT_OPTION),
	REPORT_LECTURER("Lecturer Report", LecturerReportDialog.class, JOptionPane.DEFAULT_OPTION);

	private final Class<? extends BaseDialog> dialogClass;
	private final String title;
	private final int dialogOption;

	DialogType(String title, Class<? extends BaseDialog> dialogClass, int dialogOption)
	{
		this.title = title;
		this.dialogClass = dialogClass;
		this.dialogOption = dialogOption;
	}

	public String getTitle()
	{
		return title;
	}

	public Class<? extends BaseDialog> getDialogClass()
	{
		return dialogClass;
	}

	public int getDialogOption()
	{
		return dialogOption;
	}
}
