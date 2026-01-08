package services;

import dao.*;
import model.*;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class RegistrationService {

    public boolean addCourse(int studentId, int sectionId, String courseCode, int semester, int year) {

        CourseDAO courseDAO = new CourseDAO();
        PreRegistrationDAO preDAO = new PreRegistrationDAO();

        // Check course exists
        Course course = courseDAO.getCourseByCode(courseCode);
        if (course == null) {
            System.out.println("Course not found.");
            return false;
        }

        // Check prerequisite
        String prereqCode = course.getPrerequisite();
        if (prereqCode != null && !prereqCode.isEmpty()) {
            List<String> completedCourses =
                    preDAO.getCompletedCourseCodes(studentId);

            if (!completedCourses.contains(prereqCode)) {
                System.out.println("Prerequisite not met: " + prereqCode);
                return false;
            }
        }

        // Prevent duplicate preregistration
        if (preDAO.isAlreadyRegistered(studentId, courseCode, semester, year)) {
            System.out.println("Course already preregistered.");
            return false;
        }

        // Check max credit hours (20)
        int currentCredits = preDAO.getTotalRegisteredCredits(studentId, semester, year);
        if (currentCredits + course.getCreditHour() > 20) {
            System.out.println("Exceeds maximum credit hours (20).");
            return false;
        }

        // Insert preregistration
        String sql = """
            INSERT INTO preregistrations
            (student_id, section_id, course_code, semester, year, status)
            VALUES (?, ?, ?, ?, ?, 'Registered')
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);
            ps.setString(3, courseCode);
            ps.setInt(4, semester);
            ps.setInt(5, year);

            ps.executeUpdate();
            System.out.println("Course preregistered successfully.");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
