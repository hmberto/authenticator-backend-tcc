package br.com.pucsp.tcc.authenticator.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.ConnDB;

public class UpdateUserNameDB {
	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateUserNameDB.class);
	
	public boolean update(String userFirstName, String userLastName, String userEmail, String userToken) throws SQLException {
	    Connection connection = null;
	    PreparedStatement statement = null;
	    int rowsUpdated = 0;
	    
	    try {
	        connection = ConnDB.getConnection();
	        
	        String sql = "UPDATE users SET first_name = ?, last_name = ?\n"
	        		+ "WHERE email = ?\n"
	        		+ "AND user_id IN (SELECT user_id FROM sessions WHERE session = ? AND is_active = true)";
	        
	        statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        statement.setString(1, userFirstName);
	        statement.setString(2, userLastName);
	        statement.setString(3, userEmail);
	        statement.setString(4, userToken);
	        
	        rowsUpdated = statement.executeUpdate();
	    }
	    catch (SQLException e) {
	    	LOGGER.error("Error updating user name", e);
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
	    
	    if (rowsUpdated > 0) {
	        return true;
	    } else {
	        return false;
	    }
	}
}