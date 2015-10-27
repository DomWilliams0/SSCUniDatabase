SELECT
  lecturerID,
  titleString,
  forename,
  familyName
FROM Lecturer
  INNER JOIN Titles ON Titles.titleID = Lecturer.titleID