package dao;

import utils.DBConnection;
import model.PreRegistration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PreRegistrationDAO {

    // Add a new preregistration
    public boolean addPreRegistration(int studentId, String courseCode, int sectionId, int semester, int year) {
        String sql = "INSERT INTO preregistrations(student_id, course_code, section_id, semester, year) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            ps.setString(2, courseCode);
            ps.setInt(3, sectionId);
            ps.setInt(4, semester);
            ps.setInt(5, year);

            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;

        } catch (SQLException e) {
            // Handles duplicate entry due to UNIQUE constraint
            if (e.getErrorCode() == 1062) {
                System.out.println("Student already preregistered for this course in this semester/year.");
            } else {
                e.printStackTrace();
            }
        }
        return false;
    }

    // Remove preregistration (admin privilege)
    public boolean removePreRegistration(int studentId, String courseCode, int sectionId, int semester, int year) {
        String sql = "DELETE FROM preregistrations " +
                     "WHERE student_id = ? AND course_code = ? AND section_id = ? AND semester = ? AND year = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            ps.setString(2, courseCode);
            ps.setInt(3, sectionId);
            ps.setInt(4, semester);
            ps.setInt(5, year);

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
                        rs.getInt("semester"),
                        rs.getInt("year"),
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
    public List<PreRegistration> getCurrentPreRegistrations(int studentId, int semester, int year) {
        List<PreRegistration> list = new ArrayList<>();
        String sql = "SELECT * FROM preregistrations WHERE student_id = ? AND semester = ? AND year = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            ps.setInt(2, semester);
            ps.setInt(3, year);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new PreRegistration(
                        rs.getInt("prereg_id"),
                        rs.getInt("student_id"),
                        rs.getString("course_code"),
                        rs.getInt("section_id"),
                        rs.getInt("semester"),
                        rs.getInt("year"),
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

    // Get completed course codes for a student
    public List<String> getCompletedCourseCodes(int studentId) {
        List<String> completed = new ArrayList<>();
        String sql = """
            SELECT course_code
            FROM preregistrations
            WHERE student_id = ?
            AND status = 'Completed'
        """;

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
    public boolean isAlreadyRegistered(int studentId, String courseCode, int semester, int year) {
        String sql = """
            SELECT 1 FROM preregistrations
            WHERE student_id = ?
            AND course_code = ?
            AND semester = ?
            AND year = ?
        """;

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            ps.setString(2, courseCode);
            ps.setInt(3, semester);
            ps.setInt(4, year);

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

    // Credit hour calulation (max 20, registered only)
    public int getTotalRegisteredCredits(int studentId, int semester, int year) {
        String sql = """
            SELECT SUM(c.credit_hour) AS total
            FROM preregistrations p
            JOIN courses c ON p.course_code = c.course_code
            WHERE p.student_id = ?
            AND p.semester = ?
            AND p.year = ?
            AND p.status = 'Registered'
        """;

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            ps.setInt(2, semester);
            ps.setInt(3, year);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }



}
