package controller;

import dao.StudentDAO;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import util.CookieUtil;

@WebServlet("/dashboard")
public class DashboardController extends HttpServlet {
    
    private StudentDAO studentDAO;
    
    @Override
    public void init() {
        studentDAO = new StudentDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        CookieUtil.createCookie(response, "theme", "dark", 7*24*60*60); // 7 days

        // Read cookie
        String theme = CookieUtil.getCookieValue(request, "theme");
        System.out.println("Current theme: " + theme);

        // Check if cookie exists
        if (CookieUtil.hasCookie(request, "theme")) {
            System.out.println("Theme cookie exists");
        }

        // Update cookie
        CookieUtil.updateCookie(response, "theme", "light", 7*24*60*60);
        
        // Get user from session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        
        // Get statistics
        int totalStudents = studentDAO.getTotalStudents();
        
        // Set attributes
        request.setAttribute("totalStudents", totalStudents);
        request.setAttribute("welcomeMessage", "Welcome back, " + user.getFullName() + "!");
        
        // Forward to dashboard
        request.getRequestDispatcher("/views/dashboard.jsp").forward(request, response);
        
    }
}
