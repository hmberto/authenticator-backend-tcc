package br.com.pucsp.tcc.authenticator.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.pucsp.tcc.authenticator.database.ConnDB;

public class SaveConfirmEmailDB {
	private static final String CLASS_NAME = SaveConfirmEmailDB.class.getSimpleName();
	private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);
	
	public void insertConfirmEmail(long userId, String email, boolean userConfirmed) throws ClassNotFoundException, SQLException {
	    LOGGER.entering(CLASS_NAME, "insertConfirmEmail");
	    
	    Connection connection = null;
	    PreparedStatement statement = null;
	    
	    try {
	        connection = ConnDB.connect();
	        
	        String sql = "INSERT INTO confirm_email (id_user, confirmed) VALUES (?, ?)";
	        
	        statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        statement.setInt(1, (int) userId);
	        statement.setBoolean(2, userConfirmed);
	        
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
	            ConnDB.disconnect();
	        }
	    }
	    
	    LOGGER.exiting(CLASS_NAME, "insertConfirmEmail");
	}
}