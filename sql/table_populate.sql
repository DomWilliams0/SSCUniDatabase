SELECT
  'Student' AS "Person Type",
  S.studentId,
  titleString,
  forename,
  familyname,
  eMailAddress,
  dateOfBirth,
  yearOfStudy,
  description,
  TUT.lecturerID
FROM Student S
  INNER JOIN StudentRegistration SR ON S.studentID = SR.studentID
  INNER JOIN StudentContact SC ON S.studentID = SC.studentID
  INNER JOIN RegistrationType RT ON RT.registrationTypeID = SR.registrationTypeID
  INNER JOIN Tutor TUT ON S.studentID = TUT.studentID
  INNER JOIN Titles T ON S.titleid = T.titleid;


SELECT
  'Lecturer' AS "Person Type",
  Lecturer.lecturerId,
  titleString,
  forename,
  familyname,
  eMailAddress
FROM Lecturer
  INNER JOIN Titles ON Titles.titleId = Lecturer.titleId
  INNER JOIN LecturerContact ON LecturerContact.lecturerID = Lecturer.lecturerID