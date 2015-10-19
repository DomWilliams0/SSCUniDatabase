SELECT
  'Student' AS "Person Type",
  studentId,
  titleString,
  forename,
  familyname,
  dateOfBirth
FROM Student
  INNER JOIN Titles ON Titles.titleId = Student.titleId;

SELECT
  'Lecturer' AS "Person Type",
  lecturerId,
  titleString,
  forename,
  familyname
FROM Lecturer
  INNER JOIN Titles ON Titles.titleId = Lecturer.titleId