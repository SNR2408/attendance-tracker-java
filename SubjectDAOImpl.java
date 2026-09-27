import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// DAO (Data Access Object): all the SQL of the project is here.
public class SubjectDAOImpl implements SubjectDAO {

    // INSERT
    public void addSubject(String name) throws SQLException {
        String sql = "INSERT INTO subjects(name) VALUES(?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.executeUpdate();
        }
    }

    // SELECT all
    public List<Subject> getAllSubjects() throws SQLException {
        List<Subject> list = new ArrayList<>();
        String sql = "SELECT id, name, total_classes, attended FROM subjects";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Subject(rs.getInt("id"), rs.getString("name"),
                        rs.getInt("total_classes"), rs.getInt("attended")));
            }
        }
        return list;
    }

    // SELECT one
    public Subject getSubjectById(int id) throws SQLException, SubjectNotFoundException {
        String sql = "SELECT id, name, total_classes, attended FROM subjects WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Subject(rs.getInt("id"), rs.getString("name"),
                            rs.getInt("total_classes"), rs.getInt("attended"));
                }
            }
        }
        throw new SubjectNotFoundException("No subject found with id " + id);
    }

    // UPDATE the counts + INSERT a log row.
    // Both happen together: if one fails, nothing is saved.
    public void markAttendance(int id, boolean present) throws SQLException {
        String updateSql;
        if (present) {
            updateSql = "UPDATE subjects SET total_classes = total_classes + 1, "
                    + "attended = attended + 1 WHERE id = ?";
        } else {
            updateSql = "UPDATE subjects SET total_classes = total_classes + 1 WHERE id = ?";
        }
        String logSql = "INSERT INTO attendance_log(subject_id, date, status) VALUES(?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(updateSql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(logSql)) {
                ps.setInt(1, id);
                ps.setString(2, LocalDate.now().toString());
                ps.setString(3, present ? "P" : "A");
                ps.executeUpdate();
            }

            con.commit();
        }
    }

    // DELETE the subject and its log rows
    public void deleteSubject(int id) throws SQLException {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM attendance_log WHERE subject_id = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM subjects WHERE id = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }

            con.commit();
        }
    }

    // JOIN + GROUP BY: present and absent count per subject, from the log table
    public List<String> getLogSummary() throws SQLException {
        List<String> lines = new ArrayList<>();
        String sql = "SELECT s.name, "
                + "SUM(CASE WHEN l.status = 'P' THEN 1 ELSE 0 END) AS present, "
                + "SUM(CASE WHEN l.status = 'A' THEN 1 ELSE 0 END) AS absent "
                + "FROM subjects s JOIN attendance_log l ON s.id = l.subject_id "
                + "GROUP BY s.name";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lines.add(rs.getString("name") + " -> Present: " + rs.getInt("present")
                        + ", Absent: " + rs.getInt("absent"));
            }
        }
        return lines;
    }
}
