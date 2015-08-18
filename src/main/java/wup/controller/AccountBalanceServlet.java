package wup.controller;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import wup.helper.db.DatabaseManager;

public class AccountBalanceServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        DatabaseManager databaseManager = (DatabaseManager) ctx.getBean("databaseManager");
        int id = (int) req.getSession().getAttribute("id");
        
        req.setAttribute("accounts",databaseManager.getOwnAccountList(id));
       
        RequestDispatcher rd = req.getRequestDispatcher("WEB-INF/view/account_balance.jsp");
        rd.forward(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp);
    }
}
