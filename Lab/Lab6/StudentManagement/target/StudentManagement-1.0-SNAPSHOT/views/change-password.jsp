<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Change Password</title>
    <style>
        body { 
            font-family: Arial, sans-serif; 
            background-color: #f5f5f5; 
            margin: 20px; 
        }
        .container { 
            max-width: 500px; 
            margin: 50px auto; 
            background: white; 
            padding: 30px; 
            border-radius: 10px; 
            box-shadow: 0 0 10px rgba(0,0,0,0.1); 
        }
        h2 { 
            color: #333; 
            margin-bottom: 20px; 
            text-align: center; 
        }
        .form-group { 
            margin-bottom: 15px; 
        }
        label { 
            display: block; 
            margin-bottom: 5px; 
            font-weight: bold; 
        }
        input[type="password"] { 
            width: 100%; 
            padding: 10px; 
            border: 1px solid #ddd; 
            border-radius: 5px; 
            box-sizing: border-box; 
        }
        button { 
            width: 100%; 
            padding: 10px; 
            background-color: #007bff; 
            color: white; 
            border: none; 
            border-radius: 5px; 
            cursor: pointer; 
            font-size: 16px; 
            margin-top: 10px; 
        }
        button:hover { 
            background-color: #0056b3; 
        }
        .btn-cancel { 
            display: block;
            text-align: center; 
            margin-top: 15px; 
            color: #666; 
            text-decoration: none; 
        }
        .alert { 
            padding: 10px; 
            margin-bottom: 15px; 
            border-radius: 5px; 
        }
        .alert-error { 
            background-color: #f8d7da; 
            color: #721c24; 
            border: 1px solid #f5c6cb; 
        }
        .alert-success { 
            background-color: #d4edda; 
            color: #155724; 
            border: 1px solid #c3e6cb; 
        }
    </style>
</head>
<body>

<div class="container">
    <h2>Change Password</h2>

    <%-- Success Message --%>
    <c:if test="${not empty message}">
        <div class="alert alert-success">${message}</div>
    </c:if>

    <%-- Error Message --%>
    <c:if test="${not empty error}">
        <div class="alert alert-error">${error}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/change-password" method="post">
        
        <div class="form-group">
            <label>Current Password</label>
            <input type="password" name="currentPassword" required>
        </div>

        <div class="form-group">
            <label>New Password (min 8 chars)</label>
            <input type="password" name="newPassword" minlength="8" required>
        </div>

        <div class="form-group">
            <label>Confirm New Password</label>
            <input type="password" name="confirmPassword" minlength="8" required>
        </div>

        <button type="submit">Update Password</button>
        
        <a href="${pageContext.request.contextPath}/student?action=list" class="btn-cancel">Back to Home</a>
    </form>
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