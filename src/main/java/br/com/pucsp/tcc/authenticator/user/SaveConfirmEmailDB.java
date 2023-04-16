package br.com.pucsp.tcc.authenticator.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.ConnDB;
import br.com.pucsp.tcc.authenticator.rest.RegisterEmail;

public class SaveConfirmEmailDB {
	private static final Logger LOGGER = LoggerFactory.getLogger(RegisterEmail.class);
	
	public void insertConfirmEmail(long userId, String email, boolean userConfirmed) throws ClassNotFoundException, SQLException {
	    Connection connection = null;
	    PreparedStatement statement = null;
	    
	    try {
	        connection = ConnDB.getConnection();
	        
	        String sql = "INSERT INTO confirm_email (id_user, confirmed) VALUES (?, ?)";
	        
	        statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        statement.setInt(1, (int) userId);
	        statement.setBoolean(2, userConfirmed);
	        
	        statement.executeUpdate();
	    }
	    catch (SQLException e) {
	    	LOGGER.error("Error confirming email", e);
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
	}
}