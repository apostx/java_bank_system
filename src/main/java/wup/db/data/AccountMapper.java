package wup.db.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import wup.db.data.AccountMapper.Account;
import wup.utils.FormatString;


/**
 * Create data object from result of sql query 
 */
public class AccountMapper implements RowMapper<Account> {
    
    @Override
    public Account mapRow(ResultSet rs, int i) throws SQLException {
        return new Account(rs.getString("account_number"),rs.getInt("currency_id"),rs.getString("short_name"),rs.getInt("balance"));
    }
    
    /**
     * Data object
     */
    public class Account {
        
        private String _accountNumber;
        private int _currencyID;
        private String _currencyShort;
        private int _balance;
        
        private Account(String accountNumber,int currencyID,String currencyShort,int balance) {
            _accountNumber = accountNumber;
            _currencyID = currencyID;
            _currencyShort = currencyShort;
            _balance = balance;
        }
        
        public String getAccountNumber() {
            return _accountNumber;
        }
        
        public String getFormattedAccountNumber() {
            return FormatString.accountNumber(_accountNumber);
        }

        public int getCurrencyID() {
            return _currencyID;
        }
        
        public String getCurrencyShort() {
            return _currencyShort;
        }
        
        public int getBalance() {
            return _balance;
        }
    }
}
