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
                        rs.getInt("curr_capacity"),
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

    // get section capacity
    public int getSectionCapacity(int sectionId) {
        String sql = "SELECT curr_capacity FROM sections WHERE section_id = ?";
        int capacity = -1;

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                capacity = rs.getInt("curr_capacity");
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return capacity;
    }

    // Get section by ID
    public Section getSectionId(int sectionId) {
        String sql = "SELECT * FROM sections WHERE section_id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Section(
                        rs.getInt("section_id"),
                        rs.getString("course_code"),
                        rs.getString("section_code"),
                        rs.getString("lecturer_name"),
                        rs.getInt("curr_capacity"),
                        rs.getString("schedule"),
                        rs.getString("venue")
                );
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
