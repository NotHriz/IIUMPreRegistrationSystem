package services;

import utils.DBConnection;
import java.sql.*;

// handles user login
public class AuthService {
    public boolean authenticate(String matric_number, String password) {
        String sql = "SELECT * FROM students WHERE matric_no = ? AND password = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, matric_number);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            return rs.next(); // true if student exists and password matches

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Authentication failed
        return false;
    }
}
