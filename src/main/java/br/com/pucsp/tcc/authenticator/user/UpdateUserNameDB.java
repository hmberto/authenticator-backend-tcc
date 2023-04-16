package br.com.pucsp.tcc.authenticator.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.ConnDB;
import br.com.pucsp.tcc.authenticator.rest.RegisterEmail;

public class UpdateUserNameDB {
	private static final Logger LOGGER = LoggerFactory.getLogger(RegisterEmail.class);
	
	public boolean newName(String userName, String email, String userToken) throws ClassNotFoundException, SQLException {
	    Connection connection = null;
	    PreparedStatement statement = null;
	    int rowsUpdated = 0;
	    
	    try {
	        connection = ConnDB.getConnection();
	        
	        String sql = "UPDATE users SET name = ? WHERE email = ? AND id_user IN (SELECT id_user FROM active_sessions WHERE token = ?)";

	        statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        statement.setString(1, userName);
	        statement.setString(2, email);
	        statement.setString(3, userToken);
	        
	        rowsUpdated = statement.executeUpdate();
	    }
	    catch (SQLException e) {
	    	LOGGER.error("Error updating user name", e);
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