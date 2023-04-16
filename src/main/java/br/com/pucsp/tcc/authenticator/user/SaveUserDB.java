package br.com.pucsp.tcc.authenticator.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.ConnDB;
import br.com.pucsp.tcc.authenticator.rest.RegisterEmail;

public class SaveUserDB {
	private static final Logger LOGGER = LoggerFactory.getLogger(RegisterEmail.class);
	
	public long insertUser(String userName, String userEmail) throws ClassNotFoundException, SQLException {
	    Connection connection = null;
	    PreparedStatement statement = null;
	    long userId = 0;
	    
	    try {
	        connection = ConnDB.getConnection();
	        
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
	    
	    return userId;
	}
}