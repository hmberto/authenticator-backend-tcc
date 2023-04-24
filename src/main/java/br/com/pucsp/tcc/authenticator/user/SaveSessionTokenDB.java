package br.com.pucsp.tcc.authenticator.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.ConnDB;
import br.com.pucsp.tcc.authenticator.database.SqlQueries;

public class SaveSessionTokenDB implements AutoCloseable {
	private static final Logger LOGGER = LoggerFactory.getLogger(SaveSessionTokenDB.class);
	
	public boolean insert(int userId, String userEmail, String userSessionToken, boolean isActive) {
	    Connection connection = null;
	    PreparedStatement statement = null;
	    int sessionId = 0;
	    
	    try {
	        connection = ConnDB.getConnection();
	        
	        statement = connection.prepareStatement(SqlQueries.INSERT_SESSION, Statement.RETURN_GENERATED_KEYS);
	        statement.setInt(1, userId);
	        statement.setString(2, userSessionToken);
	        statement.setBoolean(3, isActive);
	        
	        statement.executeUpdate();
	        
	        ResultSet generatedKeys = statement.getGeneratedKeys();
	        if (generatedKeys.next()) {
	        	sessionId = generatedKeys.getInt(1);
	        }
	    }
	    catch (SQLException e) {
	    	LOGGER.error("Error inserting 100 digit token into database - Email: " + userEmail, e);
	    }
	    finally {
		    if(statement != null) {
		        try {
		        	statement.close();
		        } catch (SQLException e) {
		        	LOGGER.error("Error closing statement", e);
		        }
		    }
		    if(connection != null) {
		        try {
		            ConnDB.closeConnection(connection);
		        } catch (SQLException e) {
		        	LOGGER.error("Error closing connection", e);
		        }
		    }
		}
	    
	    if(sessionId >= 1) {
	    	return true;
	    }
	    else {
	    	return false;
	    }
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}
}