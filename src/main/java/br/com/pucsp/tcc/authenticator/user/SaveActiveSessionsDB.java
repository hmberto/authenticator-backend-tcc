package br.com.pucsp.tcc.authenticator.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.ConnDB;
import br.com.pucsp.tcc.authenticator.rest.RegisterEmail;

public class SaveActiveSessionsDB {
	private static final Logger LOGGER = LoggerFactory.getLogger(RegisterEmail.class);
	
	public void insertActiveSession(long userId, String email, String userToken) throws ClassNotFoundException, SQLException {
	    Connection connection = null;
	    PreparedStatement statement = null;
	    
	    try {
	        connection = ConnDB.getConnection();
	        
	        String sql = "INSERT INTO active_sessions (id_user, token) VALUES (?, ?)";
	        
	        statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        statement.setInt(1, (int) userId);
	        statement.setString(2, userToken);
	        
	        statement.executeUpdate();
	    }
	    catch (SQLException e) {
	    	LOGGER.error("Error inserting session", e);
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