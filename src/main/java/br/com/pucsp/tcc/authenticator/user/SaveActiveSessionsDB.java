package br.com.pucsp.tcc.authenticator.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.ConnDB;

public class SaveActiveSessionsDB implements AutoCloseable {
	private static final Logger LOGGER = LoggerFactory.getLogger(SaveActiveSessionsDB.class);
	
	public int insertActiveSession(int userId, String userEmail, String userSessionToken, boolean active) {
	    Connection connection = null;
	    PreparedStatement statement = null;
	    int sessionId = 0;
	    
	    try {
	        connection = ConnDB.getConnection();
	        
	        String sql = "INSERT INTO sessions (user_id, session, is_active) VALUES (?, ?, ?)";
	        
	        statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        statement.setInt(1, userId);
	        statement.setString(2, userSessionToken);
	        statement.setBoolean(3, active);
	        
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
	    
	    return sessionId;
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}
}