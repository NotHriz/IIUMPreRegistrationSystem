package dao;

// Java Standard Library - SQL
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// Java Standard Library - Collections
import java.util.ArrayList;
import java.util.List;

// Internal Layers
import model.Course;
import utils.DBConnection;

public class CourseDAO {

    // Get course by code
    public Course getCourseByCode(String code) {
        String sql = "SELECT * FROM courses WHERE course_code = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Course(
                        rs.getString("course_code"),
                        rs.getString("course_name"),
                        rs.getInt("credit_hour"),
                        rs.getString("prerequisite_course_code"),
                        rs.getString("cat")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get all courses
    public List<Course> getAllCourses() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM courses";
        Connection conn = DBConnection.getConnection();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Course(
                        rs.getString("course_code"),
                        rs.getString("course_name"),
                        rs.getInt("credit_hour"),
                        rs.getString("prerequisite_course_code"),
                        rs.getString("cat")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

}
