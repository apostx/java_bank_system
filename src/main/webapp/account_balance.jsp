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
        <title>Account balances</title>
    </head>
    <body>
        <%@include file="menu.html" %>
        <%
            
            // generate table from sql datas
            
            WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
            DatabaseManager databaseManager = (DatabaseManager) ctx.getBean("databaseManager");
        
            List<Account> accounts = databaseManager.getOwnAccounts((Integer) session.getAttribute("id"));
            if (0 < accounts.size()) {
                Account a;
                out.println("<table border=1>");
                out.println("<tr><th>Account Number</th><th>Currency</th><th>Balance</th></tr>");
                for (Iterator<Account> i = accounts.iterator(); i.hasNext();) {
                    a = i.next();
                    
                    out.println("<tr><td>$1</td><td>$2</td><td>$3</td></tr>".
                            replace("$1",FormatString.accountNumber(a.getAccountNumber())).
                            replace("$2",a.getCurrencyShort()).
                            replace("$3",Integer.toString(a.getBalance())));
                }
                
                out.println("</table>");
            }else
                out.println("No Accounts");
        %>
    </body>
</html>
