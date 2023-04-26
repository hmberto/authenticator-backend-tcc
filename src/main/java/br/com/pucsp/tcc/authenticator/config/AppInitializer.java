package br.com.pucsp.tcc.authenticator.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.ConnDB;
import br.com.pucsp.tcc.authenticator.database.SqlQueries;

public class AppInitializer implements ServletContextListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(AppInitializer.class);
	
	private static final String SQL_SCRIPT = System.getenv("SQL_SCRIPT");
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
			createTables();
			LOGGER.info("Process of creating tables in the database has been successfully completed. Application is ready");
		} catch (Exception e) {
			LOGGER.error("Error creating tables in database.", e);
		}
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
	}
	
	private void createTables() throws Exception {
		try(ConnDB connDB = ConnDB.getInstance();
				Connection connection = connDB.getConnection();
				Statement statement = connection.createStatement()) {
			String sqlTimeZone = SqlQueries.TIME_ZONE_GLOBAL;
			
			statement.execute(sqlTimeZone);
			
			Path path = Paths.get(SQL_SCRIPT);
			String sql = new String(Files.readAllBytes(path));
			
			LOGGER.info("Script SQL File Path: {}", path);
			
			String[] sqlStatements = sql.split("\\$");
			
			for (String sqlStatement : sqlStatements) {
				String sqlType = getSqlType(sqlStatement);
				String tableName = getTableName(sqlStatement);
				
				LOGGER.info("Trying to '{}' table '{}'", sqlType, tableName);
				statement.execute(sqlStatement);
				LOGGER.info("'{}' performed successfully on table '{}'", sqlType, tableName);
			}
		}
	}
	
	private String getSqlType(String sqlStatement) {
	    if (sqlStatement.contains("DROP")) {
	        return "DROP";
	    } else if (sqlStatement.contains("CREATE")) {
	        return "CREATE";
	    } else if (sqlStatement.contains("SET")) {
	        return "SET";
	    } else {
	        throw new IllegalArgumentException("Unrecognized SQL statement: " + sqlStatement);
	    }
	}
	
	private String getTableName(String sqlStatement) {
	    String[] parts = sqlStatement.split("`");
	    if(parts.length > 1) {
	        return parts[1];
	    }
	    else {
	        return "unknown";
	    }
	}
}