package model;

public class Section {
    private int section_id, curr_capacity;
    private String course_code, section_code, lecturer_name, schedule, venue;

    // Constructor
    public Section(int section_id, String course_code, String section_code,
                   String lecturer_name, int curr_capacity, String schedule, String venue) {
        this.section_id = section_id;
        this.course_code = course_code;
        this.section_code = section_code;
        this.lecturer_name = lecturer_name;
        this.curr_capacity = curr_capacity;
        this.schedule = schedule;
        this.venue = venue;
    }

    // Getters
    public int getSectionId() {
        return section_id;
    }
    public String getCourseCode() {
        return course_code;
    }
    public String getSectionCode() {
        return section_code;
    }
    public String getLecturerName() {
        return lecturer_name;
    }
    public String getSchedule() {
        return schedule;
    }
    public String getVenue() {
        return venue;
    }
    public int getCurrCapacity() {
        return curr_capacity;
    }
    
    // toString method for easy printing
    @Override
    public String toString() {
        return "Section{" +
                "section_id=" + section_id +
                ", course_code='" + course_code + '\'' +
                ", section_code='" + section_code + '\'' +
                ", lecturer_name='" + lecturer_name + '\'' +
                ", curr_capacity=" + curr_capacity +
                ", schedule='" + schedule + '\'' +
                ", venue='" + venue + '\'' +
                '}';
    }
}
