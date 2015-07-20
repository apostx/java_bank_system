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
        <%@include file="menu.html" %>
        
        <%!
            // validate form datas and make transaction
        
            public String makeTransfer(HttpServletRequest request,DatabaseManager databaseManager,int userID) {
                boolean error = false;
            
                int sourceAccountCurrencyID = 0;
                int targetAccountCurrencyID = 0;
                int amount = 0;
                
                String sourceAccountNumber = request.getParameter("source_account_number");
                String targetAccountNumber = request.getParameter("target_account_number");
                String amountString = request.getParameter("amount");
                
                error = sourceAccountNumber == null || targetAccountNumber == null || amountString == null;

                if (error)
                    return "Missing Datas";
                
                else
                    error = sourceAccountNumber.equals(targetAccountNumber);
                
                if (error)
                    return "Target Account Number is same as source";
                 
                else 
                    try {
                        amount = Integer.parseInt(amountString);
                        error = amount <= 0;
                    } catch (Exception e) {
                        error = true;
                    }
                
                if (error)
                    return "Invalid amount format";
                
                else {
                    sourceAccountCurrencyID = databaseManager.getAccountCurrencyID(sourceAccountNumber);
                    error = sourceAccountCurrencyID == 0;
                }
                
                if (error)
                    return "Invalid source Account Number";
                
                else {
                    targetAccountCurrencyID = databaseManager.getAccountCurrencyID(targetAccountNumber);
                    error = targetAccountCurrencyID == 0;
                }
                
                if (error)
                    return "Invalid target Account Number";
                
                else
                    error = sourceAccountCurrencyID != targetAccountCurrencyID;

                if (error)
                    return "Invalid target account currency";
                
                // transaction
                
                else {
                    int status = databaseManager.transaction(userID, sourceAccountNumber, targetAccountNumber, amount);
                    error = 0 < status;
                    if (status == 2)
                        return "Too little source balance";
                    else if (error)
                        return "Transaction failed ("+status+")";
                }
                
                return null;
            }
        %>
        <%
            
            // Generate input form
            
            WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
            DatabaseManager databaseManager = (DatabaseManager) ctx.getBean("databaseManager");
        
            int userID = (int) session.getAttribute("id");
            
            List<String> accounts = databaseManager.getOwnAccountList(userID);
            
            if (0 < accounts.size()) {
                String a;
                
                out.println("<form id=\"transfer_form\" action=\"transfer.jsp\" autocomplete=\"on\" method=\"post\">");
                out.println("Source Account Number<br />");
                out.println("<select name=\"source_account_number\">");
               
                for (Iterator<String> i = accounts.iterator(); i.hasNext();) {
                    a = i.next();
 
                    out.println("<option value=\"$1\">$2</option>".
                            replace("$1",a).
                            replace("$2",FormatString.accountNumber(a)));
                }
                
                out.println("</select><br /><br />");
                
                out.println("Target Account Number (********-********-********)<br />");
                
                out.println("<input required=\"required\" name=\"target_account_number_formatted\" type=\"text\" pattern=\"[0-9]{8}[-][0-9]{8}[-][0-9]{8}\" id=\"target_account_number_list\" />");
                
                out.println("<datalist id=\"target_account_number_list\"></datalist><br /><br />");
                
                out.println("<input name=\"target_account_number\" type=\"hidden\" />");
                
                out.println("Amount (0<)<br />");
                
                out.println("<input required=\"required\" name=\"amount\" type=\"text\" pattern=\"[0-9]*[1-9][0-9]*\" /><br /><br />");
                
                out.println("<input name=\"transfer\" type=\"hidden\" value=\"transfer\" />");
        
                out.println("<input id=\"submit\" type=\"submit\" value=\"Submit\">");
                
                out.println("</form>");
                
                
            }else
                out.println("No Accounts");
            

            // form datas validating, transaction making and error handling
            
            if (request.getParameter("transfer") != null) {
                String errorMessage = makeTransfer(request,databaseManager,userID);
                boolean error = errorMessage != null;
                
                out.println("<br /><br /><p style=\"color:" + (error?"red":"green") +"\">" + (error?("Error: "+ errorMessage):"Successful Transaction") + "</p>");
            }
        %>
        
        <script>
            
            // transfer permission
    
            $(function() {
                if($("#transfer_form").length <= 0)
                    return;
                
                $("#transfer_form").submit(onClick);
                
                function onClick() {
                    if(confirm("Are you sure?!")) {
                        var f = $("#transfer_form");
                        var value = f.find("input[name=target_account_number_formatted]").val().replace(/-/g,"");
                        f.find("input[name=target_account_number]").val(value);
                    } else
                        event.preventDefault();
                }
            });
        </script>
    </body>
</html>
