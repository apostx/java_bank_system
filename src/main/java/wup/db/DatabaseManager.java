package wup.db;

import java.sql.Types;
import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
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
 * This class manage all of sql operations
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
    
    public List<Account> getOwnAccounts(int id) {
        return _jdbcTemplate.query(
                "SELECT account_number,currency_id,short_name,balance FROM account_info WHERE user_id = ?",
                new AccountMapper(),
                id);
    }
    
    public List<String> getOwnAccountList(int id) {
        return _jdbcTemplate.query(
                "SELECT account_number FROM account_info WHERE user_id = ?",
                new AccountNumberMapper(),
                id);
    }
    
    public List<Transaction> getTransactions(int id,String accountNumber) {
        return _jdbcTemplate.query(
                "SELECT " + 
                    "CASE WHEN source_account_number = ? THEN target_account_number ELSE source_account_number END AS account_number, " +
                    "currency, " +
                    "amount, " +
                    "CASE WHEN source_account_number = ? THEN 1 ELSE 0 END AS `out`, " +
                    "CASE WHEN source_account_number = ? THEN source_balance ELSE target_balance END AS `balance` "+
                "FROM transaction_info " +
                "WHERE " +
                    "(source_account_number LIKE ? OR target_account_number LIKE ?) " +
                    "AND " +
                    "(source_user_id = ? OR target_user_id = ?) " +
                "ORDER BY `timestamp` DESC",
                new Object[]{accountNumber,accountNumber,accountNumber,accountNumber,accountNumber,id,id},
                new int[]{Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.INTEGER,Types.INTEGER},
                new TransactionMapper());
    }
    
    private int getBalance(String sourceAccount) {
        return _jdbcTemplate.queryForObject(
            "SELECT balance FROM account_info WHERE account_number like ?",
            new Object[]{sourceAccount},
            new int[]{Types.VARCHAR},
            Integer.class);
    }
    
    
    /**
     * The Phase ID show that, where interrupted the process
     * If Phase ID equal 0 than the process was successful
     * 
     * Phase ID means:
     * 0 - succes
     * 2 - invalid amount
     * other - sql error
     * 
     * @return Phase ID
     */
    
    public int transaction(final int id,final String sourceAccount,final String targetAccount,final int amount) {
        return _transactionTemplate.execute(new TransactionCallback<Integer>() {

            @Override
            public Integer doInTransaction(TransactionStatus ts) {
                int phase = 1;
                try{
                    int balance = _jdbcTemplate.queryForObject(
                        "SELECT balance FROM account_info WHERE account_number like ? AND user_id = ?",
                        new Object[]{sourceAccount,id},
                        new int[]{Types.VARCHAR,Types.INTEGER},
                        Integer.class);
                    
                    phase = 2;
                    
                    if (balance < amount)
                        throw new Exception("Too little balance!");
                    
                    phase = 3;
                    
                    _jdbcTemplate.update("UPDATE account SET balance = balance - ? WHERE account_number like ?",
                        new Object[]{amount,sourceAccount},
                        new int[]{Types.INTEGER,Types.VARCHAR});
                    
                    phase = 4;
                    
                    int newSourceBalance = getBalance(sourceAccount);
                    
                    phase = 5;
                    
                    _jdbcTemplate.update("UPDATE account SET balance = balance + ? WHERE account_number like ?",
                        new Object[]{amount,targetAccount},
                        new int[]{Types.INTEGER,Types.VARCHAR});
                    
                    phase = 6;
                    
                    int newTargetBalance = getBalance(targetAccount);
                    
                    phase = 7;
                    
                    _jdbcTemplate.update("INSERT INTO transaction (source_account_number,target_account_number,amount,`timestamp`,source_balance,target_balance) VALUES (?,?,?,NOW(),?,?);",
                        new Object[]{sourceAccount,targetAccount,amount,newSourceBalance,newTargetBalance},
                        new int[]{Types.VARCHAR,Types.VARCHAR,Types.INTEGER,Types.INTEGER,Types.INTEGER});
                    
                    phase = 0;
                }catch (Exception e) {
                    ts.setRollbackOnly();
                }
                
                return phase;
            }
        });
    }
}
