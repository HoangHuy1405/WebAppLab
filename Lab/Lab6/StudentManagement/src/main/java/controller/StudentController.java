package controller;

import dao.StudentDAO;
import model.Student;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/student")
public class StudentController extends HttpServlet {
    
    private StudentDAO studentDAO;
    
    @Override
    public void init() {
        studentDAO = new StudentDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if (action == null) {
            action = "list";
        }
        
        switch (action) {
            case "list":
                listStudents(request, response);
                break;
            case "new":
                showNewForm(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deleteStudent(request, response);
                break;
            case "search":  
                searchStudents(request, response);
                break;
            case "sort":
                sortStudents(request, response);
                break;
            case "filter":
                filterStudents(request, response);
                break;
            default:
                listStudents(request, response);
                break;
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if (action == null) {
            action = "insert";
        }
        
        switch (action) {
            case "insert":
                insertStudent(request, response);
                break;
            case "update":
                updateStudent(request, response);
                break;
            default:
                listStudents(request, response);
                break;
        }
    }
    
    private void listStudents(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String pageParam = request.getParameter("page");
        int currentPage = 1;

        try {
            if (pageParam != null) {
                currentPage = Integer.parseInt(pageParam);
            }
        } catch (NumberFormatException e) {
            currentPage = 1; // fallback
        }

        if (currentPage < 1) {
            currentPage = 1;
        }

        // Calculate records per page and offset
        int recordsPerPage = 10;
        // handle edges cases
        int offset = (currentPage - 1) * recordsPerPage;

        // Get paginated list + total count
        List<Student> students = studentDAO.getStudentsPaginated(offset, recordsPerPage);
        int totalRecords = studentDAO.getTotalStudents();

        int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);

        if (currentPage > totalPages && totalPages > 0) {
            currentPage = totalPages; // prevent going beyond last page
        }

        request.setAttribute("students", students);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("recordsPerPage", recordsPerPage);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
        dispatcher.forward(request, response);
    }
    
    private void showNewForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
        dispatcher.forward(request, response);
    }
    
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        Student student = studentDAO.getStudentById(id);
        
        request.setAttribute("student", student);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
        dispatcher.forward(request, response);
    }
    
    private void insertStudent(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String studentCode = request.getParameter("studentCode");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String major = request.getParameter("major");
        
        Student student = new Student(studentCode, fullName, email, major);
        
        if (!validateStudent(student, request)) {
            request.setAttribute("student", student); // Preserve entered data
            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
            dispatcher.forward(request, response);
            return; 
        }
        
        if (studentDAO.addStudent(student)) {
            response.sendRedirect("student?action=list&message=Student added successfully");
        } else {
            response.sendRedirect("student?action=new&error=Failed to add student");
        }
    }
    
    private void updateStudent(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String major = request.getParameter("major");
        
        Student student = new Student();
        student.setId(id);
        student.setFullName(fullName);
        student.setEmail(email);
        student.setMajor(major);
        
        if (!validateStudent(student, request)) {
            request.setAttribute("student", student);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
            dispatcher.forward(request, response);
            return;
        }
        
        if (studentDAO.updateStudent(student)) {
            response.sendRedirect("student?action=list&message=Student updated successfully");
        } else {
            response.sendRedirect("student?action=edit&id=" + id + "&error=Failed to update");
        }
    }
    
    private void deleteStudent(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        
        if (studentDAO.deleteStudent(id)) {
            response.sendRedirect("student?action=list&message=Student deleted successfully");
        } else {
            response.sendRedirect("student?action=list&error=Failed to delete student");
        }
    }
    
    private void searchStudents(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String keyword = request.getParameter("keyword");

        List<Student> students;
        if (keyword == null || keyword.trim().isEmpty()) {
            students = studentDAO.getAllStudents();
            keyword = ""; // reset
        } else {
            students = studentDAO.searchStudents(keyword);
        }

        request.setAttribute("students", students);
        request.setAttribute("keyword", keyword);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
        dispatcher.forward(request, response);
    }
    
    private boolean validateStudent(Student student, HttpServletRequest request) {
        boolean isValid = true;

        String code = student.getStudentCode();
        if (code == null || code.trim().isEmpty()) {
            request.setAttribute("errorCode", "Student code is required");
            isValid = false;
        } else {
            code = code.trim();
            student.setStudentCode(code); 

            String codePattern = "^[A-Z]{2}[0-9]{3,}$";
            if (!code.matches(codePattern)) {
                request.setAttribute("errorCode", "Invalid format. Use 2 letters + 3+ digits (e.g., SV001)");
                isValid = false;
            }
        }

        String fullName = student.getFullName();
        if (fullName == null || fullName.trim().isEmpty()) {
            request.setAttribute("errorName", "Full name is required");
            isValid = false;
        } else {
            fullName = fullName.trim();
            student.setFullName(fullName); // save trimmed value

            if (fullName.length() < 2) {
                request.setAttribute("errorName", "Full name must be at least 2 characters");
                isValid = false;
            }
        }

        String email = student.getEmail();
        if (email != null) {
            email = email.trim();
            student.setEmail(email); 
        }

        if (email != null && !email.isEmpty()) {
            String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";
            if (!email.matches(emailPattern)) {
                request.setAttribute("errorEmail", "Invalid email format");
                isValid = false;
            }
        }

        String major = student.getMajor();
        if (major == null || major.trim().isEmpty()) {
            request.setAttribute("errorMajor", "Major is required");
            isValid = false;
        } else {
            major = major.trim();
            student.setMajor(major); 
        }

        return isValid;
    }
    
    private void sortStudents(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String sortBy = request.getParameter("sortBy");
        String order = request.getParameter("order");

        List<Student> students = studentDAO.getStudentsSorted(sortBy, order);

        request.setAttribute("students", students);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("order", order);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
        dispatcher.forward(request, response);
    }
    
    private void filterStudents(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        String major = request.getParameter("major");
        List<Student> students;

        if (major == null || major.trim().isEmpty()) {
            // No filter => show all
            students = studentDAO.getAllStudents();
            major = "";
        } else {
            students = studentDAO.getStudentsByMajor(major);
        }

        request.setAttribute("students", students);
        request.setAttribute("selectedMajor", major);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
        dispatcher.forward(request, response);
    }
}
