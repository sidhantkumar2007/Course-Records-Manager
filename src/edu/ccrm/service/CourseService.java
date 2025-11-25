package edu.ccrm.service;

import edu.ccrm.domain.Course;
import java.util.*;
import java.util.stream.Collectors;

public class CourseService {
    private final Map<String, Course> store = new LinkedHashMap<>();

    public void addCourse(Course c) {
        store.put(c.getCode(), c);
    }

    public Course findByCode(String code) {
        return store.get(code);
    }

    public List<Course> all() {
        return new ArrayList<>(store.values());
    }

    public void listCourses() {
        store.values().forEach(System.out::println);
    }

    public List<Course> searchByDepartment(String dept) {
        return store.values().stream().filter(c -> c.getDepartment().equalsIgnoreCase(dept)).collect(Collectors.toList());
    }

    public List<Course> filterByInstructor(String instrName) {
        return store.values().stream().filter(c -> c.getInstructor() != null && c.getInstructor().getFullName().contains(instrName)).collect(Collectors.toList());
    }
}
