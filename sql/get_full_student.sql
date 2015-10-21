SELECT
  SC.postalAddress,
  NOK.name,
  NOK.emailAddress,
  NOK.postalAddress
FROM StudentContact SC, NextOfKin NOK
WHERE SC.studentID = NOK.studentID AND SC.studentID = ?;