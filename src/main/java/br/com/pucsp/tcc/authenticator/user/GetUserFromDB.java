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
import br.com.pucsp.tcc.authenticator.database.SqlQueries;

public class GetUserFromDB implements AutoCloseable {
	private static final Logger LOGGER = LoggerFactory.getLogger(GetUserFromDB.class);
	
	public JSONObject verify(String userEmail) throws SQLException {
		JSONObject json = new JSONObject();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		try {
			connection = ConnDB.getConnection();
			
			statement = connection.prepareStatement(SqlQueries.CHECK_EMAIL_ALREADY_REGISTERED, Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, userEmail);
			
			rs = statement.executeQuery();
			
			while(rs.next()) {
				json.put("userId", rs.getInt("user_id"));
				json.put("session", rs.getString("session"));
				json.put("isSessionTokenActive", rs.getBoolean("is_active"));
				
				String firstName = rs.getString("first_name").trim().toLowerCase();
				if("null".equals(firstName) || firstName == null) {
					json.put("isLogin", false);
				}
				else {
					json.put("isLogin", true);
				}
			}
		}
		catch(SQLException e) {
			throw new SQLException("Error checking if user '" + userEmail + "' already exists in database", e);
		}
		finally {
			if(rs != null) {
				try {
					rs.close();
				}
				catch (SQLException e) {
					LOGGER.error("Error closing result set", e);
				}
			}
			if(statement != null) {
				try {
					statement.close();
				}
				catch (SQLException e) {
					LOGGER.error("Error closing statement", e);
				}
			}
			if(connection != null) {
				try {
					ConnDB.closeConnection(connection);
				}
				catch (SQLException e) {
					LOGGER.error("Error closing connection", e);
				}
			}
		}
		
		if(json.toString().length() < 3) {
			return null;
		}
		return json;
	}
	
	@Override
	public void close() throws Exception {}
}