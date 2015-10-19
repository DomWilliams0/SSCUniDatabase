package dxw405.gui;

import dxw405.util.Person;

import java.util.Date;

public class PersonEntry
{
	private final Person person;
	public final int id;
	public final String fullname;
	public final Date dob;

	public PersonEntry(Person person, int id, String fullname, Date dob)
	{
		this.person = person;
		this.id = id;
		this.fullname = fullname;
		this.dob = dob;
	}
}
