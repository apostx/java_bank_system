package wup.helper.db.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import wup.helper.data.Transaction;

/**
 * Create data class from result of sql query
 */
public class TransactionMapper implements RowMapper<Transaction> {

    @Override
    public Transaction mapRow(ResultSet rs, int i) throws SQLException {
        return new Transaction(
                rs.getString("account_number"),
                rs.getString("currency"),
                rs.getInt("amount"),
                rs.getInt("out") == 1,
                rs.getInt("balance")
        );
    }
}
