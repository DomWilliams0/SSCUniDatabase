package dxw405.util;

public enum PersonType
{
	STUDENT("StudentContact", "Student"),
	LECTURER("LecturerContact", "Lecturer");

	private final String contactTableName;
	private final String tableName;

	PersonType(String contactTableName, String tableName)
	{

		this.contactTableName = contactTableName;
		this.tableName = tableName;
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
