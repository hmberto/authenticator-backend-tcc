package br.com.pucsp.tcc.authenticator.token;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

import br.com.pucsp.tcc.authenticator.database.ConnDB;
import br.com.pucsp.tcc.authenticator.user.SaveActiveCodesDB;

public class ValidateTokenEmail {
	private static final String CLASS_NAME = SaveActiveCodesDB.class.getSimpleName();
	private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);
	
	public boolean verify(JSONObject userData) throws ClassNotFoundException, SQLException {
		LOGGER.entering(CLASS_NAME, "verify");
		
		boolean validate = false;
		
		String email = userData.getString("email");
		String token = userData.getString("token");
		String approve = userData.getString("approve");
		
		if(token.length() == 100) {
			ValidateTokenEmail.verifyLink(email, token, approve);
		}
		else if(token.length() == 6) {
			ValidateTokenEmail validateTokenEmail = new ValidateTokenEmail();
			validate = validateTokenEmail.verifyCode(email, token, approve);
		}
		else {
			LOGGER.log(Level.SEVERE, "ValidateTokenEmail.verify: Invalid token format");
			validate = false;
		}
		
		LOGGER.exiting(CLASS_NAME, "verify");
		return validate;
	}
	
	private static void verifyLink(String email, String token, String approve) {
		
	}
	
	private boolean verifyCode(String email, String token, String approve) throws ClassNotFoundException, SQLException {
		LOGGER.entering(CLASS_NAME, "verifyCode");
	    
	    Connection connection = null;
	    PreparedStatement statement = null;
	    int rowsUpdated = 0;
	    
	    try {
	        connection = ConnDB.connect();
	        
	        String sql = "UPDATE active_codes AC JOIN users U ON AC.id_user = U.id_user JOIN confirm_email CE ON AC.id_user = CE.id_user SET AC.code = ?, AC.updated_at = CURRENT_TIMESTAMP, CE.updated_at = CURRENT_TIMESTAMP, CE.confirmed = true WHERE U.email = ?";

	        statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        statement.setString(1, "0");
	        statement.setString(2, email);
	        
	        rowsUpdated = statement.executeUpdate();
	    }
	    catch (SQLException e) {
	        LOGGER.log(Level.SEVERE, "Error inserting user", e);
	        throw new SQLException(e);
	    }
	    finally {
	        if (statement != null) {
	            try {
	                statement.close();
	            } catch (SQLException e) {
	                LOGGER.log(Level.SEVERE, "Error closing statement", e);
	            }
	        }
	        if (connection != null) {
	            ConnDB.disconnect();
	        }
	    }
	    
	    LOGGER.exiting(CLASS_NAME, "verifyCode");
	    if (rowsUpdated > 0) {
	        return true;
	    } else {
	        return false;
	    }
	}
}