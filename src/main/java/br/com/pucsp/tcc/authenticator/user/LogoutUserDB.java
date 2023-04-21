package br.com.pucsp.tcc.authenticator.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.ConnDB;
import br.com.pucsp.tcc.authenticator.rest.RegisterEmail;

public class LogoutUserDB {
	private static final Logger LOGGER = LoggerFactory.getLogger(RegisterEmail.class);
	
	public boolean logout(String userEmail, String userSessionToken, boolean isSelectedKillAll) throws SQLException {
		Connection connection = null;
	    PreparedStatement statement = null;
	    int rowsUpdated = 0;
	    
	    try {
	        connection = ConnDB.getConnection();
	        
	        String sql = null;
	        
	        sql = "UPDATE sessions \n"
	        		+ "SET is_active = false \n"
	        		+ "WHERE session = ? \n"
	        		+ "AND is_active = true \n"
	        		+ "AND user_id = (SELECT user_id FROM users WHERE email = ?);";
	        
	        if(isSelectedKillAll) {
	        	sql = "DELETE a\n"
		        		+ "FROM sessions a\n"
		        		+ "JOIN (\n"
		        		+ "    SELECT session, user_id\n"
		        		+ "    FROM sessions\n"
		        		+ "    WHERE is_active = true AND session = ?\n"
		        		+ ") b ON a.session != b.session AND a.user_id = b.user_id\n"
		        		+ "WHERE a.user_id = (SELECT user_id FROM users WHERE email = ?);";
	        	
//	        	sql = "DELETE a\n"
//	        		    + "FROM sessions a\n"
//	        		    + "JOIN (\n"
//	        		    + "    SELECT session, user_id\n"
//	        		    + "    FROM sessions\n"
//	        		    + "    WHERE is_active = true AND session = ?\n"
//	        		    + ") b ON a.user_id = b.user_id\n"
//	        		    + "WHERE a.user_id = (SELECT user_id FROM users WHERE email = ?);";
	        }
	        
	        statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        statement.setString(1, userSessionToken);
	        statement.setString(2, userEmail);
	        
	        rowsUpdated = statement.executeUpdate();
	    }
	    catch (SQLException e) {
	    	LOGGER.error("Error logging out user", e);
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