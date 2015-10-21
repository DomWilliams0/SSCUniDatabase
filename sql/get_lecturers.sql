SELECT
  Lecturer.lecturerID,
  titleString,
  forename,
  familyName,
  eMailAddress,
  office
FROM Lecturer
  INNER JOIN Titles ON Lecturer.titleID = Titles.titleID
  INNER JOIN LecturerContact ON LecturerContact.lecturerID = Lecturer.lecturerID