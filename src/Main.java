import java.util.List;

import dao.*;
import model.*;
import services.*;

import utils.DBConnection;

@SuppressWarnings("unused")
public class Main {
    public static void main(String[] args) {

        // Initialize DB Connection
        DBConnection.getConnection();

        // // Test for Students
        // int matric = 2411001;
        // StudentDAO sdao = new StudentDAO();
        // Student s = sdao.getStudentByMatric(String.valueOf(matric));
        // System.out.println(s);

        // // Test for Courses
        // CourseDAO cdao = new CourseDAO();
        // List<Course> courses = cdao.getAllCourses();
        // for (Course c : courses) {
        //     System.out.println(c);
        // }

        // Test auth
        AuthService authService = new AuthService();
        boolean loginSuccess = authService.authenticate("2411001", "pass1234");
        System.out.println("Login success: " + loginSuccess);
    }
}
