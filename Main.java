import java.io.*;
import java.util.*;
import java.time.*;

// Interface
interface Manageable {
    void displayInfo();
}

// Base class
class Person {
    protected String name;

    public Person(String name) {
        this.name = name;
    }
}

// Student class
class Student extends Person implements Manageable {
    private final String studentId;
    private final String year;
    private final String semester;
    private final Set<String> attendanceDates;

    public Student(String studentId, String name, String year, String semester) {
        super(name);
        this.studentId = studentId;
        this.year = year;
        this.semester = semester;
        this.attendanceDates = new HashSet<>();
    }

    @Override
    public void displayInfo() {
        System.out.println("Student ID: " + studentId);
        System.out.println("Name: " + name);
        System.out.println("Year: " + year);
        System.out.println("Semester: " + semester);
        System.out.println("Total Attendance Days: " + attendanceDates.size());
    }

    public void markAttendance(String date) {
        if (attendanceDates.add(date)) {
            System.out.println("Attendance marked for " + date);
        } else {
            System.out.println("Attendance already marked for today.");
        }
    }

    public double getMonthlyAttendancePercentage(int year, int month) {
        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();
        long count = attendanceDates.stream()
                .filter(d -> {
                    LocalDate ld = LocalDate.parse(d);
                    return ld.getYear() == year && ld.getMonthValue() == month;
                }).count();
        return ((double) count / daysInMonth) * 100;
    }

    public String getData() {
        return studentId + "," + name + "," + year + "," + semester + "," +
                String.join(";", attendanceDates);
    }

    public static Student fromData(String line) {
        String[] parts = line.split(",", 5);
        if (parts.length < 5) return null;

        Student s = new Student(parts[0], parts[1], parts[2], parts[3]);

        if (!parts[4].isEmpty()) {
            String[] dates = parts[4].split(";");
            s.attendanceDates.addAll(Arrays.asList(dates));
        }
        return s;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }
}

// File handling
class FileHandler {
    private static final String FILE_NAME = "students.txt";

    public static void saveAllStudents(List<Student> students) {
        try (FileWriter fw = new FileWriter(FILE_NAME, false)) {
            for (Student s : students) {
                fw.write(s.getData() + "\n");
            }
        } catch (IOException e) {
            System.out.println("File error while saving.");
        }
    }

    public static List<Student> readStudentsFromFile() {
        List<Student> students = new ArrayList<>();

        File file = new File(FILE_NAME);
        if (!file.exists()) return students;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                Student s = Student.fromData(line);
                if (s != null) students.add(s);
            }
        } catch (IOException e) {
            System.out.println("File error while reading.");
        }
        return students;
    }
}

// Auto Save Thread
class AutoSaveThread extends Thread {
    public void run() {
        try {
            while (!isInterrupted()) {
                Thread.sleep(10000);
                System.out.println("Auto save running...");
            }
        } catch (InterruptedException e) {
            System.out.println("Auto save stopped.");
        }
    }
}

// Main class
public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        List<Student> students = FileHandler.readStudentsFromFile();

        AutoSaveThread autoSave = new AutoSaveThread();
        autoSave.start();

        while (true) {
            System.out.println("\n===== Student Attendance System =====");
            System.out.println("1. Add Student");
            System.out.println("2. Display All Students");
            System.out.println("3. Mark Attendance");
            System.out.println("4. Monthly Report");
            System.out.println("5. Exit");
            System.out.print("Choose option: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    System.out.print("Enter Student ID: ");
                    String id = sc.nextLine();

                    System.out.print("Enter Name: ");
                    String name = sc.nextLine();

                    System.out.print("Enter Year: ");
                    String year = sc.nextLine();

                    System.out.print("Enter Semester: ");
                    String sem = sc.nextLine();

                    Student newStudent = new Student(id, name, year, sem);
                    students.add(newStudent);
                    FileHandler.saveAllStudents(students);
                    System.out.println("Student added successfully.");
                    break;

                case 2:
                    if (students.isEmpty()) {
                        System.out.println("No students found.");
                    } else {
                        for (Student s : students) {
                            s.displayInfo();
                            System.out.println("--------------------");
                        }
                    }
                    break;

                case 3:
                    System.out.print("Enter Student ID: ");
                    String sid = sc.nextLine();

                    boolean found = false;
                    String today = LocalDate.now().toString();

                    for (Student s : students) {
                        if (s.getStudentId().equals(sid)) {
                            s.markAttendance(today);
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        System.out.println("Student not found.");
                    }

                    FileHandler.saveAllStudents(students);
                    break;

                case 4:
                    System.out.print("Enter Year (YYYY): ");
                    int y = sc.nextInt();

                    System.out.print("Enter Month (1-12): ");
                    int m = sc.nextInt();

                    for (Student s : students) {
                        double percent = s.getMonthlyAttendancePercentage(y, m);
                        System.out.printf("%s (%s): %.2f%%\n",
                                s.getName(),
                                s.getStudentId(),
                                percent);
                    }
                    break;

                case 5:
                    autoSave.interrupt();
                    System.out.println("Exiting system...");
                    return;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}
