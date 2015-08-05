<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Transfer</title>
    </head>
    <body>
        <%@include file="menu.html" %>
        <c:choose>
            <c:when test="${not empty model.accountNumbers}">
                <form id="transfer_form" action="transfer" autocomplete="on" method="post">
                    Source Account Number<br />
                    <select name="source_account_number" required>
                        <option ${model.validSourceAccountNumber ? "" : "selected"} disabled value="">Select account number</option>
                        <c:forEach var="accountNumber" items="${model.accountNumbers}">
                            <option ${false ? "selected" : ""} value="${accountNumber.value}">${accountNumber.key}</option>
                        </c:forEach>
                    </select>
                    <br />
                    <c:if test="${0 < model.sourceAccountNumberError}">
                        <span style="color:red">
                            <c:choose>
                                <c:when test="${model.sourceAccountNumberError == 1}">Missing Data</c:when>
                                <c:when test="${model.sourceAccountNumberError == 2}">Invalid Format</c:when>
                                <c:when test="${model.sourceAccountNumberError == 3}">Invalid Account Number</c:when>
                            </c:choose>
                        </span>
                        <br />
                    </c:if>
                    <br />
                    Target Account Number (********-********-********)<br />
                    <input required name="target_account_number_formatted" type="text" pattern="[0-9]{8}[-][0-9]{8}[-][0-9]{8}" list="target_account_number_list" value="${model.formattedTargetAccountNumber}" />
                    <datalist id="target_account_number_list"></datalist>
                    <input name="target_account_number" type="hidden" />
                    <br />
                    <c:if test="${0 < model.targetAccountNumberError}">
                        <span style="color:red">
                            <c:choose>
                                <c:when test="${model.targetAccountNumberError == 1}">Missing Data</c:when>
                                <c:when test="${model.targetAccountNumberError == 2}">Invalid Format</c:when>
                                <c:when test="${model.targetAccountNumberError == 3}">Invalid Account Number</c:when>
                                <c:when test="${model.targetAccountNumberError == 4}">Same As Source Account Number</c:when>
                                <c:when test="${model.targetAccountNumberError == 5}">Different Currency From Source</c:when>
                            </c:choose>
                        </span>
                        <br />
                    </c:if>
                    <br />
                    Amount (0&lt;)
                    <br />
                    <input required name="amount" type="text" pattern="[0-9]*[1-9][0-9]*" value="${model.amount}" />
                    <br />
                    <c:if test="${0 < model.amountError}">
                        <span style="color:red">
                            <c:choose>
                                <c:when test="${model.amountError == 1}">Missing Data</c:when>
                                <c:when test="${model.amountError == 2}">Invalid Format</c:when>
                                <c:when test="${model.amountError == 3}">More Than Balance</c:when>
                            </c:choose>
                        </span>
                        <br />
                    </c:if>
                    <br />
                    <input id="submit" type="submit" value="Submit">
                </form>
                <br />
                <c:if test="${not model.transferParameterError}">
                    <c:choose>
                        <c:when test="${model.serverError}">
                            <p style="color:red">Server Error (${model.serverErrorCode})</p>
                        </c:when>
                        <c:otherwise>
                            <p style="color:green">Successful Transfer</p>
                        </c:otherwise>
                    </c:choose>
                </c:if>
            </c:when>
            <c:otherwise>
                <select disabled>
                    <option selected>
                        No Accounts
                    </option>
                </select>
            </c:otherwise>
        </c:choose>
        
        <script src="resources/js/transfer.js"></script>
    </body>
</html>
