-- Titles (titleID, titleString)
CREATE TABLE Titles (
  titleID     SERIAL PRIMARY KEY,
  titleString VARCHAR(16) NOT NULL
);

INSERT INTO Titles VALUES
  (DEFAULT, 'Mr'), (DEFAULT, 'Mrs'), (DEFAULT, 'Miss'),
  (DEFAULT, 'Ms'), (DEFAULT, 'Prof'), (DEFAULT, 'Dr'),
  (DEFAULT, 'Hon'), (DEFAULT, 'Rev');

-- RegistrationType(registrationTypeID, description)
CREATE TABLE RegistrationType (
  registrationTypeID SERIAL PRIMARY KEY,
  description        VARCHAR(16) NOT NULL
);

INSERT INTO RegistrationType VALUES
  (DEFAULT, 'Normal'), (DEFAULT, 'Repeat'), (DEFAULT, 'Resit');


-- Student (studentID, titleID, forename, familyName, dateOfBirth)
CREATE TABLE Student (
  studentID   INTEGER PRIMARY KEY      CHECK (studentID > 0),
  titleID     INTEGER REFERENCES Titles NOT NULL,
  forename    VARCHAR(32)               NOT NULL,
  familyName  VARCHAR(32)               NOT NULL,
  dateOfBirth DATE                      NOT NULL CHECK (dateOfBirth >= '1900-01-01' :: DATE)
);

-- Lecturer (lecturerID, titleID, foreName, familyName)
CREATE TABLE Lecturer (
  lecturerID INTEGER PRIMARY KEY      CHECK (lecturerID > 0),
  titleID    INTEGER REFERENCES Titles NOT NULL,
  forename   VARCHAR(32)               NOT NULL,
  familyName VARCHAR(32)               NOT NULL
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
  eMailAddress  VARCHAR(320),
  postalAddress VARCHAR(512)
);

-- NextOfKin(studentID, name, eMailAddress, postalAddress)
CREATE TABLE NextOfKin (
  studentID     INTEGER PRIMARY KEY REFERENCES Student,
  name          VARCHAR(64),
  eMailAddress  VARCHAR(320),
  postalAddress VARCHAR(512)
);

-- LecturerContact(LecturerID, Office, eMailAddress)
CREATE TABLE LecturerContact (
  lecturerID   INTEGER PRIMARY KEY REFERENCES Lecturer,
  office       VARCHAR(10),
  eMailAddress VARCHAR(320)
);

-- Tutor(studentID, LecturerID)
CREATE TABLE Tutor (
  studentID  INTEGER REFERENCES Student,
  lecturerID INTEGER REFERENCES Lecturer
);
