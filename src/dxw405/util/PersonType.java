package dxw405.util;

public enum PersonType
{
	STUDENT("StudentContact", "Student", "studentID"),
	LECTURER("LecturerContact", "Lecturer", "lecturerID");

	private final String contactTableName;
	private final String tableName;
	private final String idName;

	PersonType(String contactTableName, String tableName, String idName)
	{

		this.contactTableName = contactTableName;
		this.tableName = tableName;
		this.idName = idName;
	}

	public String getContactTableName()
	{
		return contactTableName;
	}

	public String getTableName()
	{
		return tableName;
	}

	public String getIdName()
	{
		return idName;
	}
}
