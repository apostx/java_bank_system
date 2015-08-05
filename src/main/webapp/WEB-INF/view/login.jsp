<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login</title>
    </head>
    <body>
        <form action="login" autocomplete="on" method="post">
            Email address:
            <br>
            <input type="text" name="name" value="${login_name}">
            <br>
            Password
            <br>
            <input type="password" name="password">
            <br>
            <input type="submit" value="Submit">
        </form>
        <c:if test="${login_failed}">
            <span>Invalid name or password!</span>
        </c:if>
    </body>
</html>
