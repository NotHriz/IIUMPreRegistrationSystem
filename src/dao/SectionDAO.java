package dao;

import model.Section;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SectionDAO {

    public List<Section> getSectionsByCourse(int courseId) {
        List<Section> sections = new ArrayList<>();
        String sql = "SELECT * FROM sections WHERE course_id = ?";

        try {
            Connection conn = DBConnection.getConnection(); // don’t close
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                sections.add(new Section(
                        rs.getInt("section_id"),
                        rs.getString("course_code"),
                        rs.getString("section_code"),
                        rs.getString("lecturer_name"),
                        rs.getString("schedule"),
                        rs.getString("venue")
                ));
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sections;
    }

    // Optional: getSectionById(int sectionId)
}
