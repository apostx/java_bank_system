package wup.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import wup.helper.data.Transaction;
import wup.helper.db.DatabaseManager;

/**
 * Generate json from account history from database
 */
public class ServiceServlet extends HttpServlet {
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException,IOException {
        HttpSession session = req.getSession();

        Object id = session.getAttribute("id");

        if (id == null)
            return;

        WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        DatabaseManager databaseManager = (DatabaseManager) ctx.getBean("databaseManager");

        String accountNumber = req.getParameter("account_number");

        List<Transaction> transactions = databaseManager.getTransactions((int) id, accountNumber);

        ObjectMapper mapper = new ObjectMapper();

        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().println(mapper.writeValueAsString(transactions));
    }
}
