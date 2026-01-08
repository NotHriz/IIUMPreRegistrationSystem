import java.util.List;

import dao.CourseDAO;
import model.Course;
import utils.DBConnection;

public class Main {
    public static void main(String[] args) {

    CourseDAO cdao = new CourseDAO();
    Course c = cdao.getCourseByCode("BICS2301");
    System.out.println(c.getName());

    List<Course> all = cdao.getAllCourses();
    all.forEach(course -> System.out.println(course.getName()));
    }
}
