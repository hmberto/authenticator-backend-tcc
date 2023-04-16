package br.com.pucsp.tcc.authenticator.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.pucsp.tcc.authenticator.database.ConnDB;

public class SaveActiveSessionsDB {
	private static final String CLASS_NAME = SaveActiveSessionsDB.class.getSimpleName();
	private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);
	
	public void insertActiveSession(long userId, String email, String userToken) throws ClassNotFoundException, SQLException {
	    LOGGER.entering(CLASS_NAME, "insertActiveSession");
	    
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
		        try {
		            ConnDB.closeConnection(connection);
		        } catch (SQLException e) {
		            LOGGER.log(Level.SEVERE, "Error closing connection", e);
		        }
		    }
		}
	    
	    LOGGER.exiting(CLASS_NAME, "insertActiveSession");
	}
}