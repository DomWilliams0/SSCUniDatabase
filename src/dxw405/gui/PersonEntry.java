package dxw405.gui;

import dxw405.util.Person;

import java.util.Date;

public class PersonEntry
{
	private final Person person;
	public final int id;
	public final String title;
	public final String forename;
	public final String surname;
	public final Date dob;

	public PersonEntry(Person person, int id, String title, String forename, String surname, Date dob)
	{
		this.person = person;
		this.id = id;
		this.title = title;
		this.forename = forename;
		this.surname = surname;
		this.dob = dob;
	}
}
