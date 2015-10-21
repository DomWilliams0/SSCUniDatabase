package dxw405.util;

public enum PersonType
{
	STUDENT("studentID", "StudentContact", "Student"),
	LECTURER("lecturerID", "LecturerContact", "Lecturer");

	private final String idName;
	private final String contactTableName;
	private final String tableName;

	PersonType(String idName, String contactTableName, String tableName)
	{

		this.idName = idName;
		this.contactTableName = contactTableName;
		this.tableName = tableName;
	}

	public String getIDName()
	{
		return idName;
	}

	public String getContactTableName()
	{
		return contactTableName;
	}

	public String getTableName()
	{
		return tableName;
	}
}
