package wup.db;

import java.security.InvalidParameterException;
import java.sql.Types;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import wup.core.data.Transaction;
import wup.db.data.AccountMapper;
import wup.db.data.AccountMapper.Account;
import wup.db.data.AccountNumberMapper;
import wup.db.data.TransactionMapper;

/**
 * This class manages all of sql operations
 */
public class DatabaseManager {
    
    private JdbcTemplate _jdbcTemplate;
    private TransactionTemplate _transactionTemplate;
    
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        _jdbcTemplate = jdbcTemplate;
    }
    
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        _transactionTemplate = transactionTemplate;
    }
    
    public int getUserID(String name,String password) {
        try {
            return _jdbcTemplate.queryForObject(
                    "SELECT id FROM user WHERE name LIKE ? AND password LIKE ?",
                    new Object[]{name,new Md5PasswordEncoder(){}.encodePassword(password,null)},
                    new int[]{Types.VARCHAR,Types.VARCHAR},
                    Integer.class);
        } catch(EmptyResultDataAccessException e) {
            return 0;
        }
    }
    
    public int getAccountCurrencyID(String accountNumber) {
        try {
            return _jdbcTemplate.queryForObject(
                    "SELECT currency_id FROM account_info WHERE account_number LIKE ?",
                    new Object[]{accountNumber},
                    new int[]{Types.VARCHAR},
                    Integer.class);
        } catch(EmptyResultDataAccessException e) {
            return 0;
        }
    }
    
    public List<Account> getOwnAccountList(int id) {
        return _jdbcTemplate.query(
                "SELECT account_number,currency_id,short_name,balance FROM account_info WHERE user_id = ? ORDER BY account_number ASC",
                new AccountMapper(),
                id);
    }
    
    public List<String> getOwnAccountNumberList(int id) {
        return _jdbcTemplate.query(
                "SELECT account_number FROM account WHERE user_id = ? ORDER BY account_number ASC",
                new AccountNumberMapper(),
                id);
    }
    
    public List<Transaction> getTransactions(int id,String accountNumber) {
        StringBuilder sql = new StringBuilder("");
            sql.append( "SELECT ");
            sql.append(     "CASE WHEN source_account_number = ? THEN target_account_number ELSE source_account_number END AS account_number, ");
            sql.append(     "currency, " );            
            sql.append(     "amount, " );
            sql.append(     "CASE WHEN source_account_number = ? THEN 1 ELSE 0 END AS `out`, ");
            sql.append(     "CASE WHEN source_account_number = ? THEN source_balance ELSE target_balance END AS `balance` ");
            sql.append( "FROM transaction_info ");
            sql.append( "WHERE " );
            sql.append(         "(source_account_number LIKE ? OR target_account_number LIKE ?) " );
            sql.append(     "AND " );
            sql.append(         "(source_user_id = ? OR target_user_id = ?) " );
            sql.append( "ORDER BY `timestamp` DESC" );
        
        return _jdbcTemplate.query(
                sql.toString(),
                new Object[]{accountNumber,accountNumber,accountNumber,accountNumber,accountNumber,id,id},
                new int[]{Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.INTEGER,Types.INTEGER},
                new TransactionMapper());
    }
    
    /**
     * Validating the parameters and transfer amount from source account to target account
     * 
     * Steps:
     * @see TransferStep#CHECK_SOURCE_ACCOUNT
     * @see TransferStep#CHECK_SOURCE_BALANCE
     * @see TransferStep#CHECK_DIFFERENT_ACCOUNT
     * @see TransferStep#CHECK_TARGET_ACCOUNT
     * @see TransferStep#CHECK_CURRENCY
     * @see TransferStep#SUBTRACTION
     * @see TransferStep#ADDITION
     * @see TransferStep#LOGGING
     * @see TransferStep#COMPLETE
     */
    public TransferResult transfer(final int userID,final String sourceAccountNumber,final String targetAccountNumber,final int amount) {
        return _transactionTemplate.execute(new TransactionCallback<TransferResult>() {

            @Override
            public TransferResult doInTransaction(TransactionStatus ts) {
                TransferResult result = TransferResult.SUCCESSFUL;
                TransferStep step = null;
                int rowNum;
                
                try {
                    // 1 - Check source account
                    step = TransferStep.CHECK_SOURCE_ACCOUNT;
                    Account sourceAccount = _jdbcTemplate.queryForObject(
                            "SELECT account_number,currency_id,short_name,balance FROM account_info WHERE user_id = ? AND account_number LIKE ?",
                            new Object[]{userID,sourceAccountNumber},
                            new int[]{Types.INTEGER,Types.VARCHAR},
                            new AccountMapper());
                    
                    // 2 -  Check source balance
                    step = TransferStep.CHECK_SOURCE_BALANCE;
                    if(sourceAccount.getBalance() < amount)
                        throw new InvalidParameterException();
                    
                    // 3 - Check target account number is different
                    step = TransferStep.CHECK_DIFFERENT_ACCOUNT;
                    if(targetAccountNumber.equals(sourceAccountNumber))
                        throw new InvalidParameterException();
                    
                    // 4 - Check target account
                    step = TransferStep.CHECK_TARGET_ACCOUNT;
                    Account targetAccount = _jdbcTemplate.queryForObject(
                            "SELECT account_number,currency_id,short_name,balance FROM account_info WHERE account_number LIKE ?",
                            new Object[]{targetAccountNumber},
                            new int[]{Types.VARCHAR},
                            new AccountMapper());
                    
                    // 5 - Check target currency
                    step = TransferStep.CHECK_CURRENCY;
                    System.out.println(targetAccount.getCurrencyID());
                    System.out.println(sourceAccount.getCurrencyID());
                    if (targetAccount.getCurrencyID() != sourceAccount.getCurrencyID())
                        throw new InvalidParameterException();
                    
                    // 6 - Subtraction
                    step = TransferStep.SUBTRACTION;
                    rowNum = _jdbcTemplate.update(
                        "UPDATE account SET balance = balance - ? WHERE account_number LIKE ? AND ? <= balance",
                        new Object[]{amount,sourceAccountNumber,amount},
                        new int[]{Types.INTEGER,Types.VARCHAR,Types.INTEGER});

                    manageTransferException(rowNum);
                    
                    // 7 - Addition
                    step = TransferStep.ADDITION;
                    rowNum = _jdbcTemplate.update(
                        "UPDATE account SET balance = balance + ? WHERE account_number LIKE ?",
                        new Object[]{amount,targetAccountNumber},
                        new int[]{Types.INTEGER,Types.VARCHAR});

                    manageTransferException(rowNum);
                    
                    // 8 - Logging
                    step = TransferStep.LOGGING;
                    
                    StringBuilder sql = new StringBuilder("");
                    sql.append( "INSERT INTO transaction " );
                    sql.append(     "(source_account_number, target_account_number, amount, `timestamp`, source_balance, target_balance) " );
                    sql.append(     "SELECT source.account_number, target.account_number, ?, NOW(), source.balance, target.balance " );            
                    sql.append(         "FROM account AS source, account AS target " );
                    sql.append(         "WHERE source.account_number LIKE ? AND target.account_number LIKE ? " );
                    
                    rowNum = _jdbcTemplate.update(
                        sql.toString(),
                        new Object[]{amount,sourceAccountNumber,targetAccountNumber},
                        new int[]{Types.INTEGER,Types.VARCHAR,Types.VARCHAR});
                    
                    manageTransferException(rowNum);
                } catch (Exception e) {
                    result = generateTransferResultWhenException(step,e);
                    System.out.println(e);
                    System.out.println(e.getMessage());
                    ts.setRollbackOnly();
                }

                return result;
            }
        });
    }
    
    public enum TransferErrorType {
        NONE(0),
        DATABASE_RUNTIME(1),
        INVALID_PARAMTER(2),
        INCONSISTENT_DATABASE(3),
        UNKNOWN(4);
        
        private final int _code;
        private TransferErrorType(int code) {
            _code = code;
        }
        
        public int getCode() {
            return _code;
        }
    }
    
    public enum TransferStep {
        /**
         * Check existence and owner of source account
         */
        CHECK_SOURCE_ACCOUNT(1),
        
        /**
         * Check balance of source account is greater than amount
         */
        CHECK_SOURCE_BALANCE(2),
        
        /**
         * Check target account number different from source account number
         */
        CHECK_DIFFERENT_ACCOUNT(3),
        
        /**
         * Check existence of target account
         */
        CHECK_TARGET_ACCOUNT(4),
        
        /**
         * Check currency id of target account is same as currency id of source account
         */
        CHECK_CURRENCY(5),
        
        /**
         * Substract amount from balance of source account
         */
        SUBTRACTION(6),
        
        /**
         * Add amount to balance of target account
         */
        ADDITION(7),
        
        /**
         * Make a database entry about transfer
         */
        LOGGING(8),
        
        /**
         * The transfer is successfully closed
         */
        COMPLETE(0);
        
        private final int _code;
        private TransferStep(int code) {
            _code = code;
        }
        
        public int getCode() {
            return _code;
        }
    }
    
    public static class TransferResult {
        
        public final static TransferResult SUCCESSFUL = new TransferResult(TransferStep.COMPLETE,TransferErrorType.NONE);
        
        private final TransferStep _step;
        private final TransferErrorType _errorType;
        
        public TransferResult(TransferStep step, TransferErrorType errorType) {
            _step = step;
            _errorType = errorType;
        }
        
        public TransferStep getStep() {
            return _step;
        }
        
        public TransferErrorType getErrorType() {
            return _errorType;
        }
    }
    
    private TransferResult generateTransferResultWhenException(TransferStep step,Exception exception) {
        TransferErrorType errorType = TransferErrorType.UNKNOWN;
        
        if (IncorrectResultSizeDataAccessException.class.isInstance(exception)) {
            IncorrectResultSizeDataAccessException ex = (IncorrectResultSizeDataAccessException) exception;
            errorType = ex.getActualSize() < ex.getExpectedSize() ? TransferErrorType.INVALID_PARAMTER : TransferErrorType.INCONSISTENT_DATABASE;
        
        } else if (InvalidParameterException.class.isInstance(exception))
            errorType = TransferErrorType.INVALID_PARAMTER;
        
        else if (DataAccessException.class.isInstance(exception))
            errorType = TransferErrorType.DATABASE_RUNTIME;
              
        return new TransferResult(step,errorType);
    }
    
    private void manageTransferException(int actualSize) throws IncorrectResultSizeDataAccessException {
        manageTransferException(1,actualSize);
    }
    
    private void manageTransferException(int expectedSize,int actualSize) throws IncorrectResultSizeDataAccessException {
        if (expectedSize != actualSize)
            throw new IncorrectResultSizeDataAccessException(expectedSize,actualSize);
    }
}
