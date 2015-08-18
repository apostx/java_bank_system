package wup.controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import wup.helper.db.DatabaseManager;

public class LoginServlet extends HttpServlet {

    private static final String ID_ATTR = "id";
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        // logout procedure
        boolean isLogoutRequest = "true".equals(req.getParameter("logout"));
        if (isLogoutRequest && isLogined(req)) {
            HttpSession session = req.getSession();
            session.removeAttribute(ID_ATTR);
            session.invalidate();
        }
        
        processRequest(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(!isLogined(req)) {
            WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
            DatabaseManager databaseManager = (DatabaseManager) ctx.getBean("databaseManager");
            
            // login procedure
            String name = req.getParameter("name");
            String password = req.getParameter("password");

            if(name != null && password != null) {
                int id = databaseManager.getUserID(name,password);
                
                if (0 < id)
                    req.getSession().setAttribute("id", id);
                else {
                    req.setAttribute("login_failed",true);
                    req.setAttribute("login_name",name);
                }
            }
        }
        
        processRequest(req,resp);
    }
    
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(isLogined(req))
            resp.sendRedirect("account_balance");
        else {
            RequestDispatcher rd = req.getRequestDispatcher("WEB-INF/view/login.jsp");
            rd.forward(req, resp);
        }
    }

    private boolean isLogined(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null && session.getAttribute(ID_ATTR) != null;
    }
}
