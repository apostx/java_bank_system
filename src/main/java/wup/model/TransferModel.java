package wup.model;

import java.util.Map;
import java.util.TreeMap;
import wup.utils.FormatString;

public class TransferModel {

    /**
     * Source account error code
     * 0 - No error
     * 1 - Missing data
     * 2 - Invalid data format
     * 3 - Invalid data
     */
    public int _sourceAccountNumberError = -1;
    
    /**
     * Target account error code
     *  0 - No error
     *  1 - Missing data
     *  2 - Invalid data format
     *  3 - Invalid data
     *  4 - Account number same as in source
     *  5 - Currency id different from source
     */
    public int _targetAccountNumberError = 0;
    
    /**
     * Amount error code
     *  0 - No error
     *  1 - Missing data
     *  2 - Invalid data format
     *  3 - Invalid data
     */
    public int _amountError = 0;
    
    /**
     * Server error code shows the code of last successful step of transfer and the type of error
     * Format: "{TransferStep}_{TransferErrorType}"
     * @see wup.db.DatabaseManager.TransferStep
     * @see wup.db.DatabaseManager.TransferErrorType
     */
    public String _serverError = null;
    
    public String _sourceAccountNumber = null;
    public String _targetAccountNumber = null;
    public String _amount = null;
            
    public Map<String,String> _accountNumbers = new TreeMap<>();

    public boolean _isTransfer;
    
    public TransferModel(boolean isTransfer) {
        _isTransfer = isTransfer;
    }
    
    public void initFormErrors(int sourceAccountNumberError,int targetAccountNumberError,int amountError) {
        _sourceAccountNumberError = Math.max(sourceAccountNumberError, _sourceAccountNumberError);
        _targetAccountNumberError = Math.max(targetAccountNumberError, _targetAccountNumberError);
        _amountError = Math.max(amountError, _amountError);
    }
    
    public void initServerErrors(int stepCode,int errorTypeCode) {
        _serverError = stepCode + "_" + errorTypeCode;
    }
    
    public void initFormData(String sourceAccountNumber,String targetAccountNumber,String amount) {
        _sourceAccountNumber = sourceAccountNumber;
        _targetAccountNumber = targetAccountNumber;
        _amount = amount;
    }
    
    public int getSourceAccountNumberError() {
        return _sourceAccountNumberError;
    }
    
    public int getTargetAccountNumberError() {
        return _targetAccountNumberError;
    }
    
    public int getAmountError() {
        return _amountError;
    }
    
    public boolean isServerError() {
        return _serverError != null;
    }
    
    public String getServerErrorCode() {
        return _serverError;
    }
    
    public String getSourceAccountNumber() {
        return _sourceAccountNumber;
    }
    
    public String getTargetAccountNumber() {
        return _targetAccountNumber;
    }
    
    public String getFormattedTargetAccountNumber() {
        String result = null;
        try {
            result = FormatString.accountNumber(_targetAccountNumber);
        } catch(Exception e){}
        
        return result;
    }
    
    public String getAmount() {
        return _amount;
    }

    public Map<String,String> getAccountNumbers() {
        return _accountNumbers;
    }
    
    public boolean isValidSourceAccountNumber() {
        return _sourceAccountNumber != null && _accountNumbers.containsValue(_sourceAccountNumber);
    }
    
    public boolean isTransferParameterError() {
        return _sourceAccountNumberError != 0 || _targetAccountNumberError != 0 || _amountError != 0;
    }
}
