import java.io.*;
import java.util.*;
import java.time.*;

// Interface for displayable entities
interface Manageable {
    void displayInfo();
}

// Base class Person
class Person {
    protected String name;

    public Person(String name) {
        this.name = name;
    }
}

// Student class
class Student extends Person implements Manageable {
    private String studentId;
    private String year;
    private String semester;
    private Set<String> attendanceDates; // stores dates in YYYY-MM-DD

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
        return studentId + "," + name + "," + year + "," + semester + "," + String.join(";", attendanceDates);
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
}

// File handling for students
class FileHandler {
    private static final String FILE_NAME = "students.txt";

    public static void saveAllStudents(List<Student> students) {
        try (FileWriter fw = new FileWriter(FILE_NAME, false)) {
            for (Student s : students) {
                fw.write(s.getData() + "\n");
            }
        } catch (IOException e) {
            System.out.println("File error");
        }
    }

    public static List<Student> readStudentsFromFile() {
        List<Student> students = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                Student s = Student.fromData(line);
                if (s != null) students.add(s);
            }
        } catch (IOException e) {
            System.out.println("No records found");
        }
        return students;
    }

    public static void displayAllStudents() {
        List<Student> students = readStudentsFromFile();
        if (students.isEmpty()) {
            System.out.println("No students found.");
        } else {
            for (Student s : students) {
                s.displayInfo();
                System.out.println("--------------------");
            }
        }
    }

    public static void markAttendance(String studentId) {
        List<Student> students = readStudentsFromFile();
        boolean found = false;
        String today = LocalDate.now().toString();
        for (Student s : students) {
            if (s.getStudentId().equals(studentId)) {
                s.markAttendance(today);
                found = true;
                break;
            }
        }
        if (found) saveAllStudents(students);
        else System.out.println("Student ID not found.");
    }

    public static void showMonthlyReport(int year, int month) {
        List<Student> students = readStudentsFromFile();
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        System.out.println("Attendance Report for " + YearMonth.of(year, month));
        for (Student s : students) {
            double percent = s.getMonthlyAttendancePercentage(year, month);
            System.out.printf("%s (%s): %.2f%%\n", s.name, s.getStudentId(), percent);
        }
    }
}

// Auto-save simulation thread
class AutoSaveThread extends Thread {
    public void run() {
        try {
            while (true) {
                Thread.sleep(10000);
                System.out.println("Auto save running...");
            }
        } catch (InterruptedException e) {
            System.out.println("Thread stopped");
        }
    }
}

