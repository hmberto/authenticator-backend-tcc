package br.com.pucsp.tcc.authenticator.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.ConnDB;

public class OTPManagerDB implements AutoCloseable {
	private static final Logger LOGGER = LoggerFactory.getLogger(OTPManagerDB.class);
	
	private Connection connection;
	
	public OTPManagerDB() throws SQLException {
		this.connection = ConnDB.getConnection();
	}
	
	public boolean insert(String sql, String userEmail, String userOTP) {
		PreparedStatement statement = null;
	    int rowsUpdated = 0;
	    
	    try {
	    	connection = ConnDB.getConnection();
	        
	        statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        statement.setString(1, userOTP);
	        statement.setString(2, userEmail);
	        
	        rowsUpdated = statement.executeUpdate();
	    }
	    catch(SQLException e) {
	    	LOGGER.error("Error updating OTP or Session Token - Email: " + userEmail, e);
	    }
	    finally {
	    	if(statement != null) {
		    	try {
		        	statement.close();
		        }
		        catch(SQLException e) {
		        	LOGGER.error("Error closing statement", e);
		        }
		    }
		    if(connection != null) {
		    	try {
		        	ConnDB.closeConnection(connection);
		        }
		        catch(SQLException e) {
		        	LOGGER.error("Error closing connection", e);
		        }
		    }
	    }
	    
	    if(rowsUpdated > 0) {
	    	return true;
	    }
	    else {
	    	return false;
	    }
	}
	
	@Override
	public void close() throws Exception {
		if(connection != null) {
			ConnDB.closeConnection(connection);
		}
	}
}