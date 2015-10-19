package dxw405.util;

public enum Person
{
	STUDENT("studentID", "StudentContact", "Student"),
	LECTURER("lecturerID", "LecturerContact", "Lecturer");

	private final String idName;
	private final String contactTableName;
	private final String tableName;

	Person(String idName, String contactTableName, String tableName)
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


	// todo: generic method in Utils for enums
	public static Person parse(String s)
	{
		String upper = s.toUpperCase();
		for (Person person : values())
			if (person.toString().equals(upper))
				return person;
		return null;
	}
}
