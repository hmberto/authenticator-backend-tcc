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

public class CheckEmailAlreadyRegisteredDB implements AutoCloseable {
	private static final Logger LOGGER = LoggerFactory.getLogger(CheckEmailAlreadyRegisteredDB.class);
	
	public String verify(String userEmail) throws SQLException {
		JSONObject json = new JSONObject();
		
		Connection connection = null;
	    PreparedStatement statement = null;
	    ResultSet rs = null;
	    
		try {
	        connection = ConnDB.getConnection();
	        
	        String sql = "SELECT sessions.user_id, sessions.session, sessions.is_active, users.first_name\n"
	        		+ "FROM sessions\n"
	        		+ "INNER JOIN users ON users.user_id = sessions.user_id\n"
	        		+ "WHERE users.email = ?";
	        
	        statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        statement.setString(1, userEmail);
	        
	        rs = statement.executeQuery();
	        
	        while (rs.next()) {
	            json.put("userId", rs.getInt("id_user"));
	            json.put("session", rs.getString("token"));
	            
	            if(rs.getBoolean("active")) {
	            	json.put("isSessionTokenActive", "true");
	            }
	            else {
	            	json.put("isSessionTokenActive", "false");
	            }
	            
	            if(!"null".equals(rs.getString("name").trim().toLowerCase())) {
	            	json.put("isLogin", "true");
	            }
	            else {
	            	json.put("isLogin", "false");
	            }
	        }
	    }
	    catch (SQLException e) {
	    	LOGGER.error("Error inserting user", e);
	        throw new SQLException(e);
	    }
		finally {
		    if(rs != null) {
		        try {
		            rs.close();
		        } catch (SQLException e) {
		        	LOGGER.error("Error closing result set", e);
		        }
		    }
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
		
	    if(json.toString().length() < 3) {
	    	return null;
	    }
	    return json.toString();
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}
}