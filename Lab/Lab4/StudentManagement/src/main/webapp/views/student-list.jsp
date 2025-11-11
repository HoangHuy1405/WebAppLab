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
    </style>
</head>
<body>
    <h1>📚 Student Management System (MVC)</h1>
    
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
    
    <a href="student?action=new" class="btn">➕ Add New Student</a>
    
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Student Code</th>
                <th>Full Name</th>
                <th>Email</th>
                <th>Major</th>
                <th>Actions</th>
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
                    <td>
                        <a href="student?action=edit&id=${student.id}" class="action-link">✏️ Edit</a>
                        <a href="student?action=delete&id=${student.id}" 
                           class="action-link delete-link"
                           onclick="return confirm('Are you sure?')">🗑️ Delete</a>
                    </td>
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
</body>
</html>
