package model;

public class Course {
    private String courseCode, course_name, prerequisite_course_code;
    private int credit_hour;

    // Constructor
    public Course(String courseCode, String course_name, int credit_hour, String prerequisite_course_code) {
        this.courseCode = courseCode;
        this.course_name = course_name;
        this.credit_hour = credit_hour;
        this.prerequisite_course_code = prerequisite_course_code;
    }

    // Getters
    public String getCourseCode() { return courseCode; }
    public String getCourse_name() { return course_name; }
    public int getCredit_hour() { return credit_hour; }
    public String getPrerequisite_course_code() { return prerequisite_course_code; }

    // toString method
    @Override
    public String toString() {
        return "Course{" +
                "courseCode='" + courseCode + '\'' +
                ", course_name='" + course_name + '\'' +
                ", credit_hour=" + credit_hour +
                ", prerequisite_course_code='" + prerequisite_course_code + '\'' +
                '}';
    }
}
