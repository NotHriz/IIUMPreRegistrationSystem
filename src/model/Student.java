package model;

public class Student {
    private int studentId;
    private String matric;
    private String name;
    private String email;
    private int totalChr;
    private int currentChr;
    private String password;

    public Student(int studentId, String matric, String name, String email,
                int totalChr, String password) {
        this.studentId = studentId;
        this.matric = matric;
        this.name = name;
        this.email = email;
        this.totalChr = totalChr;
        this.password = password;
    }

    // Getters (and optionally setters)
    public int getStudentId() { return studentId; }
    public String getMatric() { return matric; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public int getTotalChr() { return totalChr; }
    public int getCurrentChr() { return currentChr; }
    public String getPassword() { return password; }

    // toString method for easy printing
    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", matric='" + matric + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", totalChr=" + totalChr +
                ", password='" + password + '\'' +
                '}';
    }
}
