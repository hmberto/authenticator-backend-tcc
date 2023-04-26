package br.com.pucsp.tcc.authenticator.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.ConnDB;
import br.com.pucsp.tcc.authenticator.database.SqlQueries;

public class EmailTokenManagerDB implements AutoCloseable {
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailTokenManagerDB.class);
	
	private Connection connection;
	
	public EmailTokenManagerDB() throws SQLException {
		this.connection = ConnDB.getConnection();
	}
	
	public boolean insert(int userId, String userEmail, String userSessionToken, String userEmailToken) {
		PreparedStatement statementDelete = null;
		PreparedStatement statement = null;
	    int rowsUpdated = 0;
	    
	    try {
	    	statementDelete = connection.prepareStatement(SqlQueries.DELETE_EMAIL_TOKENS);
	        statementDelete.setInt(1, userId);
	        int rowsDeleted = statementDelete.executeUpdate();
	        LOGGER.info("'{}' access confirmations deleted from the database for user '{}'", rowsDeleted, userEmail);
	        
	    	statement = connection.prepareStatement(SqlQueries.UPDATE_APPROVE_LOGIN, Statement.RETURN_GENERATED_KEYS);
	        statement.setString(1, userSessionToken);
	        statement.setString(2, userEmail);
	        statement.setString(3, userEmailToken);
	        
	        rowsUpdated = statement.executeUpdate();
	    }
	    catch(SQLException e) {
	    	LOGGER.error("Error updating Email Token - Email: " + userEmail, e);
	    }
	    finally {
	    	if(statementDelete != null) {
		    	try {
		    		statementDelete.close();
		        }
		        catch(SQLException e) {
		        	LOGGER.error("Error closing statement", e);
		        }
		    }
	    	if(statement != null) {
		    	try {
		        	statement.close();
		        }
		        catch(SQLException e) {
		        	LOGGER.error("Error closing statement", e);
		        }
		    }
		    if(connection != null) {
		    	try {
		        	ConnDB.closeConnection(connection);
		        }
		        catch(SQLException e) {
		        	LOGGER.error("Error closing connection", e);
		        }
		    }
	    }
	    
	    if(rowsUpdated > 0) {
	    	return true;
	    }
	    else {
	    	return false;
	    }
	}
	
	@Override
	public void close() throws Exception {
		if(connection != null) {
			ConnDB.closeConnection(connection);
		}
	}
}