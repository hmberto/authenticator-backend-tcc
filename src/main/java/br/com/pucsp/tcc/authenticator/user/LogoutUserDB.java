package br.com.pucsp.tcc.authenticator.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.ConnDB;
import br.com.pucsp.tcc.authenticator.database.SqlQueries;
import br.com.pucsp.tcc.authenticator.exceptions.InvalidEmailException;
import br.com.pucsp.tcc.authenticator.exceptions.InvalidTokenException;
import br.com.pucsp.tcc.authenticator.utils.DataValidator;

public class LogoutUserDB {
	private static final Logger LOGGER = LoggerFactory.getLogger(LogoutUserDB.class);
	
	private static final int SESSION_LENGTH = Integer.parseInt(System.getenv("SESSION_LENGTH"));
	
	public boolean logout(final JSONObject body) throws Exception {
		String userEmail = body.getString("email").trim().toLowerCase();
		String userSessionToken = body.getString("session").trim().toUpperCase();
		boolean isSelectedKillAll = body.getBoolean("killAll");
		
		Connection connection = null;
	    PreparedStatement statement = null;
	    int rowsUpdated = 0;
	    
	    validateBody(userEmail, userSessionToken);
	    
	    try {
	        connection = ConnDB.getConnection();
	        
	        String sql = null;
	        
	        sql = SqlQueries.UPDATE_SESSION_LOGOUT_ONE;
	        
	        if(isSelectedKillAll) {
	        	sql = SqlQueries.UPDATE_SESSION_LOGOUT_ALL;
	        }
	        
	        statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        statement.setString(1, userSessionToken);
	        statement.setString(2, userEmail);
	        
	        rowsUpdated = statement.executeUpdate();
	    }
	    catch(SQLException e) {
	    	LOGGER.error("Error logging out user", e);
	    }
	    finally {
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
	    else{
	        return false;
	    }
	}
	
	private static void validateBody(String userEmail, String userSessionToken) throws Exception {
		if(!DataValidator.isValidEmail(userEmail)) {
			throw new InvalidEmailException("Invalid email format");
		}
		if(!DataValidator.isValidToken(userSessionToken) || userSessionToken.length() != SESSION_LENGTH) {
			throw new InvalidTokenException("Invalid Session Token format");
		}
	}
}