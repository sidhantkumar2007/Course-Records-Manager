package edu.ccrm.domain;

public class Instructor extends Person {
    private String department;

    public Instructor(String id, String fullName, String email, String department) {
        super(id, fullName, email);
        this.department = department;
    }

    public String getDepartment() { return department; }
    @Override
    public void printProfile() {
        System.out.println("Instructor: " + fullName + " | dept: " + department);
    }
}
