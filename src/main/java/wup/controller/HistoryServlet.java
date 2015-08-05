package wup.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import wup.db.DatabaseManager;
import wup.utils.FormatString;

public class HistoryServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        DatabaseManager databaseManager = (DatabaseManager) ctx.getBean("databaseManager");
        int id = (int) req.getSession().getAttribute("id");
        
        List<String> accountNumberList = databaseManager.getOwnAccountNumberList(id);
        
        Map<String,String> accountNumbers = new TreeMap<>();
        for (String accountNumber : accountNumberList)
            accountNumbers.put(FormatString.accountNumber(accountNumber),accountNumber);
        
        req.setAttribute("account_numbers",accountNumbers);
        
        RequestDispatcher rd = req.getRequestDispatcher("WEB-INF/view/history.jsp");
        rd.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
