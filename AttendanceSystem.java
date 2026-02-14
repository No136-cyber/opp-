import java.util.List;
import java.util.Scanner;

// Main class
public class AttendanceSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        AutoSaveThread t = new AutoSaveThread();
        t.start();

        while (true) {
            try {
                System.out.println("\n1. Add Student");
                System.out.println("2. View Students");
                System.out.println("3. Mark Attendance");
                System.out.println("4. Monthly Attendance Report");
                System.out.println("5. Exit");
                System.out.print("Enter choice: ");

                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1:
                        System.out.print("Student ID: ");
                        String id = sc.nextLine();
                        System.out.print("Name: ");
                        String name = sc.nextLine();
                        System.out.print("Year: ");
                        String year = sc.nextLine();
                        System.out.print("Semester: ");
                        String semester = sc.nextLine();

                        Student s = new Student(id, name, year, semester);
                        List<Student> students = FileHandler.readStudentsFromFile();
                        students.add(s);
                        FileHandler.saveAllStudents(students);
                        System.out.println("Student added.");
                        break;

                    case 2:
                        FileHandler.displayAllStudents();
                        break;

                    case 3:
                        System.out.print("Enter Student ID: ");
                        String sid = sc.nextLine();
                        FileHandler.markAttendance(sid);
                        break;

                    case 4:
                        System.out.print("Enter Year (YYYY): ");
                        int reportYear = sc.nextInt();
                        System.out.print("Enter Month (1-12): ");
                        int reportMonth = sc.nextInt();
                        sc.nextLine();
                        FileHandler.showMonthlyReport(reportYear, reportMonth);
                        break;

                    case 5:
                        System.exit(0);
                        break;

                    default:
                        System.out.println("Invalid choice");
                        break;
                }
            } catch (Exception e) {
                System.out.println("Invalid input");
                sc.nextLine();
            }
        }
    }
}
