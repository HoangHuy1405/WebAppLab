<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    // 1. Get theme from cookie, default to 'light'
    String currentTheme = "light";
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (Cookie cookie : cookies) {
            if ("user_theme".equals(cookie.getName())) {
                currentTheme = cookie.getValue();
                break;
            }
        }
    }
%>

<!DOCTYPE html>
<!-- KEY FIX: Add the theme attribute here so CSS can detect it -->
<html lang="en" data-bs-theme="<%= currentTheme %>">
<head>
    <meta charset="UTF-8">
    <title>Dashboard</title>
    
    <!-- Bootstrap CSS (Required for the Dropdown and Icons) -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">

    <style>
        /* --- THEME VARIABLES --- */
        /* Define colors using variables so they can switch automatically */
        :root, [data-bs-theme="light"] {
            --bg-body: #f5f5f5;
            --bg-card: #ffffff;
            --bg-navbar: #2c3e50;
            --text-primary: #2c3e50;
            --text-secondary: #7f8c8d;
            --text-navbar: #ffffff;
            --shadow: rgba(0,0,0,0.1);
        }

        [data-bs-theme="dark"] {
            --bg-body: #121212;
            --bg-card: #1e1e1e;
            --bg-navbar: #0f172a;
            --text-primary: #e0e0e0;
            --text-secondary: #a0a0a0;
            --text-navbar: #e0e0e0;
            --shadow: rgba(0,0,0,0.5);
        }

        /* --- BASE STYLES USING VARIABLES --- */
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: var(--bg-body);       /* Use Variable */
            color: var(--text-primary);       /* Use Variable */
            transition: background 0.3s, color 0.3s; /* Smooth transition */
        }
        
        /* Note: Using .custom-navbar to avoid conflict with Bootstrap .navbar */
        .custom-navbar {
            background: var(--bg-navbar);     /* Use Variable */
            color: var(--text-navbar);        /* Use Variable */
            padding: 15px 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .custom-navbar h2 {
            font-size: 20px;
            margin: 0;
        }
        
        .navbar-right {
            display: flex;
            align-items: center;
            gap: 20px;
        }
        
        .user-info {
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        /* Role Badges */
        .role-badge {
            padding: 4px 12px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 600;
        }
        .role-admin { background: #e74c3c; color: white; }
        .role-user { background: #3498db; color: white; }
        
        /* Logout Button */
        .btn-logout {
            padding: 8px 20px;
            background: #e74c3c;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            font-size: 14px;
            transition: background 0.3s;
            border: none;
        }
        .btn-logout:hover { background: #c0392b; color: white; }
        
        .container-custom {
            max-width: 1200px;
            margin: 30px auto;
            padding: 0 20px;
        }
        
        /* --- CARDS (Updated to use variables) --- */
        .welcome-card, .stat-card, .quick-actions {
            background: var(--bg-card);       /* Use Variable */
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px var(--shadow); /* Use Variable */
            margin-bottom: 30px;
        }
        
        .welcome-card h1 {
            color: var(--text-primary);       /* Use Variable */
            margin-bottom: 10px;
        }
        
        .welcome-card p, .stat-content p {
            color: var(--text-secondary);     /* Use Variable */
        }
        
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
        }
        
        .stat-card {
            display: flex;
            align-items: center;
            gap: 20px;
            padding: 25px;
        }
        
        .stat-icon {
            font-size: 40px;
            width: 60px;
            height: 60px;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 10px;
        }
        .stat-icon-students { background: #e8f4fd; }
        
        .stat-content h3 {
            font-size: 28px;
            color: var(--text-primary);       /* Use Variable */
            margin-bottom: 5px;
        }
        
        .quick-actions h2 {
            color: var(--text-primary);       /* Use Variable */
            margin-bottom: 20px;
        }
        
        .action-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
        }
        
        .action-btn {
            padding: 20px;
            color: white;
            text-decoration: none;
            border-radius: 8px;
            text-align: center;
            transition: all 0.3s;
            display: block;
        }
        .action-btn:hover { transform: translateY(-2px); color: white; }
        
        .action-btn-primary { background: #3498db; }
        .action-btn-success { background: #27ae60; }
        .action-btn-warning { background: #f39c12; }
        
        /* Override Bootstrap dropdown button to match your theme */
        .btn-theme-toggle {
            color: var(--text-navbar);
            border: 1px solid rgba(255,255,255,0.3);
        }
        .btn-theme-toggle:hover {
            background: rgba(255,255,255,0.1);
            color: var(--text-navbar);
        }
    </style>
</head>
<body>
    <!-- Navigation Bar -->
    <div class="custom-navbar">
        <h2>📚 Student Management System</h2>
        <div class="navbar-right">
            <div class="user-info">
                <span>${sessionScope.fullName}</span>
                <span class="role-badge role-${sessionScope.role}">
                    ${sessionScope.role}
                </span>
            </div>
            
            <!-- Bootstrap Dropdown for Theme -->
            <div class="dropdown">
                <button class="btn btn-theme-toggle btn-sm dropdown-toggle" 
                        type="button" 
                        id="themeDropdown" 
                        data-bs-toggle="dropdown" 
                        aria-expanded="false">
                    <i class="bi bi-palette"></i> 
                    Theme: <%= currentTheme.substring(0, 1).toUpperCase() + currentTheme.substring(1) %>
                </button>
                <ul class="dropdown-menu dropdown-menu-end shadow" aria-labelledby="themeDropdown">
                    <li>
                        <a class="dropdown-item <%= "light".equals(currentTheme) ? "active" : "" %>" 
                           href="theme?mode=light">
                            <i class="bi bi-sun"></i> Light
                        </a>
                    </li>
                    <li>
                        <a class="dropdown-item <%= "dark".equals(currentTheme) ? "active" : "" %>" 
                           href="theme?mode=dark">
                            <i class="bi bi-moon-stars"></i> Dark
                        </a>
                    </li>
                </ul>
            </div>
            
            <a href="logout" class="btn-logout">Logout</a>
        </div>
    </div>
    
    <!-- Main Content -->
    <div class="container-custom">
        <!-- Welcome Card -->
        <div class="welcome-card">
            <h1>${welcomeMessage}</h1>
            <p>Here's what's happening with your students today.</p>
        </div>
        
        <!-- Statistics -->
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon stat-icon-students">
                    👨🎓
                </div>
                <div class="stat-content">
                    <h3>${totalStudents}</h3>
                    <p>Total Students</p>
                </div>
            </div>
        </div>
        
        <!-- Quick Actions -->
        <div class="quick-actions">
            <h2>Quick Actions</h2>
            <div class="action-grid">
                <a href="student?action=list" class="action-btn action-btn-primary">
                    📋 View All Students
                </a>
                
                <c:if test="${sessionScope.role eq 'admin'}">
                    <a href="student?action=new" class="action-btn action-btn-success">
                        ➕ Add New Student
                    </a>
                </c:if>
                
                <a href="student?action=search" class="action-btn action-btn-warning">
                    🔍 Search Students
                </a>
            </div>
        </div>
    </div>

    <!-- Bootstrap JS (Required for Dropdown) -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>