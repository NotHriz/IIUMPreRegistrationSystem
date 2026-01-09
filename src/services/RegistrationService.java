package services;

import dao.*;
import model.*;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/*
## Error Codes to return:
500 : Database error
101 : Course not found
102 : Prerequisite not met
103 : Course already preregistered
104 : Exceeds maximum credit hours
105 : Section capacity full
106 : Schedule conflict
0   : Success
*/

public class RegistrationService {

    public int addCourse(int studentId, int sectionId, String courseCode, int semester, int year) {

        CourseDAO courseDAO = new CourseDAO();
        PreRegistrationDAO preDAO = new PreRegistrationDAO();

        // Check course exists
        Course course = courseDAO.getCourseByCode(courseCode);
        if (course == null) {
            System.out.println("Course not found.");
            return 101;
        }

        // Check prerequisite
        String prereqCode = course.getPrerequisite();
        if (prereqCode != null && !prereqCode.isEmpty()) {
            List<String> completedCourses =
                    preDAO.getCompletedCourseCodes(studentId);

            if (!completedCourses.contains(prereqCode)) {
                System.out.println("Prerequisite not met: " + prereqCode);
                return 102;
            }
        }

        // Prevent duplicate preregistration
        if (preDAO.isAlreadyRegistered(studentId, courseCode, semester, year)) {
            System.out.println("Course already preregistered.");
            return 103;
        }

        // Check max credit hours (20)
        int currentCredits = preDAO.getTotalRegisteredCredits(studentId, semester, year);
        if (currentCredits + course.getCreditHour() > 20) {
            System.out.println("Exceeds maximum credit hours (20).");
            return 104;
        }

        // TODO:Check section capacity (x/30)
        SectionDAO sectionDAO = new SectionDAO();
        int capacity = sectionDAO.getSectionCapacity(sectionId);
        if (capacity >= 30) {
            System.out.println("Section capacity full.");
            return 105;
        }

        // TODO:Check schedule conflicts (just check time(String) overlaps)
        // Get all subject student is registered in
        List<PreRegistration> registeredSections = preDAO.getPreRegistrationsByStudent(studentId);
        // Compare them one by one
        for (PreRegistration p : registeredSections) {
            Section sec = sectionDAO.getSectionId(p.getSectionId());
            if (sec != null && sec.getSchedule().equals(
                    sectionDAO.getSectionId(sectionId).getSchedule())) {
                System.out.println("Schedule conflict with section ID: " + p.getSectionId());
                return 106;
            }
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
            return 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return 500;
        }
    }
}
