package dao;

import utils.DBConnection;
import model.PreRegistration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PreRegistrationDAO {

    // Add a new preregistration
    public boolean addPreRegistration(int studentId, String courseCode, int sectionId) {
        String sql = "INSERT INTO preregistrations(student_id, course_code, section_id, status) VALUES (?, ?, ?, 'Registered')";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            ps.setString(2, courseCode);
            ps.setInt(3, sectionId);

            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.out.println("Student already preregistered for this course.");
            } else {
                e.printStackTrace();
            }
        }
        return false;
    }

    // Remove preregistration
    public boolean removePreRegistration(int studentId, String courseCode) {
        // Updated SQL to match new schema (removed semester/year)
        String sql = "DELETE FROM preregistrations WHERE student_id = ? AND course_code = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            ps.setString(2, courseCode);

            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get all preregistrations for a student
    public List<PreRegistration> getPreRegistrationsByStudent(int studentId) {
        List<PreRegistration> list = new ArrayList<>();
        String sql = "SELECT * FROM preregistrations WHERE student_id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new PreRegistration(
                        rs.getInt("prereg_id"),
                        rs.getInt("student_id"),
                        rs.getString("course_code"),
                        rs.getInt("section_id"),
                        rs.getString("status"),
                        rs.getTimestamp("prereg_date")
                ));
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Get preregistrations for current semester/year
    public List<PreRegistration> getCurrentPreRegistrations(int studentId) {
        List<PreRegistration> list = new ArrayList<>();
        String sql = "SELECT * FROM preregistrations WHERE student_id = ? AND status = 'Registered'";

        // The connection 'conn' is created here and will be closed automatically at the end of the }
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = (conn != null) ? conn.prepareStatement(sql) : null) {
            
            if (ps == null) return list;

            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new PreRegistration(
                            rs.getInt("prereg_id"),
                            rs.getInt("student_id"),
                            rs.getString("course_code"),
                            rs.getInt("section_id"),
                            rs.getString("status"),
                            rs.getTimestamp("prereg_date")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Caught here! Your UI/Main code remains unchanged.
        }
        return list;
    }

    // Get completed course codes for a student
    public List<String> getCompletedCourseCodes(int studentId) {
        List<String> completed = new ArrayList<>();
        String sql = "SELECT course_code FROM preregistrations WHERE student_id = ? AND status = 'Completed'";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                completed.add(rs.getString("course_code"));
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return completed;
    }

    // Prevent duplicate preregistration
    public boolean isAlreadyRegistered(int studentId, String courseCode) {
        String sql = "SELECT 1 FROM preregistrations WHERE student_id = ? AND course_code = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            ps.setString(2, courseCode);

            ResultSet rs = ps.executeQuery();
            boolean exists = rs.next();

            rs.close();
            ps.close();
            return exists;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Credit hour calculation
    public int getTotalRegisteredCredits(int studentId) {
        String sql = "SELECT SUM(c.credit_hour) AS total " +
                     "FROM preregistrations p " +
                     "JOIN courses c ON p.course_code = c.course_code " +
                     "WHERE p.student_id = ? AND p.status = 'Registered'";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);

            ResultSet rs = ps.executeQuery();
            int total = 0;
            if (rs.next()) {
                total = rs.getInt("total");
            }

            rs.close();
            ps.close();
            return total;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}