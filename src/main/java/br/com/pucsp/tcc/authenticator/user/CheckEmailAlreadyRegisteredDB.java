package br.com.pucsp.tcc.authenticator.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.ConnDB;
import br.com.pucsp.tcc.authenticator.rest.RegisterEmail;

public class CheckEmailAlreadyRegisteredDB {
	private static final Logger LOGGER = LoggerFactory.getLogger(RegisterEmail.class);
	
	public String verify(String email) throws SQLException, ClassNotFoundException {
		JSONObject json = new JSONObject();
		
		Connection connection = null;
	    PreparedStatement statement = null;
	    ResultSet rs = null;
	    
		try {
	        connection = ConnDB.getConnection();
	        
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
	    	LOGGER.error("Error inserting user", e);
	        throw new SQLException(e);
	    }
		finally {
		    if (rs != null) {
		        try {
		            rs.close();
		        } catch (SQLException e) {
		        	LOGGER.error("Error closing result set", e);
		        }
		    }
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
	    
	    if(json.toString().length() < 3) {
	    	return null;
	    }
	    return json.toString();
	}
}