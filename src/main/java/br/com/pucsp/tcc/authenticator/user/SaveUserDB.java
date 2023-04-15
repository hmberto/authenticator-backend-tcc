package br.com.pucsp.tcc.authenticator.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.pucsp.tcc.authenticator.database.ConnDB;

public class SaveUserDB {
	private static final String CLASS_NAME = SaveUserDB.class.getSimpleName();
	private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);
	
	public long insertUser(String userName, String userEmail) throws ClassNotFoundException, SQLException {
	    LOGGER.entering(CLASS_NAME, "insertUser");
	    
	    Connection connection = null;
	    PreparedStatement statement = null;
	    long userId = 0;
	    
	    try {
	        connection = ConnDB.connect();
	        
	        String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
	        
	        statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        statement.setString(1, userName);
	        statement.setString(2, userEmail);
	        
	        statement.executeUpdate();
	        
	        ResultSet generatedKeys = statement.getGeneratedKeys();
	        if (generatedKeys.next()) {
	            userId = generatedKeys.getLong(1);
	        }
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
	    
	    LOGGER.exiting(CLASS_NAME, "insertUser");
	    return userId;
	}
}