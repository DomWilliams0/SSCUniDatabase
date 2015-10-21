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
  LEFT JOIN StudentRegistration SR ON S.studentID = SR.studentID
  LEFT JOIN StudentContact SC ON S.studentID = SC.studentID
  LEFT JOIN RegistrationType RT ON RT.registrationTypeID = SR.registrationTypeID
  LEFT JOIN Titles T ON S.titleid = T.titleid
  LEFT JOIN Tutor TUT ON S.studentID = TUT.studentID;


SELECT
  'Lecturer' AS "Person Type",
  Lecturer.lecturerId,
  titleString,
  forename,
  familyname,
  eMailAddress,
  office
FROM Lecturer
  INNER JOIN Titles ON Titles.titleId = Lecturer.titleId
  INNER JOIN LecturerContact ON LecturerContact.lecturerID = Lecturer.lecturerID