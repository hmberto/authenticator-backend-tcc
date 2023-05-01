package br.com.pucsp.tcc.authenticator.database;

import org.apache.commons.dbcp2.BasicDataSource;

import br.com.pucsp.tcc.authenticator.utils.system.SystemDefaultVariables;

public class DataSource {
	private static final String DB_HOST = SystemDefaultVariables.dbHost;
	private static final String DB_NAME = SystemDefaultVariables.dbName;
	private static final String DB_USER = SystemDefaultVariables.dbUser;
	private static final String DB_PASS = SystemDefaultVariables.dbPass;

	private static final BasicDataSource dataSource;

	static {
		dataSource = new BasicDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://" + DB_HOST + "/" + DB_NAME + "?sslMode=VERIFY_IDENTITY");
		dataSource.setUsername(DB_USER);
		dataSource.setPassword(DB_PASS);
		dataSource.setInitialSize(5);
		dataSource.setMaxTotal(10);
		dataSource.setMaxIdle(5);
		dataSource.setMinIdle(2);
		dataSource.setMaxWaitMillis(5000);
	}

	private DataSource() {}

	public static BasicDataSource getInstance() {
		return dataSource;
	}
}