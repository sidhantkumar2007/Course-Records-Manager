package edu.ccrm.service;

import edu.ccrm.domain.Student;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StudentService {
    private final Map<String, Student> store = new LinkedHashMap<>();

    public void addStudent(Student s) {
        store.put(s.getId(), s);
    }

    public Student findById(String id) {
        return store.get(id);
    }

    public List<Student> all() {
        return new ArrayList<>(store.values());
    }

    public void listStudents() {
        store.values().forEach(Student::printProfile);
    }

    public List<Student> filter(Predicate<Student> p) {
        return store.values().stream().filter(p).collect(Collectors.toList());
    }
}
