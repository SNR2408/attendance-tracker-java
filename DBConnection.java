import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// Connects to the SQLite database file and creates the tables if they don't exist.
public class DBConnection {

    private static final String URL = "jdbc:sqlite:attendance.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void createTables() throws SQLException {
        String subjects = "CREATE TABLE IF NOT EXISTS subjects ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL UNIQUE, "
                + "total_classes INTEGER DEFAULT 0, "
                + "attended INTEGER DEFAULT 0)";

        String log = "CREATE TABLE IF NOT EXISTS attendance_log ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "subject_id INTEGER, "
                + "date TEXT, "
                + "status TEXT, "
                + "FOREIGN KEY (subject_id) REFERENCES subjects(id))";

        try (Connection con = getConnection(); Statement st = con.createStatement()) {
            st.execute(subjects);
            st.execute(log);
        }
    }
}
