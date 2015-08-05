/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wup.controller.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * If the request hasn't logined session, than the request will redirect to login site
 * If it has that, than there is default behavior 
 */
public class LoginFilter implements Filter {

    private static final String LOGIN_URL = "/login";
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        
        HttpSession session = req.getSession(false);
        
        
        boolean login = ! (session == null || session.getAttribute("id") == null);
        
        if (login || req.getContextPath().concat(LOGIN_URL).equals(req.getRequestURI()))
            chain.doFilter(request, response);
        
        else
            resp.sendRedirect(req.getContextPath()+LOGIN_URL);
        
    }

    public void setFilterConfig(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}

    @Override
    public void init(FilterConfig filterConfig) {}
}
