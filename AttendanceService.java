// Business logic: the 75% rule. No SQL here, only calculations.
public class AttendanceService {

    // Highest number of classes that can still be missed while staying at 75% or more.
    // Rule: attended / (total + x) >= 3/4, which gives x <= attended * 4 / 3 - total
    public int classesCanSkip(Subject s) {
        return s.getAttended() * 4 / 3 - s.getTotalClasses();
    }

    // Number of classes that must be attended in a row to reach 75%.
    // Rule: (attended + x) / (total + x) >= 3/4, which gives x >= 3 * total - 4 * attended
    public int classesNeeded(Subject s) {
        return 3 * s.getTotalClasses() - 4 * s.getAttended();
    }

    public String getStatus(Subject s) {
        if (s.getTotalClasses() == 0) {
            return "No classes yet";
        }
        int needed = classesNeeded(s);
        if (needed <= 0) {
            return "Safe - can skip " + classesCanSkip(s) + " class(es)";
        }
        return "LOW - must attend next " + needed + " class(es)";
    }
}
