<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Student List</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f5f5f5;
        }
        h1 { color: #333; }
        .message {
            padding: 10px;
            margin-bottom: 20px;
            border-radius: 5px;
        }
        .success {
            background-color: #d4edda;
            color: #155724;
        }
        .error {
            background-color: #f8d7da;
            color: #721c24;
        }
        .btn {
            display: inline-block;
            padding: 10px 20px;
            margin-bottom: 20px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 5px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            background-color: white;
        }
        th {
            background-color: #007bff;
            color: white;
            padding: 12px;
            text-align: left;
        }
        td {
            padding: 10px;
            border-bottom: 1px solid #ddd;
        }
        tr:hover { background-color: #f8f9fa; }
        .action-link {
            color: #007bff;
            text-decoration: none;
            margin-right: 10px;
        }
        .delete-link { color: #dc3545; }
        .pagination {
            margin: 20px 0;
            text-align: center;
        }
        .pagination a {
            padding: 8px 12px;
            margin: 0 4px;
            border: 1px solid #ddd;
            background-color: white;
            text-decoration: none;
            border-radius: 4px;
            color: #007bff;
        }

        .pagination a:hover {
            background-color: #f1f1f1;
        }

        .pagination strong {
            padding: 8px 12px;
            margin: 0 4px;
            background-color: #007bff;
            color: white;
            border: 1px solid #007bff;
            border-radius: 4px;
        }
        .navbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            background-color: #ffffff;
            padding: 15px 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            box-shadow: 0px 2px 6px rgba(0,0,0,0.1);
        }

        .navbar-right {
            display: flex;
            align-items: center;
            gap: 15px;
        }

        .user-info {
            font-size: 14px;
            color: #333;
            text-align: right;
        }

        .role-badge {
            padding: 3px 8px;
            margin-left: 5px;
            background-color: #007bff;
            color: white;
            border-radius: 4px;
            font-size: 12px;
        }

        /* Dashboard Button */
        .btn-nav {
            padding: 8px 16px;
            background-color: #0069d9;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            transition: 0.3s;
        }

        .btn-nav:hover {
            background-color: #0053b3;
        }

        /* LOGOUT BUTTON (Red style) */
        .btn-logout {
            padding: 8px 16px;
            background-color: #dc3545;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            font-weight: bold;
            transition: 0.3s;
        }

        .btn-logout:hover {
            background-color: #b21f2d;
        }
    </style>
</head>
<body>
    
    <c:if test="${not empty param.message}">
        <div class="message success">
            ${param.message}
        </div>
    </c:if>
    
    <c:if test="${not empty param.error}">
        <div class="message error">
            ${param.error}
        </div>
    </c:if>
    
    <!--<a href="student?action=new" class="btn">➕ Add New Student</a>-->
    
    <!-- Navigation Bar -->
    <div class="navbar">
        <h2>📚 Student Management System</h2>
        <div class="navbar-right">
            <div class="user-info">
                <span>Welcome, ${sessionScope.fullName}</span>
                <span class="role-badge role-${sessionScope.role}">
                    ${sessionScope.role}
                </span>
            </div>

            <a href="dashboard" class="btn-nav">Dashboard</a>

            <a href="change-password" class="btn-nav">Change Password</a>

            <a href="logout" class="btn-logout">Logout</a>
        </div>
    </div>
    
    <div class="filter-box" style="margin: 20px 0;">
        <form action="student" method="get" style="display: flex; gap: 10px; align-items: center;">
            <input type="hidden" name="action" value="filter">

            <label><strong>Filter by Major:</strong></label>

            <select name="major" style="padding: 5px;">
                <option value="">All Majors</option>

                <option value="Computer Science" 
                    ${selectedMajor == 'Computer Science' ? 'selected' : ''}>
                    Computer Science
                </option>

                <option value="Information Technology" 
                    ${selectedMajor == 'Information Technology' ? 'selected' : ''}>
                    Information Technology
                </option>

                <option value="Software Engineering" 
                    ${selectedMajor == 'Software Engineering' ? 'selected' : ''}>
                    Software Engineering
                </option>

                <option value="Business Administration" 
                    ${selectedMajor == 'Business Administration' ? 'selected' : ''}>
                    Business Administration
                </option>
            </select>

            <button type="submit" class="btn">Apply Filter</button>

            <c:if test="${not empty selectedMajor}">
                <a href="student?action=list" class="btn" 
                   style="background-color:#6c757d;">Clear Filter</a>
            </c:if>
        </form>
    </div>
    
    <div class="search-box" style="margin: 20px 0; padding: 15px; background: #ffffff; border-radius: 8px;">
        <form action="student" method="get" style="display: flex; gap: 10px; align-items: center;">
            <input type="hidden" name="action" value="search">

            <input 
                type="text" 
                name="keyword" 
                placeholder="Search by code, name, or email..."
                value="${keyword}"
                style="flex: 1; padding: 10px; border-radius: 5px; border: 1px solid #ccc;"
            >

            <button type="submit" 
                    style="padding: 10px 20px; background-color: #28a745; color: white; border: none; border-radius: 5px; cursor: pointer;">
                🔍 Search
            </button>

            <c:if test="${not empty keyword}">
                <a href="student?action=list" 
                   style="padding: 10px 15px; background-color: #dc3545; color: white; text-decoration: none; border-radius: 5px;">
                    Clear
                </a>
            </c:if>
        </form>
    </div>

    <c:if test="${not empty keyword}">
        <p style="font-style: italic; color: #555;">Search results for: <strong>${keyword}</strong></p>
    </c:if>
    
    <div class="container">
        <h1>📚 Student List</h1>
        
        <!-- Add button - Admin only -->
        <c:if test="${sessionScope.role eq 'admin'}">
            <div style="margin: 20px 0;">
                <a href="student?action=new" class="btn-add">➕ Add New Student</a>
            </div>
        </c:if>
        <table>
            <thead>
                <tr>
                    <th>
                        <a href="student?action=sort&sortBy=id&order=${order == 'asc' ? 'desc' : 'asc'}">
                            ID
                            <c:if test="${sortBy == 'id'}">
                                ${order == 'asc' ? '▲' : '▼'}
                            </c:if>
                        </a>
                    </th>

                    <th>
                        <a href="student?action=sort&sortBy=student_code&order=${order == 'asc' ? 'desc' : 'asc'}">
                            Code
                            <c:if test="${sortBy == 'student_code'}">
                                ${order == 'asc' ? '▲' : '▼'}
                            </c:if>
                        </a>
                    </th>

                    <th>
                        <a href="student?action=sort&sortBy=full_name&order=${order == 'asc' ? 'desc' : 'asc'}">
                            Name
                            <c:if test="${sortBy == 'full_name'}">
                                ${order == 'asc' ? '▲' : '▼'}
                            </c:if>
                        </a>
                    </th>

                    <th>
                        <a href="student?action=sort&sortBy=email&order=${order == 'asc' ? 'desc' : 'asc'}">
                            Email
                            <c:if test="${sortBy == 'email'}">
                                ${order == 'asc' ? '▲' : '▼'}
                            </c:if>
                        </a>
                    </th>

                    <th>
                        <a href="student?action=sort&sortBy=major&order=${order == 'asc' ? 'desc' : 'asc'}">
                            Major
                            <c:if test="${sortBy == 'major'}">
                                ${order == 'asc' ? '▲' : '▼'}
                            </c:if>
                        </a>
                    </th>

                    <c:if test="${sessionScope.role eq 'admin'}">
                        <th>Actions</th>
                    </c:if>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="student" items="${students}">
                    <tr>
                        <td>${student.id}</td>
                        <td>${student.studentCode}</td>
                        <td>${student.fullName}</td>
                        <td>${student.email != null ? student.email : 'N/A'}</td>
                        <td>${student.major != null ? student.major : 'N/A'}</td>
                        <!-- Action buttons - Admin only -->
                        <c:if test="${sessionScope.role eq 'admin'}">
                            <td>
                                <a href="student?action=edit&id=${student.id}" 
                                   class="btn-edit">Edit</a>
                                <a href="student?action=delete&id=${student.id}" 
                                   class="btn-delete"
                                   onclick="return confirm('Delete this student?')">Delete</a>
                            </td>
                        </c:if>
                    </tr>
                </c:forEach>

                <c:if test="${empty students}">
                    <tr>
                        <td colspan="6" style="text-align: center;">
                            No students found.
                        </td>
                    </tr>
                </c:if>
            </tbody>
        </table>
        <!-- pagination -->
        <div class="pagination">
            <c:if test="${currentPage > 1}">
                <a href="student?action=list&page=${currentPage - 1}">« Previous</a>
            </c:if>

            <c:forEach begin="1" end="${totalPages}" var="i">
                <c:choose>
                    <c:when test="${i == currentPage}">
                        <strong>${i}</strong>
                    </c:when>
                    <c:otherwise>
                        <a href="student?action=list&page=${i}">${i}</a>
                    </c:otherwise>
                </c:choose>
            </c:forEach>

            <c:if test="${currentPage < totalPages}">
                <a href="student?action=list&page=${currentPage + 1}">Next »</a>
            </c:if>
        </div>

        <p style="text-align:center; margin-top:10px;">
            Showing page <strong>${currentPage}</strong> of <strong>${totalPages}</strong>
        </p>
    </div>
    <script>
        const SESSION_TIMEOUT = 30 * 60 * 1000;
        let lastActivity = Date.now();

        ['mousemove', 'keypress', 'click'].forEach(event => {
        document.addEventListener(event, () => lastActivity = Date.now());
        });

        setInterval(() => {
        const timeRemaining = SESSION_TIMEOUT - (Date.now() - lastActivity);
        const minutesLeft = Math.floor(timeRemaining / 60000);

        if (timeRemaining <= 0) {
            alert('Session expired. Please login again.');
            window.location.href = 'logout';
        } else if (minutesLeft <= 5 && minutesLeft > 0) {
            console.warn(`⚠️ Session expires in ${minutesLeft} minutes`);
        }
        }, 60000);
    </script>
</body>
</html>
