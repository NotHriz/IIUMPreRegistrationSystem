import java.util.List;

import dao.CourseDAO;
import model.Course;

import dao.StudentDAO;
import model.Student;
import utils.DBConnection;

public class Main {
    public static void main(String[] args) {

        // Initialize DB Connection
        DBConnection.getConnection();

        // Test for Students
        int matric = 2411001;
        StudentDAO sdao = new StudentDAO();
        Student s = sdao.getStudentByMatric(String.valueOf(matric));
        System.out.println(s);

        // Test for Courses
        CourseDAO cdao = new CourseDAO();
        List<Course> courses = cdao.getAllCourses();
        for (Course c : courses) {
            System.out.println(c);
        }


    }
}
