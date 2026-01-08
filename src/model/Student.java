package model;

public class Student {
    private int studentId;
    private String matric;
    private String name;
    private String email;
    private int currentSem;
    private int studyLevel;
    private int totalChr;
    private int currentChr;
    private String password;

    public Student(int studentId, String matric, String name, String email,
                   int currentSem, int studyLevel, int totalChr, int currentChr, String password) {
        this.studentId = studentId;
        this.matric = matric;
        this.name = name;
        this.email = email;
        this.currentSem = currentSem;
        this.studyLevel = studyLevel;
        this.totalChr = totalChr;
        this.currentChr = currentChr;
        this.password = password;
    }

    // Getters (and optionally setters)
    public int getStudentId() { return studentId; }
    public String getMatric() { return matric; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public int getCurrentSem() { return currentSem; }
    public int getStudyLevel() { return studyLevel; }
    public int getTotalChr() { return totalChr; }
    public int getCurrentChr() { return currentChr; }
    public String getPassword() { return password; }
}
