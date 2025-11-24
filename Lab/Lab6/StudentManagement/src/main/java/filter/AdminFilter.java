package filter;

import model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Admin Filter - Checks if user has admin role
 * Protects admin-only pages
 */
@WebFilter(filterName = "AdminFilter", urlPatterns = {"/student"})
public class AdminFilter implements Filter {
    
    // Admin-only actions
    private static final String[] ADMIN_ACTIONS = {
        "new",
        "insert",
        "edit",
        "update",
        "delete"
    };
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("AdminFilter initialized");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String action = httpRequest.getParameter("action");
        
        System.out.println("--- AdminFilter Debug ---");
        System.out.println("Request Action: " + action);
        System.out.println("Is Admin Action? " + isAdminAction(action));
        
        // Check if this action requires admin role
        if (isAdminAction(action)) {
            HttpSession session = httpRequest.getSession(false);
            
            if (session != null) {
                User user = (User) session.getAttribute("user");
                
                if (user != null && user.isAdmin()) {
                    // User is admin, allow access
                    chain.doFilter(request, response);
                } else {
                    // User is not admin, deny access
                    httpResponse.sendRedirect(httpRequest.getContextPath() + 
                        "/student?action=list&error=Access denied. Admin privileges required.");
                }
            } else {
                // No session, redirect to login
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            }
        } else {
            // Not an admin action, allow access
            chain.doFilter(request, response);
        }
    }
    
    @Override
    public void destroy() {
        System.out.println("AdminFilter destroyed");
    }
    
    /**
     * Check if action requires admin role
     */
    private boolean isAdminAction(String action) {
        if (action == null) return false;
        
        for (String adminAction : ADMIN_ACTIONS) {
            if (adminAction.equals(action)) {
                return true;
            }
        }
        return false;
    }
}
