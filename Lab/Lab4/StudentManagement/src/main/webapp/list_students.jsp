<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="java.util.regex.*" %>
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
            border: 1px solid #c3e6cb;
        }
        .error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
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
        .highlight {
            background-color: yellow;
            font-weight: bold;
        }
        .pagination {
            margin-top: 20px;
            text-align: center;
        }
        .pagination a, .pagination strong {
            margin: 0 5px;
            text-decoration: none;
            color: #007bff;
        }
        .pagination strong {
            font-weight: bold;
            color: #000;
        }
        form {
            margin-bottom: 20px;
        }
        input[type="text"] {
            padding: 8px;
            width: 250px;
        }
        button {
            padding: 8px 15px;
            background-color: #007bff;
            color: white;
            border: none;
            cursor: pointer;
        }
        a.clear-link {
            margin-left: 10px;
            color: #007bff;
            text-decoration: none;
        }
        .table-responsive {
            overflow-x: auto;
        }

        @media (max-width: 768px) {
            table {
                font-size: 12px;
            }
            th, td {
                padding: 5px;
            }
        }
    </style>
    <script>
    setTimeout(function() {
        var messages = document.querySelectorAll('.message');
        messages.forEach(function(msg) {
            msg.style.display = 'none';
        });
    }, 3000);
    </script>
</head>
<body>
    <h1>📚 Student Management System</h1>
    
    <% if (request.getParameter("message") != null) { %>
    <div class="message success">✓ <%= request.getParameter("message") %></div>
    <% } %>

    <% if (request.getParameter("error") != null) { %>
        <div class="message error">✗ <%= request.getParameter("error") %></div>
    <% } %>

    <!-- 🔍 Search Form -->
    <form action="list_students.jsp" method="GET">
        <input type="text" name="keyword" placeholder="Search by any field..." 
               value="<%= request.getParameter("keyword") != null ? request.getParameter("keyword") : "" %>">
        <button type="submit">Search</button>
        <a href="list_students.jsp" class="clear-link">Clear</a>
    </form>

    <a href="add_student.jsp" class="btn">➕ Add New Student</a>

    <div class="table-responsive">
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Student Code</th>
                <th>Full Name</th>
                <th>Email</th>
                <th>Major</th>
                <th>Created At</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>

<%!
    public int getTotalRecords(String keyword) {
        int total = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/student_management",
                "root",
                "root"
            );

            String sql;
            if (keyword != null && !keyword.trim().isEmpty()) {
                sql = "SELECT COUNT(*) FROM students "
                    + "WHERE full_name LIKE ? OR student_code LIKE ? OR email LIKE ? OR major LIKE ?";
                pstmt = conn.prepareStatement(sql);
                for (int i = 1; i <= 4; i++) {
                    pstmt.setString(i, "%" + keyword + "%");
                }
            } else {
                sql = "SELECT COUNT(*) FROM students";
                pstmt = conn.prepareStatement(sql);
            }

            rs = pstmt.executeQuery();
            if (rs.next()) {
                total = rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return total;
    }
%>

<%
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    String keyword = request.getParameter("keyword");
    String pageParam = request.getParameter("page");

    int currentPage = 1;
    try {
        if (pageParam != null) currentPage = Integer.parseInt(pageParam);
    } catch (NumberFormatException e) {
        currentPage = 1;
    }

    int recordsPerPage = 10;
    int offset = (currentPage - 1) * recordsPerPage;

    int totalRecords = getTotalRecords(keyword);
    int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);

    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/student_management",
            "root",
            "root"
        );

        String sql;
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql = "SELECT * FROM students WHERE full_name LIKE ? OR student_code LIKE ? OR email LIKE ? OR major LIKE ? "
                + "ORDER BY id DESC LIMIT ? OFFSET ?";
            pstmt = conn.prepareStatement(sql);
            for (int i = 1; i <= 4; i++) pstmt.setString(i, "%" + keyword + "%");
            pstmt.setInt(5, recordsPerPage);
            pstmt.setInt(6, offset);
        } else {
            sql = "SELECT * FROM students ORDER BY id DESC LIMIT ? OFFSET ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, recordsPerPage);
            pstmt.setInt(2, offset);
        }

        rs = pstmt.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("id");
            String studentCode = rs.getString("student_code");
            String fullName = rs.getString("full_name");
            String email = rs.getString("email");
            String major = rs.getString("major");
            Timestamp createdAt = rs.getTimestamp("created_at");

            if (keyword != null && !keyword.trim().isEmpty()) {
                String safeKeyword = Pattern.quote(keyword);
                fullName = fullName.replaceAll("(?i)(" + safeKeyword + ")", "<span class='highlight'>$1</span>");
                studentCode = studentCode.replaceAll("(?i)(" + safeKeyword + ")", "<span class='highlight'>$1</span>");
                if (email != null)
                    email = email.replaceAll("(?i)(" + safeKeyword + ")", "<span class='highlight'>$1</span>");
                if (major != null)
                    major = major.replaceAll("(?i)(" + safeKeyword + ")", "<span class='highlight'>$1</span>");
            }
%>
            <tr>
                <td><%= id %></td>
                <td><%= studentCode %></td>
                <td><%= fullName %></td>
                <td><%= email != null ? email : "N/A" %></td>
                <td><%= major != null ? major : "N/A" %></td>
                <td><%= createdAt %></td>
                <td>
                    <a href="edit_student.jsp?id=<%= id %>" class="action-link">✏️ Edit</a>
                    <a href="delete_student.jsp?id=<%= id %>" 
                       class="action-link delete-link"
                       onclick="return confirm('Are you sure you want to delete this student?')">🗑️ Delete</a>
                </td>
            </tr>
<%
        }

    } catch (Exception e) {
        out.println("<tr><td colspan='7'>Error: " + e.getMessage() + "</td></tr>");
        e.printStackTrace();
    } finally {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
%>
        </tbody>
    </table>
    </div>
        
    <div class="pagination">
        <% if (currentPage > 1) { %>
            <a href="list_students.jsp?page=<%= currentPage - 1 %><%= (keyword != null && !keyword.trim().isEmpty()) ? "&keyword=" + keyword : "" %>">Previous</a>
        <% } %>

        <% for (int i = 1; i <= totalPages; i++) { %>
            <% if (i == currentPage) { %>
                <strong><%= i %></strong>
            <% } else { %>
                <a href="list_students.jsp?page=<%= i %><%= (keyword != null && !keyword.trim().isEmpty()) ? "&keyword=" + keyword : "" %>"><%= i %></a>
            <% } %>
        <% } %>

        <% if (currentPage < totalPages) { %>
            <a href="list_students.jsp?page=<%= currentPage + 1 %><%= (keyword != null && !keyword.trim().isEmpty()) ? "&keyword=" + keyword : "" %>">Next</a>
        <% } %>
    </div>

</body>
</html>
