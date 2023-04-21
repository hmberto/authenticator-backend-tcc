package br.com.pucsp.tcc.authenticator.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.ConnDB;

public class AppInitializer implements ServletContextListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(AppInitializer.class);
	
	private static final String SQL_SCRIPT = System.getenv("SQL_SCRIPT");
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
			createTables();
			LOGGER.info("Process of creating tables in the database has been successfully completed. Application is ready");
		} catch (SQLException | IOException e) {
			LOGGER.error("Error creating tables in database.", e);
		}
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
	}
	
	private void createTables() throws SQLException, IOException {
		try (Connection connection = ConnDB.getConnection(); Statement statement = connection.createStatement()) {
			Path path = Paths.get(SQL_SCRIPT);
			String sql = new String(Files.readAllBytes(path));
			
			LOGGER.info("Script SQL File Path: {}", path);
			
			String[] sqlStatements = sql.split("\\$");
			
			for (String sqlStatement : sqlStatements) {
				String sqlType = sqlStatement.contains("DROP") ? "DROP" : 
	                 sqlStatement.contains("CREATE") ? "CREATE" : null;
				
				String partes[] = sqlStatement.split("`");
				String tableName = partes[1];
				
				LOGGER.info("Trying to '{}' table '{}'", sqlType, tableName);
				statement.execute(sqlStatement);
				LOGGER.info("'{}' performed successfully on table '{}'", sqlType, tableName);
			}
		}
	}
}