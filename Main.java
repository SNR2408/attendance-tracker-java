import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

// Console menu. Talks to the DAO for data and to the service for calculations.
public class Main {

    private static Scanner sc = new Scanner(System.in);
    private static SubjectDAO dao = new SubjectDAOImpl();
    private static AttendanceService service = new AttendanceService();

    public static void main(String[] args) {
        try {
            DBConnection.createTables();
        } catch (SQLException e) {
            System.out.println("Could not set up the database: " + e.getMessage());
            return;
        }

        while (true) {
            printMenu();
            int choice = readInt("Enter choice: ");

            try {
                switch (choice) {
                    case 1:
                        addSubject();
                        break;
                    case 2:
                        markAttendance();
                        break;
                    case 3:
                        viewReport();
                        break;
                    case 4:
                        viewLogSummary();
                        break;
                    case 5:
                        deleteSubject();
                        break;
                    case 6:
                        System.out.println("Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid choice, try 1-6.");
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            } catch (SubjectNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("===== Attendance Tracker =====");
        System.out.println("1. Add subject");
        System.out.println("2. Mark attendance");
        System.out.println("3. View attendance report");
        System.out.println("4. View present/absent summary");
        System.out.println("5. Delete subject");
        System.out.println("6. Exit");
    }

    private static void addSubject() throws SQLException {
        System.out.print("Subject name: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }
        dao.addSubject(name);
        System.out.println("Subject added.");
    }

    private static void markAttendance() throws SQLException, SubjectNotFoundException {
        if (!showSubjects()) {
            return;
        }
        int id = readInt("Enter subject id: ");
        dao.getSubjectById(id); // throws SubjectNotFoundException if the id is wrong

        String answer = "";
        while (!answer.equals("P") && !answer.equals("A")) {
            System.out.print("Present (P) or Absent (A)? ");
            answer = sc.nextLine().trim().toUpperCase();
        }
        dao.markAttendance(id, answer.equals("P"));
        System.out.println("Attendance saved.");
    }

    private static void viewReport() throws SQLException {
        List<Subject> subjects = dao.getAllSubjects();
        if (subjects.isEmpty()) {
            System.out.println("No subjects yet. Add one first.");
            return;
        }
        System.out.printf("%-4s %-20s %-7s %-9s %-7s %s%n",
                "ID", "Subject", "Total", "Attended", "%", "Status");
        for (Subject s : subjects) {
            System.out.printf("%-4d %-20s %-7d %-9d %-7.1f %s%n",
                    s.getId(), s.getName(), s.getTotalClasses(), s.getAttended(),
                    s.getPercentage(), service.getStatus(s));
        }
    }

    private static void viewLogSummary() throws SQLException {
        List<String> lines = dao.getLogSummary();
        if (lines.isEmpty()) {
            System.out.println("No attendance marked yet.");
            return;
        }
        for (String line : lines) {
            System.out.println(line);
        }
    }

    private static void deleteSubject() throws SQLException, SubjectNotFoundException {
        if (!showSubjects()) {
            return;
        }
        int id = readInt("Enter subject id to delete: ");
        dao.getSubjectById(id);
        dao.deleteSubject(id);
        System.out.println("Subject deleted.");
    }

    // Prints id and name of every subject. Returns false if there are none.
    private static boolean showSubjects() throws SQLException {
        List<Subject> subjects = dao.getAllSubjects();
        if (subjects.isEmpty()) {
            System.out.println("No subjects yet. Add one first.");
            return false;
        }
        for (Subject s : subjects) {
            System.out.println(s.getId() + ". " + s.getName());
        }
        return true;
    }

    // Keeps asking until the user types a valid number.
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }
}
