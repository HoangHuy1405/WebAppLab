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
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/change-password")
public class ChangePasswordController extends HttpServlet {
    
    private UserDAO userDAO;
    
    @Override
    public void init() {
        userDAO = new UserDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Protect the page: If no user in session, send to login
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Show change password form
        request.getRequestDispatcher("/views/change-password.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        
        if (user == null) {
            response.sendRedirect("login");
            return;
        }

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        String error = null;

        // 1. Basic Validation
        if (currentPassword == null || newPassword == null || confirmPassword == null || 
            currentPassword.isEmpty() || newPassword.isEmpty()) {
            error = "All fields are required.";
        } 
        else if (newPassword.length() < 8) {
            error = "New password must be at least 8 characters.";
        }
        else if (!newPassword.equals(confirmPassword)) {
            error = "New password and Confirm password do not match.";
        }
        else {
            // 2. Verify Current Password using BCrypt
            // We retrieve the HASH from the database, then ask BCrypt to compare them
            String storedHash = userDAO.getPasswordById(user.getId());
            
            if (storedHash == null || !BCrypt.checkpw(currentPassword, storedHash)) {
                error = "Incorrect current password.";
            }
        }

        // 3. Update Password if no errors
        if (error == null) {
            // Hash the NEW password using BCrypt
            String hashedNewPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            
            boolean isUpdated = userDAO.updatePassword(user.getId(), hashedNewPassword);
            
            if (isUpdated) {
                request.setAttribute("message", "Password changed successfully!");
            } else {
                error = "Database error. Could not update password.";
            }
        }

        if (error != null) {
            request.setAttribute("error", error);
        }
        
        request.getRequestDispatcher("/views/change-password.jsp").forward(request, response);
    }
    
    
}