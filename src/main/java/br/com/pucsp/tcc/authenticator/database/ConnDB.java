package br.com.pucsp.tcc.authenticator.database;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnDB implements AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnDB.class);
    
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://{HOST}/{DB_NAME}?sslMode=VERIFY_IDENTITY";
    private static final String DB_HOST = System.getenv("DB_HOST");
    private static final String DB_NAME = System.getenv("DB_NAME");
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASS = System.getenv("DB_PASS");
    private static final int MAX_POOL_SIZE = 250;

    private static DataSource dataSource;
    private Connection connection;
    
    private ConnDB() {}
    
    private static ConnDB instance;
    public static ConnDB getInstance() {
        if(instance == null) {
            instance = new ConnDB();
        }
        return instance;
    }

    private static DataSource getDataSource() {
        if (dataSource == null) {
        	String dbURL = DB_URL.replace("{HOST}", DB_HOST).replace("{DB_NAME}", DB_NAME);
        	
            BasicDataSource basicDataSource = new BasicDataSource();
            basicDataSource.setDriverClassName(DB_DRIVER);
            basicDataSource.setUrl(dbURL);
            basicDataSource.setUsername(DB_USER);
            basicDataSource.setPassword(DB_PASS);
            basicDataSource.setMaxTotal(MAX_POOL_SIZE);
            dataSource = basicDataSource;
            
            LOGGER.info("DataSource created - Database Type: MySQL, Database URL: " + dbURL);
        }
        return dataSource;
    }

    public Connection getConnection() throws SQLException {
    	if (connection == null || connection.isClosed()) {
    		connection = getDataSource().getConnection();
    		LOGGER.info("Connection established");
    	}
    	
        return connection;
    }

	@Override
	public void close() throws Exception {
		LOGGER.info("AutoCloseable - Is connection closed: '{}'", connection.isClosed());
		if (connection != null && !connection.isClosed()) {
            connection.close();
            LOGGER.info("Connection closed");
        }
	}
}