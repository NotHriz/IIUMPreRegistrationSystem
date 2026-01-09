package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.Student;
import utils.DBConnection;


public class StudentDAO {

    // Get student info by matric number
    public Student getStudentByMatric(String matric) {
        String sql = "SELECT * FROM students WHERE matric_no = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, matric);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Student(
                        rs.getInt("student_id"),
                        rs.getString("matric_no"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getInt("total_credits"),
                        rs.getString("password")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Not found
    }

    // Check login
    public boolean login(String matric, String password) {
        String sql = "SELECT * FROM students WHERE matric_number = ? AND password = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, matric);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            return rs.next(); // true if student exists and password matches

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get list of courses the student has already taken (for prerequisites)
    public List<Integer> getTakenCourses(int studentId) {
        List<Integer> taken = new ArrayList<>();
        String sql = "SELECT course_id FROM registrations WHERE student_id = ? AND status = 'taken'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                taken.add(rs.getInt("course_id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return taken;
    }

    // Get list of courses currently registered (current semester)
    public List<Integer> getCurrentRegistrations(int studentId) {
        List<Integer> current = new ArrayList<>();
        String sql = "SELECT course_id FROM registrations WHERE student_id = ? AND status = 'current'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                current.add(rs.getInt("course_id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return current;
    }

    // Get student ID by matric number
    public int getStudentIdByMatric(String matric) {
        String sql = "SELECT student_id FROM students WHERE matric_no = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, matric);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("student_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Not found
    }
}