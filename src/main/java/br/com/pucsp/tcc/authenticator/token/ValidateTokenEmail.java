package br.com.pucsp.tcc.authenticator.token;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.ConnDB;

public class ValidateTokenEmail {
	private static final Logger LOGGER = LoggerFactory.getLogger(ValidateTokenEmail.class);
	
	public boolean verify(JSONObject userData) throws ClassNotFoundException, SQLException {
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
			LOGGER.error("Invalid token format");
			validate = false;
		}
		
		return validate;
	}
	
	private static void verifyLink(String email, String token, String approve) {
		
	}
	
	private boolean verifyCode(String email, String token, String approve) throws ClassNotFoundException, SQLException {
		Connection connection = null;
	    PreparedStatement statement = null;
	    int rowsUpdated = 0;
	    
	    try {
	        connection = ConnDB.getConnection();
	        
	        String sql = "UPDATE active_codes AC JOIN users U ON AC.id_user = U.id_user JOIN confirm_email CE ON AC.id_user = CE.id_user SET AC.code = ?, AC.updated_at = CURRENT_TIMESTAMP, CE.updated_at = CURRENT_TIMESTAMP, CE.confirmed = true WHERE U.email = ?";

	        statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        statement.setString(1, "0");
	        statement.setString(2, email);
	        
	        rowsUpdated = statement.executeUpdate();
	    }
	    catch (SQLException e) {
	    	LOGGER.error("Error inserting user", e);
	    }
	    finally {
		    if (statement != null) {
		        try {
		        	statement.close();
		        } catch (SQLException e) {
		        	LOGGER.error("Error closing statement", e);
		        }
		    }
		    if (connection != null) {
		        try {
		            ConnDB.closeConnection(connection);
		        } catch (SQLException e) {
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
}