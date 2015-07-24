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
            </c:when>
            <c:otherwise>
                <select disabled>
                    <option selected>
                        No Accounts
                    </option>
                </select>
            </c:otherwise>
        </c:choose>
        
        <script>
            
            // selected account number handling
            $(function() {
                $("#account_number").on("change",onChange);
                
                /**
                 * Init DOM elements and send the service a request
                 */
                function onChange(e) {
                    $("#account_number").prop('disabled',true);            
                    $("#history").prop('hidden',false);
                    $("#history").empty();
                    $("#history").html("Loading...");
                    $.getJSON( "service?account_number=" + e.currentTarget.value,onComplete);
                }
                
                /**
                 * Process datas from service and generate a table from the datas
                 */
                function onComplete(transactions) {
                    $("#history").empty();
                    
                    if (transactions.length === 0)
                        $("#history").html("Empty list");
                    
                    else {
                        var table = $('<table>',{border:1});
                        var headers = ["Source/Target Account","Currency","Amount","Transfer Direction","Balance"];
                        var row = $("<tr>");
                        
                        $.each(headers, function (i, header) {
                            row.append($('<th>', { text : header }));
                        });
                        table.append(row);
                        
                        $.each(transactions, function (i, transaction) {
                            row = $("<tr>");
                            row.append($('<td>', { text : transaction.account_number.match(/.{8}/g).join("-") }));
                            row.append($('<td>', { text : transaction.currency }));
                            row.append($('<td>', { text : transaction.amount }));
                            row.append($('<td>', { text : transaction.out ? "-" : "+" }));
                            row.append($('<td>', { text : transaction.balance }));
                            table.append(row);
                        });

                        $("#history").append(table);
                    }
                    $("#account_number").prop('disabled',false);  
                }
            });
        </script>
    </body>
</html>
