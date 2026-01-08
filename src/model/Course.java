package model;

public class Course {
    private int courseId;
    private String code;
    private String name;
    private int creditHour;
    private String day;
    private String time;
    private String venue;
    private String lecturer;

    public Course(int courseId, String code, String name, int creditHour,
                  String day, String time, String venue, String lecturer) {
        this.courseId = courseId;
        this.code = code;
        this.name = name;
        this.creditHour = creditHour;
        this.day = day;
        this.time = time;
        this.venue = venue;
        this.lecturer = lecturer;
    }

    // Getters
    public int getCourseId() { return courseId; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public int getCreditHour() { return creditHour; }
    public String getDay() { return day; }
    public String getTime() { return time; }
    public String getVenue() { return venue; }
    public String getLecturer() { return lecturer; }
}
