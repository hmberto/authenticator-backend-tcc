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
	
	public boolean logout(String userEmail, String userToken, boolean isSelectedKillAll) throws SQLException {
		Connection connection = null;
	    PreparedStatement statement = null;
	    int rowsUpdated = 0;
	    
	    try {
	        connection = ConnDB.getConnection();
	        
	        String sql = null;
	        
	        sql = "DELETE a\n"
	        		+ "FROM active_sessions a\n"
	        		+ "JOIN (\n"
	        		+ "    SELECT token, id_user\n"
	        		+ "    FROM active_sessions\n"
	        		+ "    WHERE active = true AND token = ?\n"
	        		+ ") b ON a.token != b.token AND a.id_user = b.id_user\n"
	        		+ "WHERE a.id_user = (SELECT id_user FROM users WHERE email = ?);";
	        
	        if(isSelectedKillAll) {
	        	sql = "DELETE a\n"
	        		    + "FROM active_sessions a\n"
	        		    + "JOIN (\n"
	        		    + "    SELECT token, id_user\n"
	        		    + "    FROM active_sessions\n"
	        		    + "    WHERE active = true AND token = ?\n"
	        		    + ") b ON a.id_user = b.id_user\n"
	        		    + "WHERE a.id_user = (SELECT id_user FROM users WHERE email = ?);";
	        }
	        
	        statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        statement.setString(1, userToken);
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