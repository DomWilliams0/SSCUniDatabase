package dxw405.util;

public enum Person
{
	STUDENT("studentID", "Student"),
	LECTURER("lecturerID", "Lecturer");

	private final String idName;
	private final String tableName;

	Person(String idName, String tableName)
	{

		this.idName = idName;
		this.tableName = tableName;
	}

	public String getIDName()
	{
		return idName;
	}

	public String getTableName()
	{
		return tableName;
	}
}
