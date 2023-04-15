package br.com.pucsp.tcc.authenticator.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

import br.com.pucsp.tcc.authenticator.database.ConnDB;

public class CheckEmailAlreadyRegisteredDB {
	private static final String CLASS_NAME = SaveActiveCodesDB.class.getSimpleName();
	private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);
	
	public String verify(String email) throws SQLException, ClassNotFoundException {
		LOGGER.entering(CLASS_NAME, "verify");
		
		JSONObject json = new JSONObject();
		
		Connection connection = null;
	    PreparedStatement statement = null;
	    ResultSet rs = null;
	    
		try {
	        connection = ConnDB.connect();
	        
	        String sql = "SELECT active_sessions.id_user, active_sessions.token " +
		             "FROM active_sessions " +
		             "INNER JOIN users ON users.id_user = active_sessions.id_user " +
		             "WHERE users.email = ?";
	        
	        statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        statement.setString(1, email);
	        
	        rs = statement.executeQuery();
	        
	        while (rs.next()) {
	        	json.put("id_user", rs.getInt("id_user"));
	            json.put("token", rs.getString("token"));
	        }
	    }
	    catch (SQLException e) {
	        LOGGER.log(Level.SEVERE, "Error inserting user", e);
	        throw new SQLException(e);
	    }
	    finally {
	        if (statement != null) {
	            try {
	            	rs.close();
	                statement.close();
	                connection.close();
	            } catch (SQLException e) {
	                LOGGER.log(Level.SEVERE, "Error closing statement", e);
	            }
	        }
	        if (connection != null) {
	            ConnDB.disconnect();
	        }
	    }
	    
	    LOGGER.exiting(CLASS_NAME, "insertActiveCode");
	    if(json.toString().length() < 3) {
	    	return null;
	    }
	    return json.toString();
	}
}