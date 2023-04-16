package br.com.pucsp.tcc.authenticator.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.pucsp.tcc.authenticator.database.ConnDB;

public class UpdateUserNameDB {
	private static final String CLASS_NAME = SaveActiveSessionsDB.class.getSimpleName();
	private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);
	
	public boolean newName(String userName, String email, String userToken) throws ClassNotFoundException, SQLException {
	    LOGGER.entering(CLASS_NAME, "newName");
	    
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
	    
	    LOGGER.exiting(CLASS_NAME, "newName");
	    if (rowsUpdated > 0) {
	        return true;
	    } else {
	        return false;
	    }
	}
}