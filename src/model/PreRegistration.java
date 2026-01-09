package model;

public class PreRegistration {
    private int prereg_id;
    private int student_id;
    private String course_code;
    private int section_id;
    private String status;
    private java.sql.Timestamp prereg_date;

    // Constructor
    public PreRegistration(int prereg_id, int student_id, String course_code,
                          int section_id,
                          String status, java.sql.Timestamp prereg_date) {
        this.prereg_id = prereg_id;
        this.student_id = student_id;
        this.course_code = course_code;
        this.section_id = section_id;
        this.status = status;
        this.prereg_date = prereg_date;
    }

    // Getters
    public int getPreregId() {
        return prereg_id;
    }
    public int getStudentId() {
        return student_id;
    }
    public String getCourseCode() {
        return course_code;
    }
    public int getSectionId() {
        return section_id;
    }
    public String getStatus() {
        return status;
    }
    public java.sql.Timestamp getPreregDate() {
        return prereg_date;
    }

    // toString
    @Override
    public String toString() {
        return "PreRegistration{" +
                "prereg_id=" + prereg_id +
                ", student_id=" + student_id +
                ", course_code='" + course_code + '\'' +
                ", section_id=" + section_id +
                ", status='" + status + '\'' +
                ", prereg_date=" + prereg_date +
                '}';
    }
}
