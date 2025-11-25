package edu.ccrm.cli;

import edu.ccrm.config.AppConfig;
import edu.ccrm.domain.Course;
import edu.ccrm.domain.Instructor;
import edu.ccrm.domain.Semester;
import edu.ccrm.domain.Student;
import edu.ccrm.io.BackupService;
import edu.ccrm.io.ImportExportService;
import edu.ccrm.service.CourseService;
import edu.ccrm.service.EnrollmentService;
import edu.ccrm.service.StudentService;
import edu.ccrm.util.RecursionUtil;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {
    private static final StudentService studentService = new StudentService();
    private static final CourseService courseService = new CourseService();
    private static final EnrollmentService enrollmentService = new EnrollmentService(studentService, courseService);
    private static final ImportExportService ioService = new ImportExportService(studentService, courseService, enrollmentService);
    private static final BackupService backupService = new BackupService();

    public static void main(String[] args) throws Exception {
        AppConfig.getInstance().setDataFolder("data");
        setupFolders();

        seedDemoData();

        Scanner sc = new Scanner(System.in);
        outer:
        while (true) {
            System.out.println("\n=== CCRM Menu ===");
            System.out.println("1. Manage Students");
            System.out.println("2. Manage Courses");
            System.out.println("3. Enroll / Record Grades");
            System.out.println("4. Import / Export Data");
            System.out.println("5. Backup & Show Backup Size");
            System.out.println("6. Reports (GPA distribution)");
            System.out.println("7. Exit");
            System.out.print("Choice: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> manageStudents(sc);
                case "2" -> manageCourses(sc);
                case "3" -> manageEnrollments(sc);
                case "4" -> importExport(sc);
                case "5" -> backupAndSize();
                case "6" -> reports();
                case "7" -> {
                    System.out.println("Bye.");
                    break outer;
                }
                default -> System.out.println("Invalid option.");
            }
        }
        sc.close();
    }

    private static void setupFolders() throws Exception {
        Path data = Path.of(AppConfig.getInstance().getDataFolder());
        if (!Files.exists(data)) Files.createDirectories(data);
        Path screenshots = data.resolve("screenshots");
        if (!Files.exists(screenshots)) Files.createDirectories(screenshots);
    }

    private static void seedDemoData() {
        Instructor i1 = new Instructor("I-01","Dr. Rekha","rekha@univ.edu","CSE");
        Instructor i2 = new Instructor("I-02","Prof. Kumar","kumar@univ.edu","ECE");
        courseService.addCourse(new Course.Builder().code("CS101").title("Intro to CS").credits(4).instructor(i1).semester(Semester.FALL).department("CSE").build());
        courseService.addCourse(new Course.Builder().code("EC201").title("Signals").credits(3).instructor(i2).semester(Semester.SPRING).department("ECE").build());
        studentService.addStudent(new Student("S-001","Ankit Singh","ankit@uni.edu","24BCE10024"));
        studentService.addStudent(new Student("S-002","Priya Patel","priya@uni.edu","24BCE10025"));
    }

    private static void manageStudents(Scanner sc) {
        System.out.println("Student Menu: a) Add b) List c) Print Profile d) Back");
        String cmd = sc.nextLine().trim();
        switch (cmd) {
            case "a" -> {
                System.out.print("id: "); String id = sc.nextLine();
                System.out.print("fullName: "); String fn = sc.nextLine();
                System.out.print("email: "); String em = sc.nextLine();
                System.out.print("regNo: "); String reg = sc.nextLine();
                studentService.addStudent(new Student(id, fn, em, reg));
                System.out.println("Added.");
            }
            case "b" -> studentService.listStudents();
            case "c" -> {
                System.out.print("student id: ");
                String sid = sc.nextLine();
                var s = studentService.findById(sid);
                if (s != null) System.out.println(s.detailedProfile());
                else System.out.println("Not found.");
            }
            default -> {
                System.out.println("Back.");
                break;
            }
        }
    }

    private static void manageCourses(Scanner sc) {
        System.out.println("Course Menu: a) Add b) List c) Search by dept d) Back");
        String cmd = sc.nextLine().trim();
        switch (cmd) {
            case "a" -> {
                System.out.print("code: "); String code = sc.nextLine();
                System.out.print("title: "); String title = sc.nextLine();
                System.out.print("credits: "); int credits = Integer.parseInt(sc.nextLine());
                System.out.print("instructor id (create new if blank): "); String iid = sc.nextLine();
                System.out.print("semester (SPRING/SUMMER/FALL): "); Semester sem = Semester.valueOf(sc.nextLine().trim().toUpperCase());
                System.out.print("department: "); String dept = sc.nextLine();
                Instructor instr = new Instructor("I-"+System.currentTimeMillis()%1000, "TBD", "tbd@u.edu", dept);
                Course c = new Course.Builder().code(code).title(title).credits(credits).instructor(instr).semester(sem).department(dept).build();
                courseService.addCourse(c);
                System.out.println("Course added.");
            }
            case "b" -> courseService.listCourses();
            case "c" -> {
                System.out.print("department: "); String d = sc.nextLine();
                courseService.searchByDepartment(d).forEach(System.out::println);
            }
            default -> System.out.println("Back.");
        }
    }

    private static void manageEnrollments(Scanner sc) {
        System.out.println("Enroll Menu: a) Enroll b) Unenroll c) Record Grade d) Print Transcript e) Back");
        String cmd = sc.nextLine().trim();
        try {
            switch (cmd) {
                case "a" -> {
                    System.out.print("student id: "); String sid = sc.nextLine();
                    System.out.print("course code: "); String cc = sc.nextLine();
                    enrollmentService.enroll(sid, cc);
                    System.out.println("Enrolled.");
                }
                case "b" -> {
                    System.out.print("student id: "); String sid = sc.nextLine();
                    System.out.print("course code: "); String cc = sc.nextLine();
                    enrollmentService.unenroll(sid, cc);
                    System.out.println("Unenrolled.");
                }
                case "c" -> {
                    System.out.print("student id: "); String sid = sc.nextLine();
                    System.out.print("course code: "); String cc = sc.nextLine();
                    System.out.print("grade (S/A/B/C/D/E/F): "); String g = sc.nextLine();
                    enrollmentService.recordGrade(sid, cc, g);
                    System.out.println("Grade recorded.");
                }
                case "d" -> {
                    System.out.print("student id: "); String sid = sc.nextLine();
                    System.out.println(enrollmentService.getTranscript(sid));
                }
                default -> System.out.println("Back.");
            }
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private static void importExport(Scanner sc) {
        System.out.println("IO: a) Export b) Import c) Back");
        String cmd = sc.nextLine().trim();
        switch (cmd) {
            case "a" -> ioService.exportAll();
            case "b" -> ioService.importAll();
            default -> System.out.println("Back.");
        }
    }

    private static void backupAndSize() throws Exception {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String src = AppConfig.getInstance().getDataFolder();
        String target = src + "/backup_" + timestamp;
        backupService.backupData(src, target);
        long size = RecursionUtil.getDirectorySize(Path.of(target).toFile());
        System.out.println("Backup created at: " + target);
        System.out.println("Size (bytes): " + size);
    }

    private static void reports() {
        System.out.println("GPA Distribution:");
        enrollmentService.gpaDistribution().forEach((k,v) -> System.out.println(k + " -> " + v));
    }
}
