-- gets all student information for a single student
SELECT
  S.studentId,
  titleString,
  forename,
  familyname,
  SC.eMailAddress,
  dateOfBirth,
  yearOfStudy,
  description,
  TUT.lecturerID,
  SC.postalAddress,
  NOK.name,
  NOK.emailAddress,
  NOK.postalAddress
FROM Student S
  LEFT JOIN StudentRegistration SR ON S.studentID = SR.studentID
  LEFT JOIN StudentContact SC ON S.studentID = SC.studentID
  LEFT JOIN RegistrationType RT ON RT.registrationTypeID = SR.registrationTypeID
  LEFT JOIN Titles T ON S.titleid = T.titleid
  LEFT JOIN Tutor TUT ON S.studentID = TUT.studentID
  LEFT JOIN NextOfKin NOK ON S.studentID = NOK.studentID
WHERE S.studentid = ?;