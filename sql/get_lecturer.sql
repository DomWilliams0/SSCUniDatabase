-- gets all lecturer information for a single lecturer
SELECT
  L.lecturerID,
  titleString,
  forename,
  familyName,
  eMailAddress,
  office
FROM Lecturer L
  INNER JOIN Titles T ON L.titleID = T.titleID
  INNER JOIN LecturerContact LC ON LC.lecturerID = L.lecturerID
WHERE L.lecturerID = ?;