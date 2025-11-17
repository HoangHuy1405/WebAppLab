package test;


import dao.StudentDAO;
import model.Student;
import java.util.List;


public class Test {
    public static void main(String[] args) {
        StudentDAO dao = new StudentDAO();
        List<Student> results = dao.searchStudents("john");

        System.out.println("Found " + results.size() + " students");
        for (Student s : results) {
            System.out.println(s);
        }
    }
}
