<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Account balances</title>
    </head>
    <body>
        <%@include file="menu.html" %>
        <c:choose>
            <c:when test="${not empty accounts}">
                <table border=1>
                    <tr>
                        <th>Account Number</th>
                        <th>Currency</th>
                        <th>Balance</th>
                    </tr>
                    <c:forEach var="account" items="${accounts}">
                        <tr>
                            <td>${account.formattedAccountNumber}</td>
                            <td>${account.currencyShort}</td> 
                            <td>${account.balance}</td> 
                        </tr>
                    </c:forEach>
                </table>
            </c:when>
            <c:otherwise>
                <span>No Accounts</span>
            </c:otherwise>
        </c:choose>
    </body>
</html>
