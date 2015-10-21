package dxw405.gui;

import dxw405.util.Person;

import java.util.Date;

public class PersonEntry
{
	public final Person person;
	public final int id;
	public final String title;
	public final String forename;
	public final String surname;
	public final String email;
	public final Integer yearOfStudy;
	public final String courseType;
	public final Integer tutorID;
	public final Date dob;

	public PersonEntry(Person person, int id, String title, String forename, String surname, String email,
					   Integer yearOfStudy, String courseType, Integer tutorID, Date dob)
	{
		this.person = person;
		this.id = id;
		this.title = title != null ? title.trim() : null;
		this.forename = forename != null ? forename.trim() : null;
		this.surname = surname != null ? surname.trim() : null;
		this.email = email != null ? email.trim() : null;
		this.yearOfStudy = yearOfStudy;
		this.courseType = courseType != null ? courseType.trim() : null;
		this.tutorID = tutorID;
		this.dob = dob;
	}

	public static PersonEntry addStudent(int id, String title, String forename, String surname, String email,
										 Integer yearOfStudy, String courseType, Integer tutorID, Date dob)
	{
		return new PersonEntry(Person.STUDENT, id, title, forename, surname, email, yearOfStudy, courseType, tutorID, dob);
	}

	public static PersonEntry addLecturer(int id, String title, String forename, String surname, String email)
	{
		return new PersonEntry(Person.LECTURER, id, title, forename, surname, email, null, null, null, null);
	}

	public String getFullName()
	{
		return title + ". " + forename + " " + surname;
	}
}
