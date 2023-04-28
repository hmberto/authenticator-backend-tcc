package br.com.pucsp.tcc.authenticator.utils.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.ConnDB;
import br.com.pucsp.tcc.authenticator.database.SqlQueries;

public class AppInitializer implements ServletContextListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(AppInitializer.class);
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
			createTables();
			LOGGER.info("Database started successfully");
			if(validateEnvironmentVariable()) {
				LOGGER.info("Application may not work properly - unconfigured environment variables");
			}
			else {
				LOGGER.info("Environment variables OK");
			}
		}
		catch(SQLException e) {
			LOGGER.error("Unable to initialize the database");
		}
		catch(Exception e) {
			LOGGER.error("Application started with errors");
		}
		
		LOGGER.info("Application is ready");
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
	}
	
	private static boolean validateEnvironmentVariable() {
		boolean error = false;
		
		List<String> variableList = variableList();
		
		LOGGER.info("--------------------------------------------");
		LOGGER.info("Checking environment variables");
		for(String variableName : variableList) {
			String variableValue = System.getenv(variableName);
			if(variableValue == null || variableValue.isEmpty()) {
				LOGGER.warn(">> Variable '{}' not set correctly", variableName);
				error = true;
			}
			else {
				LOGGER.info(">> Variable '{}' OK", variableName);
			}
		}
		LOGGER.info("--------------------------------------------");
		
		return error;
	}
	
	private static List<String> variableList() {
		List<String> variable = new ArrayList<String>();
		variable.add("EMAIL_BOX");
		variable.add("EMAIL_PASS");
		variable.add("EMAIL_SERVER");
		variable.add("DB_HOST");
		variable.add("DB_NAME");
		variable.add("DB_USER");
		variable.add("DB_PASS");
		variable.add("OTP_LENGTH");
		variable.add("SESSION_LENGTH");
		variable.add("EMAIL_TOKEN_LENGTH");
		variable.add("SQL_SCRIPT");
		variable.add("SITE_HOST");
		variable.add("TIME_ZONE");
		
		return variable;
	}
	
	private void createTables() throws Exception {
		try(ConnDB connDB = ConnDB.getInstance();
				Connection connection = connDB.getConnection();
				Statement statement = connection.createStatement()) {
			String sqlTimeZone = SqlQueries.TIME_ZONE_GLOBAL;
			
			statement.execute(sqlTimeZone);
			
			Path path = Paths.get(System.getenv("SQL_SCRIPT"));
			String sql = new String(Files.readAllBytes(path));
			
			LOGGER.info("Script SQL File Path: {}", path);
			LOGGER.info("--------------------------------------------");
			LOGGER.info("Checking database tables");
			
			String[] sqlStatements = sql.split("\\$");
			
			for (String sqlStatement : sqlStatements) {
				String sqlType = getSqlType(sqlStatement);
				String tableName = getTableName(sqlStatement);
				
				LOGGER.info(">> Trying to '{}' table '{}'", sqlType, tableName);
				statement.execute(sqlStatement);
				LOGGER.info(">> '{}' performed successfully on table '{}'", sqlType, tableName);
			}
			
			LOGGER.info("--------------------------------------------");
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