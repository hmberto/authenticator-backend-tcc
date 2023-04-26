package br.com.pucsp.tcc.authenticator.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import org.json.JSONObject;
import br.com.pucsp.tcc.authenticator.database.ConnDB;
import br.com.pucsp.tcc.authenticator.database.SqlQueries;
import br.com.pucsp.tcc.authenticator.exceptions.InvalidEmailException;
import br.com.pucsp.tcc.authenticator.exceptions.InvalidNameException;
import br.com.pucsp.tcc.authenticator.exceptions.InvalidTokenException;
import br.com.pucsp.tcc.authenticator.utils.DataValidator;

public class UpdateUserNameDB {
	private static final int SESSION_LENGTH = Integer.parseInt(System.getenv("SESSION_LENGTH"));
	
	public boolean update(JSONObject body) throws Exception {
		String userFirstName = body.getString("firstName").trim();
		String userLastName = body.getString("lastName").trim();
		String userEmail = body.getString("email").trim().toLowerCase();
		String userSessionToken = body.getString("session").trim().toUpperCase();
		
		int rowsUpdated = 0;
	    
	    validateBody(userFirstName, userLastName, userEmail, userSessionToken);
	    
	    try(ConnDB connDB = ConnDB.getInstance();
				Connection connection = connDB.getConnection();
	    		PreparedStatement statement = connection.prepareStatement(SqlQueries.UPDATE_NAME, Statement.RETURN_GENERATED_KEYS);) {
	    	
	    	statement.setString(1, userFirstName);
	    	statement.setString(2, userLastName);
	    	statement.setString(3, userEmail);
	    	statement.setString(4, userSessionToken);
	    	
	    	rowsUpdated = statement.executeUpdate();
	    }
	    
	    return rowsUpdated > 0 ? true : false;
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
			throw new InvalidTokenException("Invalid Session Token format");
		}
		String fullName = userFirstName + " " + userLastName;
		if(!fullName.matches("^[\\p{L}]+( [\\p{L}]+)+$")) {
			throw new InvalidNameException("Name must have two words");
		}
	}
}