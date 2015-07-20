package wup.db.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;


/**
 * Create data class from result of sql query
 */
public class AccountNumberMapper implements RowMapper<String> {
    
    @Override
    public String mapRow(ResultSet rs, int i) throws SQLException {
        return rs.getString("account_number");
    }
}
