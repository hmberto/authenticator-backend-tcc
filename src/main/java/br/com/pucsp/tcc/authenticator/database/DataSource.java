package br.com.pucsp.tcc.authenticator.database;

import org.apache.commons.dbcp2.BasicDataSource;

public class DataSource {
    private static final String DB_HOST = System.getenv("DB_HOST");
    private static final String DB_NAME = System.getenv("DB_NAME");
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASS = System.getenv("DB_PASS");

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