package edu.ccrm.service;

import edu.ccrm.domain.*;
import edu.ccrm.service.exceptions.DuplicateEnrollmentException;
import edu.ccrm.service.exceptions.MaxCreditLimitExceededException;
import java.util.*;
import java.util.stream.Collectors;

public class EnrollmentService {
    private final StudentService studentService;
    private final CourseService courseService;
    private final Map<String, List<Enrollment>> enrollmentsByStudent = new HashMap<>();
    private final int MAX_CREDITS_PER_SEM = 20;

    public EnrollmentService(StudentService ss, CourseService cs) {
        this.studentService = ss;
        this.courseService = cs;
    }

    public void enroll(String studentId, String courseCode) throws Exception {
        Student s = studentService.findById(studentId);
        Course c = courseService.findByCode(courseCode);
        if (s == null || c == null) throw new IllegalArgumentException("Student or Course not found");
        List<Enrollment> list = enrollmentsByStudent.computeIfAbsent(studentId, k -> new ArrayList<>());
        boolean already = list.stream().anyMatch(e -> e.getCourse().getCode().equals(courseCode));
        if (already) throw new DuplicateEnrollmentException("Already enrolled");
        int currentCredits = list.stream()
                .filter(e -> e.getCourse().getSemester() == c.getSemester())
                .mapToInt(e -> e.getCourse().getCredits()).sum();
        if (currentCredits + c.getCredits() > MAX_CREDITS_PER_SEM) throw new MaxCreditLimitExceededException("Max credits exceeded");
        Enrollment e = new Enrollment(s, c);
        list.add(e);
        s.addEnrollment(e);
    }

    public void unenroll(String studentId, String courseCode) {
        List<Enrollment> list = enrollmentsByStudent.get(studentId);
        if (list == null) return;
        Enrollment found = list.stream().filter(e -> e.getCourse().getCode().equals(courseCode)).findFirst().orElse(null);
        if (found != null) {
            list.remove(found);
            found.getStudent().removeEnrollment(found);
        }
    }

    public void recordGrade(String studentId, String courseCode, String gradeStr) {
        List<Enrollment> list = enrollmentsByStudent.get(studentId);
        if (list == null) throw new IllegalArgumentException("No enrollments");
        Enrollment e = list.stream().filter(x -> x.getCourse().getCode().equals(courseCode)).findFirst().orElseThrow(() -> new IllegalArgumentException("Not enrolled"));
        Grade g = Grade.valueOf(gradeStr.toUpperCase());
        e.setGrade(g);
    }

    public String getTranscript(String studentId) {
        Student s = studentService.findById(studentId);
        if (s == null) return "Student not found";
        class T {
            static String fmt(double v) { return String.format("%.2f", v); }
        }
        double gpa = computeGPA(s);
        StringBuilder sb = new StringBuilder();
        sb.append("Transcript for ").append(s.getFullName()).append("\n");
        s.getEnrollments().forEach(en -> sb.append(en.toString()).append("\n"));
        sb.append("GPA: ").append(T.fmt(gpa)).append("\n");
        return sb.toString();
    }

    public double computeGPA(Student s) {
        List<Enrollment> list = s.getEnrollments();
        int totalCredits = list.stream().mapToInt(e -> e.getCourse().getCredits()).sum();
        if (totalCredits == 0) return 0.0;
        int weighted = list.stream()
                .filter(e -> e.getGrade() != null)
                .mapToInt(e -> e.getGrade().getPoints() * e.getCourse().getCredits())
                .sum();
        return totalCredits == 0 ? 0.0 : (double) weighted / totalCredits;
    }

    public Map<Integer, Long> gpaDistribution() {
        return studentService.all().stream().collect(Collectors.groupingBy(s -> (int)Math.round(computeGPA(s)), Collectors.counting()));
    }
}
