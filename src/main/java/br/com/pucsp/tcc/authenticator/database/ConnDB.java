package br.com.pucsp.tcc.authenticator.database;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.utils.system.SystemDefaultVariables;

public class ConnDB implements AutoCloseable {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConnDB.class);

	private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://{HOST}/{DB_NAME}?sslMode=VERIFY_IDENTITY";
	private static final String DB_HOST = SystemDefaultVariables.dbHost;
	private static final String DB_NAME = SystemDefaultVariables.dbName;
	private static final String DB_USER = SystemDefaultVariables.dbUser;
	private static final String DB_PASS = SystemDefaultVariables.dbPass;
	private static final int MAX_POOL_SIZE = 250;

	private static DataSource dataSource;
	private Connection connection;

	private ConnDB() {}

	private static ConnDB instance;

	public static ConnDB getInstance() {
		if (instance == null) {
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
		LOGGER.info("Database connection status: {}", connection.isClosed() ? "closed" : "open");
		if (connection != null && !connection.isClosed()) {
			connection.close();
			LOGGER.info("Connection closed");
		}
	}
}