<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%@page import="wup.db.DatabaseManager"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% 
    WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
    DatabaseManager databaseManager = (DatabaseManager) ctx.getBean("databaseManager");
    
    boolean tryLogin = false;
    boolean isLogin = session.getAttribute("id") != null;
    boolean logoutRequest = "true".equals(request.getParameter("logout"));

    if (isLogin && logoutRequest) {
        
        // logout procedure
        
        session.removeAttribute("id");
        session.invalidate();
    } else if(!isLogin) {
        
        // login procedure
        
        String emailAddress = request.getParameter("email_address");
        String password = request.getParameter("password");
        tryLogin = !(emailAddress == null &&  password == null);
        
        if(tryLogin) {
            int id = databaseManager.getUserID(emailAddress,password);
            
            if (0<id) {
                session.setAttribute("id", id);
                response.sendRedirect("account_balance.jsp");
            }
        }
    } else {
        response.sendRedirect("account_balance.jsp");
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login</title>
    </head>
    <body>
        <form action="login.jsp" autocomplete="on" method="post">
            Email address:
            <br>
            <input type="text" name="email_address">
            <br>
            Paddword
            <br>
            <input type="password" name="password">
            <br>
            <input type="submit" value="Submit">
        </form>
        <%
            if (tryLogin)
                out.print("Invalid email address or password.");
        %>
    </body>
</html>
