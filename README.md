# Attendance Tracker (Java + JDBC + SQLite)

A console app to track subject-wise attendance and see how many classes you can skip
(or must attend) to stay at 75%.

## Features
- Add and delete subjects
- Mark present / absent (saved with the date)
- Attendance report with % and 75% status
- Present/absent summary using a SQL JOIN + GROUP BY

## Project structure
| File | Role |
|---|---|
| `Subject.java` | Model class (encapsulation) |
| `SubjectDAO.java` | Interface for database operations (abstraction) |
| `SubjectDAOImpl.java` | All SQL queries using PreparedStatement |
| `AttendanceService.java` | 75% calculations (business logic) |
| `SubjectNotFoundException.java` | Custom exception |
| `DBConnection.java` | Connects to SQLite, creates tables |
| `Main.java` | Menu-driven console UI |

## How to run
1. Install JDK 17 or newer.
2. Download `sqlite-jdbc-3.36.0.3.jar` from Maven Central and put it in this folder.
3. Compile and run:

Windows:
```
javac -cp ".;sqlite-jdbc-3.36.0.3.jar" *.java
java -cp ".;sqlite-jdbc-3.36.0.3.jar" Main
```

Mac / Linux:
```
javac -cp ".:sqlite-jdbc-3.36.0.3.jar" *.java
java -cp ".:sqlite-jdbc-3.36.0.3.jar" Main
```

The database file `attendance.db` is created automatically on first run.

## Formulas (75% rule)
- Classes you can skip = attended * 4 / 3 - total
- Classes you must attend = 3 * total - 4 * attended
