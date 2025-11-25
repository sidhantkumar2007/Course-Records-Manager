package edu.ccrm.domain;

import java.time.LocalDate;

public class Enrollment {
    private final Student student;
    private final Course course;
    private final LocalDate enrolledOn;
    private Grade grade;

    public Enrollment(Student student, Course course) {
        this.student = student;
        this.course = course;
        this.enrolledOn = LocalDate.now();
    }

    public Student getStudent() { return student; }
    public Course getCourse() { return course; }
    public LocalDate getEnrolledOn() { return enrolledOn; }
    public Grade getGrade() { return grade; }
    public void setGrade(Grade g) { this.grade = g; }

    @Override
    public String toString() {
        return course.getCode() + " | " + course.getTitle() + " | " + (grade != null ? grade.name() : "NA");
    }
}
