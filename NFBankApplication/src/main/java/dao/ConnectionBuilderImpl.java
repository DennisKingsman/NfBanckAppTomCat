
package dao;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionBuilderImpl implements ConnectionBuilder {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionBuilderImpl.class);
    private DataSource dataSource;

    public ConnectionBuilderImpl() {
        try {
            Context context = new InitialContext();
            this.dataSource = (DataSource)context.lookup("java:comp/env/jdbc/NFDatabase");
        } catch (NamingException var2) {
            logger.error("", var2);
        }

    }

    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }
}
