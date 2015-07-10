<%@page import="wup.utils.FormatString"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%@page import="wup.db.data.AccountMapper.Account"%>
<%@page import="wup.db.DatabaseManager"%>
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
        
        <%
            WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
            DatabaseManager databaseManager = (DatabaseManager) ctx.getBean("databaseManager");
        
            List<String> accounts = databaseManager.getOwnAccountList((int) session.getAttribute("id"));
            
            if (0 < accounts.size()) {
                String a;
                
                out.println("<select id=\"account_number\"><option value=\"\" disabled selected>Select account number</option>");
               
                int j;
                for (Iterator<String> i = accounts.iterator(); i.hasNext();) {
                    a = i.next();
                    
                    out.println("<option value=\"$1\">$2</option>".
                            replace("$1",a).
                            replace("$2",FormatString.accountNumber(a)));
                }
                
                out.println("");
                
            }else
                out.println("<select id=\"account_number\" disabled><option value=\"\" selected>No Accounts</option>");
            
                out.println("</select>");
        %>
        <br />
        <br />
        <span id="history" hidden></span>
        
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
                    $.getJSON( "/wup_bank_system/service.json?account_number=" + e.currentTarget.value,onComplete);
                }
                
                /**
                 * process datas from service and generate a table from the datas
                 */
                function onComplete(transactions) {
                    $("#history").empty();
                    
                    if (transactions.length === 0)
                        $("#history").html("Empty list");
                    
                    else {
                        var table = $('<table>',{border:1});
                        table.append($("<tr><th>Source/Target Account</th><th>Currency</th><th>Amount</th><th>Transaction</th><th>Balance</th></tr>"));
                        var row;
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
