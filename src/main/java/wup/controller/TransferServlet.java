package wup.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import wup.helper.db.DatabaseManager;
import wup.helper.db.DatabaseManager.TransferErrorType;
import wup.helper.db.DatabaseManager.TransferResult;
import wup.model.TransferModel;
import wup.helper.view.FormatString;

public class TransferServlet extends HttpServlet {

    private static final String ACCOUNT_NUMBER_PATTERN = "[0-9]{24}";
    private static final String AMOUNT_PATTERN = "[0-9]*[1-9][0-9]*";
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        DatabaseManager databaseManager = (DatabaseManager) ctx.getBean("databaseManager");
        TransferModel model = new TransferModel(false);
        processRequest(req,resp,databaseManager,model);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        DatabaseManager databaseManager = (DatabaseManager) ctx.getBean("databaseManager");
        int userID = (int) req.getSession().getAttribute("id");
        
        TransferModel model = new TransferModel(true);
        initModelData(model,req);
        
        if (!model.isTransferParameterError()) {
            TransferResult transferResult = databaseManager.transfer(userID, model.getSourceAccountNumber(), model.getTargetAccountNumber(), Integer.parseInt(model.getAmount()));
            initModelError(model,transferResult);
        }
        
        processRequest(req,resp,databaseManager,model);
    }
    
    private void processRequest(HttpServletRequest req, HttpServletResponse resp,DatabaseManager databaseManager,TransferModel model) throws ServletException, IOException {
        int userID = (int) req.getSession().getAttribute("id");
        List<String> accountNumberList = databaseManager.getOwnAccountNumberList(userID);
        System.out.println(accountNumberList);
        Map<String,String> accountNumbers = model.getAccountNumbers();
        for (String accountNumber : accountNumberList)
            accountNumbers.put(FormatString.accountNumber(accountNumber),accountNumber);
        
        req.setAttribute("model",model);
        
        RequestDispatcher rd = req.getRequestDispatcher("WEB-INF/view/transfer.jsp");
        rd.forward(req, resp);
    }
    
    public void initModelData(TransferModel model,HttpServletRequest req) {
        int sourceAccountNumberError = 0;
        int targetAccountNumberError = 0;
        int amountError = 0;
   
        String sourceAccountNumber = req.getParameter("source_account_number");
        String targetAccountNumber = req.getParameter("target_account_number");
        String amountString = req.getParameter("amount");

        // source account number validating
        if(sourceAccountNumber == null || sourceAccountNumber.length() == 0) 
            sourceAccountNumberError = 1;
        
        if(sourceAccountNumberError == 0 && ! sourceAccountNumber.matches(ACCOUNT_NUMBER_PATTERN)) 
            sourceAccountNumberError = 2;
        
        // target account number validating
        if(targetAccountNumber == null || targetAccountNumber.length() == 0) 
            targetAccountNumberError = 1;
        
        if(targetAccountNumberError == 0 && ! targetAccountNumber.matches(ACCOUNT_NUMBER_PATTERN)) 
            targetAccountNumberError = 2;
        
        // amount validating
        if(amountString == null || amountString.length() == 0) 
            amountError = 1;
        
        if(amountError == 0 && ! amountString.matches(AMOUNT_PATTERN)) 
            amountError = 2;

        model.initFormData(sourceAccountNumber, targetAccountNumber, amountString);
        model.initFormErrors(sourceAccountNumberError, targetAccountNumberError, amountError);
    }
    
    public void initModelError(TransferModel model,TransferResult result) {
        if (result.equals(TransferResult.SUCCESSFUL))
            return;
        
        int sourceAccountNumberError = 0;
        int targetAccountNumberError = 0;
        int amountError = 0;
        
        if(result.getErrorType().equals(TransferErrorType.INVALID_PARAMTER)) {
            switch(result.getStep()) {
                case CHECK_SOURCE_ACCOUNT:
                    sourceAccountNumberError = 3;
                    break;
                case CHECK_SOURCE_BALANCE:
                    amountError = 3;
                    break;
                case CHECK_TARGET_ACCOUNT:
                    targetAccountNumberError = 3;
                    break;
                case CHECK_DIFFERENT_ACCOUNT:
                    targetAccountNumberError = 4;
                    break;
                case CHECK_CURRENCY:
                    targetAccountNumberError = 5;
                    break;
                case SUBTRACTION:
                case ADDITION:
                case LOGGING:
                    model.initServerErrors(result.getStep().getCode(),result.getErrorType().getCode());
                    break;
            }
            model.initFormErrors(sourceAccountNumberError,targetAccountNumberError,amountError);
        } else
            model.initServerErrors(result.getStep().getCode(),result.getErrorType().getCode());
    }
}
