import java.sql.SQLException;
import java.util.List;

// Interface: lists what the database layer can do.
// The actual SQL is written in SubjectDAOImpl.
public interface SubjectDAO {

    void addSubject(String name) throws SQLException;

    List<Subject> getAllSubjects() throws SQLException;

    Subject getSubjectById(int id) throws SQLException, SubjectNotFoundException;

    void markAttendance(int id, boolean present) throws SQLException;

    void deleteSubject(int id) throws SQLException;

    List<String> getLogSummary() throws SQLException;
}
