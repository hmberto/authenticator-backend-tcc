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
import br.com.pucsp.tcc.authenticator.exceptions.InvalidNameException;
import br.com.pucsp.tcc.authenticator.exceptions.InvalidSessionTokenOrOTPException;
import br.com.pucsp.tcc.authenticator.utils.DataValidator;

public class UpdateUserNameDB {
	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateUserNameDB.class);
	
	private static final int SESSION_LENGTH = Integer.parseInt(System.getenv("SESSION_LENGTH"));
	
	public boolean update(JSONObject body) throws Exception {
		String userFirstName = body.getString("firstName").trim();
		String userLastName = body.getString("lastName").trim();
		String userEmail = body.getString("email").trim().toLowerCase();
		String userSessionToken = body.getString("session").trim().toUpperCase();
		
		Connection connection = null;
	    PreparedStatement statement = null;
	    int rowsUpdated = 0;
	    
	    validateBody(userFirstName, userLastName, userEmail, userSessionToken);
	    
	    try {
	        connection = ConnDB.getConnection();
	        
	        statement = connection.prepareStatement(SqlQueries.UPDATE_NAME, Statement.RETURN_GENERATED_KEYS);
	        statement.setString(1, userFirstName);
	        statement.setString(2, userLastName);
	        statement.setString(3, userEmail);
	        statement.setString(4, userSessionToken);
	        
	        rowsUpdated = statement.executeUpdate();
	    }
	    catch (SQLException e) {
	    	LOGGER.error("Error updating user name", e);
	    }
	    finally {
		    if(statement != null) {
		        try {
		        	statement.close();
		        }
		        catch (SQLException e) {
		        	LOGGER.error("Error closing statement", e);
		        }
		    }
		    if(connection != null) {
		        try {
		            ConnDB.closeConnection(connection);
		        }
		        catch (SQLException e) {
		        	LOGGER.error("Error closing connection", e);
		        }
		    }
		}
	    
	    if (rowsUpdated > 0) {
	        return true;
	    } else {
	        return false;
	    }
	}
	
	private static void validateBody(String userFirstName, String userLastName, String userEmail, String userSessionToken) throws Exception {
		if(!DataValidator.isValidUsername(userFirstName)) {
			throw new InvalidNameException("Invalid first name format");
		}
		if(!DataValidator.isValidUsername(userLastName)) {
			throw new InvalidNameException("Invalid last name format");
		}
		if(!DataValidator.isValidEmail(userEmail)) {
			throw new InvalidEmailException("Invalid email format");
		}
		if(!DataValidator.isValidToken(userSessionToken) || userSessionToken.length() != SESSION_LENGTH) {
			throw new InvalidSessionTokenOrOTPException("Invalid Session Token format");
		}
		String fullName = userFirstName + " " + userLastName;
		if(!fullName.matches("^[\\p{L}]+( [\\p{L}]+)+$")) {
			throw new InvalidNameException("Name must have two words");
		}
	}
}