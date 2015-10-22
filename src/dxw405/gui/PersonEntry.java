package dxw405.gui;

import dxw405.util.PersonType;
import dxw405.util.Utils;

import java.util.Date;

public class PersonEntry
{
	private PersonType personType;
	private int id;
	private String title;
	private String forename;
	private String surname;
	private String email;

	private Integer yearOfStudy;
	private String courseType;
	private Date dob;
	private Integer tutorID;
	private String tutorName;

	private String address;
	private String nokName;
	private String nokEmail;
	private String nokAddress;
	private String office;
	private boolean populated;

	/**
	 * Normal constructor for common fields between students and stff
	 */
	public PersonEntry(PersonType personType, int id, String title, String forename, String surname, String email)
	{
		this.personType = personType;
		this.populated = false;
		setID(id);
		setTitle(title);
		setForename(forename);
		setSurname(surname);
		setEmail(email);
	}

	public PersonEntry(PersonType personType, int id, String title, String forename, String surname, String email, Integer yearOfStudy, String courseType,
					   Date dob,
					   Integer tutorID, String address, String nokName, String nokEmail, String nokAddress, String office)
	{
		this(personType, id, title, forename, surname, email);
		setYearOfStudy(yearOfStudy);
		setCourseType(courseType);
		setDOB(dob);
		setTutorID(tutorID);
		setAddress(address);
		setNOKName(nokName);
		setNOKEmail(nokEmail);
		setNOKAddress(nokAddress);
		setOffice(office);
	}

	public static PersonEntry addStudent(int id, String title, String forename, String surname, String email,
										 Integer yearOfStudy, String courseType, Integer tutorID, Date dob)
	{
		PersonEntry entry = new PersonEntry(PersonType.STUDENT, id, title, forename, surname, email);
		entry.setYearOfStudy(yearOfStudy);
		entry.setCourseType(courseType);
		entry.setTutorID(tutorID);
		entry.setDOB(dob);
		return entry;
	}

	public static PersonEntry addLecturer(int id, String title, String forename, String surname, String email, String office)
	{
		PersonEntry entry = new PersonEntry(PersonType.STUDENT, id, title, forename, surname, email);
		entry.setOffice(office);
		return entry;

	}

	public boolean isPopulated()
	{
		return populated;
	}

	public String getFullName()
	{
		return title + ". " + forename + " " + surname;
	}


	public PersonType getPersonType()
	{
		return personType;
	}

	public int getID()
	{
		return id;
	}

	public void setID(int id)
	{
		this.id = id;
	}

	public String getIDString()
	{
		return Integer.toString(id);
	}

	public String getTitle()
	{
		return title + ".";
	}

	public void setTitle(String title)
	{
		this.title = title != null ? Utils.capitalise(title.trim()) : null;
	}

	public String getUnformattedTitle()
	{
		return title;
	}

	public String getForename()
	{
		return forename;
	}

	public void setForename(String forename)
	{
		this.forename = forename != null ? Utils.capitalise(forename.trim()) : null;
	}

	public String getSurname()
	{
		return surname;
	}

	public void setSurname(String surname)
	{
		this.surname = surname != null ? Utils.capitalise(surname.trim()) : null;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email != null ? email.trim() : null;
	}

	public Integer getYearOfStudy()
	{
		return yearOfStudy;
	}

	public void setYearOfStudy(Integer yearOfStudy)
	{
		this.yearOfStudy = yearOfStudy;
	}

	public String getCourseType()
	{
		return courseType;
	}

	public void setCourseType(String courseType)
	{
		this.courseType = courseType != null ? courseType.trim() : null;
	}

	public Date getDOB()
	{
		return dob;
	}

	public void setDOB(Date dob)
	{
		this.dob = dob;
	}

	public Integer getTutorID()
	{
		return tutorID;
	}

	public void setTutorID(Integer tutorID)
	{
		this.tutorID = tutorID;
	}

	public String getTutorName()
	{
		return tutorName;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address != null ? address.trim() : null;
	}

	public String getNOKName()
	{
		return nokName;
	}

	public void setNOKName(String nokName)
	{
		this.nokName = nokName != null ? nokName.trim() : null;
	}

	public String getNOKEmail()
	{
		return nokEmail;
	}

	public void setNOKEmail(String nokEmail)
	{
		this.nokEmail = nokEmail != null ? nokEmail.trim() : null;
	}

	public String getNOKAddress()
	{
		return nokAddress;
	}

	public void setNOKAddress(String nokAddress)
	{
		this.nokAddress = nokAddress != null ? nokAddress.trim() : null;
	}

	public String getOffice()
	{
		return office;
	}

	public void setOffice(String office)
	{
		this.office = office != null ? office.trim() : null;
	}

	public void setTutorID(Integer tutorID, DBModel model)
	{
		setTutorID(tutorID);

		if (tutorID != null)
		{
			PersonEntry tutor = model.getEntryFromID(tutorID);
			if (tutor != null)
				tutorName = tutor.getFullName();
		} else
			tutorName = null;
	}

	public String getDOBFormatted()
	{
		return dob == null ? "" : Utils.DATE_FORMATTER.format(dob);
	}

	public void setPopulated(boolean populated)
	{
		this.populated = populated;
	}
}
