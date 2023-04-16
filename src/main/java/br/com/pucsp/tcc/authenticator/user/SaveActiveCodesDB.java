package br.com.pucsp.tcc.authenticator.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.ConnDB;

public class SaveActiveCodesDB implements AutoCloseable {
	private static final Logger LOGGER = LoggerFactory.getLogger(SaveActiveCodesDB.class);
	
	public boolean updateCode(String sql, String email, String code) {
		Connection connection = null;
	    PreparedStatement statement = null;
	    int rowsUpdated = 0;
	    
	    try {
	        connection = ConnDB.getConnection();
	        
	        statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        statement.setString(1, code);
	        statement.setString(2, email);
	        
	        rowsUpdated = statement.executeUpdate();
	    }
	    catch (SQLException e) {
	    	LOGGER.error("Error inserting 6 digit code into database - Email: " + email, e);
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

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}
}