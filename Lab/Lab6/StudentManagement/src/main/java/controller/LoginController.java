package controller;

import dao.UserDAO;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;
import javax.servlet.http.Cookie;

@WebServlet("/login")
public class LoginController extends HttpServlet {
    
    private UserDAO userDAO;
    
    @Override
    public void init() {
        userDAO = new UserDAO();
    }
    
    /**
     * Display login page
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // If already logged in, redirect to dashboard
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect("dashboard");
            return;
        }
        
        // Show login page
        request.getRequestDispatcher("/views/login.jsp").forward(request, response);
    }
    
    /**
     * Process login form
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        // Use "rememberMe" to match the JSP checkbox name
        String rememberMe = request.getParameter("rememberMe");
        
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Username and password are required");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
            return;
        }
        
        User user = userDAO.authenticate(username, password);
        
        if (user != null) {
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) oldSession.invalidate();
            
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("role", user.getRole());
            session.setAttribute("fullName", user.getFullName());
            session.setMaxInactiveInterval(30 * 60);
            
            // --- REMEMBER ME IMPLEMENTATION ---
            if ("on".equals(rememberMe)) {
                // 1. Generate secure random token
                String token = UUID.randomUUID().toString();
                
                // 2. Save to DB
                userDAO.saveRememberToken(user.getId(), token);
                
                // 3. Create Cookie
                Cookie rememberCookie = new Cookie("remember_token", token);
                rememberCookie.setMaxAge(30 * 24 * 60 * 60); // 30 days
                rememberCookie.setPath("/"); // Available for entire app
                rememberCookie.setHttpOnly(true); // Secure against XSS
                response.addCookie(rememberCookie);
            }
            
            if (user.isAdmin()) {
                response.sendRedirect("dashboard");
            } else {
                response.sendRedirect("student?action=list");
            }
            
        } else {
            request.setAttribute("error", "Invalid username or password");
            request.setAttribute("username", username);
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
        }
    }
}
