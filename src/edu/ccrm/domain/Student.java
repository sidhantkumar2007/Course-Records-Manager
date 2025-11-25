package edu.ccrm.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Student extends Person {
    private final String regNo;
    private boolean active = true;
    private final LocalDate enrollmentDate;
    private final List<Enrollment> enrollments = new ArrayList<>();

    public Student(String id, String fullName, String email, String regNo) {
        super(id, fullName, email);
        this.regNo = regNo;
        this.enrollmentDate = LocalDate.now();
    }

    public String getRegNo() { return regNo; }
    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public List<Enrollment> getEnrollments() { return enrollments; }

    public void addEnrollment(Enrollment e) {
        enrollments.add(e);
    }

    public void removeEnrollment(Enrollment e) {
        enrollments.remove(e);
    }

    @Override
    public void printProfile() {
        System.out.println("Student: " + fullName + " | regNo: " + regNo + " | active: " + active);
    }

    public String detailedProfile() {
        StringBuilder sb = new StringBuilder();
        sb.append("Student: ").append(fullName).append("\nRegNo: ").append(regNo)
          .append("\nEmail: ").append(email).append("\nEnrollment Date: ").append(enrollmentDate).append("\nCourses:\n");
        for (Enrollment e : enrollments) {
            sb.append(" - ").append(e.getCourse().toString());
            if (e.getGrade() != null) sb.append(" | Grade: ").append(e.getGrade());
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student s = (Student) o;
        return Objects.equals(id, s.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }
}
