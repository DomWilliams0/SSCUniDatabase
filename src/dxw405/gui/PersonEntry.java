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
	public final Date dob;
	public Integer tutorID;
	public String tutorName;

	public String address;
	public String nokName;
	public String nokEmail;
	public String nokAddress;
	public String office;

	public PersonEntry(Person person, int id, String title, String forename, String surname, String email,
					   Integer yearOfStudy, String courseType, Integer tutorID, Date dob, String office)
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
		this.tutorName = null;
		this.dob = dob;
		this.office = office != null ? office.trim() : null;

		address = nokName = nokEmail = nokAddress = null;
	}

	public static PersonEntry addStudent(int id, String title, String forename, String surname, String email,
										 Integer yearOfStudy, String courseType, Integer tutorID, Date dob)
	{
		return new PersonEntry(Person.STUDENT, id, title, forename, surname, email, yearOfStudy, courseType, tutorID, dob, null);
	}

	public static PersonEntry addLecturer(int id, String title, String forename, String surname, String email, String office)
	{
		return new PersonEntry(Person.LECTURER, id, title, forename, surname, email, null, null, null, null, office);
	}

	public void updateTutorName(DBModel model)
	{
		if (tutorID != null)
		{
			PersonEntry tutor = model.getEntryFromID(tutorID);
			if (tutor != null)
				tutorName = tutor.getFullName();
		} else
			tutorName = null;
	}

	public String getFullName()
	{
		return title + ". " + forename + " " + surname;
	}
}
