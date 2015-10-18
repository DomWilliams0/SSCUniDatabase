-- Titles (titleID, titleString)
CREATE TABLE Titles (
  titleID     SERIAL PRIMARY KEY,
  titleString CHAR(16) NOT NULL
);

INSERT INTO Titles VALUES
  (DEFAULT, 'Mr'), (DEFAULT, 'Mrs'), (DEFAULT, 'Miss'),
  (DEFAULT, 'Ms'), (DEFAULT, 'Master'), (DEFAULT, 'Doctor'),
  (DEFAULT, 'Professor'), (DEFAULT, 'Reverend');

-- RegistrationType(registrationTypeID, description)
CREATE TABLE RegistrationType (
  registrationTypeID SERIAL PRIMARY KEY,
  description        CHAR(16) NOT NULL
);

INSERT INTO RegistrationType VALUES
  (DEFAULT, 'Normal'), (DEFAULT, 'Repeat'), (DEFAULT, 'Resit');


-- Student (studentID, titleID, forename, familyName, dateOfBirth)
CREATE TABLE Student (
  studentID   INTEGER PRIMARY KEY      CHECK (studentID > 0),
  titleID     INTEGER REFERENCES Titles NOT NULL,
  forename    CHAR(32)                  NOT NULL,
  familyName  CHAR(32)                  NOT NULL,
  dateOfBirth DATE                      NOT NULL CHECK (dateOfBirth > '1900-01-01' :: DATE)
);

-- Lecturer (lecturerID, titleID, foreName, familyName)
CREATE TABLE Lecturer (
  lecturerID INTEGER PRIMARY KEY      CHECK (lecturerID > 0),
  titleID    INTEGER REFERENCES Titles,
  forename   CHAR(32) NOT NULL,
  familyName CHAR(32) NOT NULL
);

-- StudentRegistration(studentID, yearOfStudy, registrationTypeID)
CREATE TABLE StudentRegistration (
  studentID          INTEGER PRIMARY KEY REFERENCES Student,
  yearOfStudy        SMALLINT CHECK (yearOfStudy >= 1 AND yearOfStudy <= 5),
  registrationTypeID INTEGER REFERENCES RegistrationType
);


-- StudentContact (studentID, eMailAddress, postalAddress)
CREATE TABLE StudentContact (
  studentID     INTEGER PRIMARY KEY REFERENCES Student,
  eMailAddress  CHAR(320),
  postalAddress VARCHAR(512)
);

-- NextOfKin(studentID, name, eMailAddress, postalAddress)
CREATE TABLE NextOfKin (
  studentID     INTEGER PRIMARY KEY REFERENCES Student,
  name          CHAR(64),
  eMailAddress  CHAR(320),
  postalAddress VARCHAR(512)
);

-- LecturerContact(LecturerID, Office, eMailAddress)
CREATE TABLE LecturerContact (
  lecturerID   INTEGER PRIMARY KEY REFERENCES Lecturer,
  office       CHAR(10),
  eMailAddress CHAR(320)
);

-- Tutor(studentID, LecturerID)
CREATE TABLE Tutor (
  tutorID SERIAL PRIMARY KEY      CHECK (tutorID > 0),
  studentID  INTEGER REFERENCES Student,
  lecturerID INTEGER REFERENCES Lecturer
);
