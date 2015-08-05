<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>History</title>
    </head>
    <body>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <%@include file="menu.html" %>
        
        <c:choose>
            <c:when test="${not empty account_numbers}">
                <select id="account_number">
                    <option disabled selected>Select account number</option>
                    <c:forEach var="accountNumber" items="${account_numbers}">
                        <option value="${accountNumber.value}">${accountNumber.key}</option>
                    </c:forEach>
                </select>
                <br />
                <br />
                <span id="history" hidden></span>
                <script src="resources/js/history.js"></script>
            </c:when>
            <c:otherwise>
                <select disabled>
                    <option selected>
                        No Accounts
                    </option>
                </select>
            </c:otherwise>
        </c:choose>
    </body>
</html>
