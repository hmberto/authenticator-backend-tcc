package br.com.pucsp.tcc.authenticator.resources.users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONObject;
import br.com.pucsp.tcc.authenticator.database.SqlQueries;

public class GetUserFromDB {
	public JSONObject verify(Connection connection, String userEmail) throws SQLException {
		JSONObject json = new JSONObject();
		int userId = 0;
		
		try(PreparedStatement statement = connection.prepareStatement(SqlQueries.CHECK_EMAIL_ALREADY_REGISTERED, Statement.RETURN_GENERATED_KEYS);) {
			statement.setString(1, userEmail);
			ResultSet rs = statement.executeQuery();
			
			if(rs.next()) {
				userId = rs.getInt("user_id");
				json.put("userId", userId);
				json.put("session", rs.getString("session"));
				json.put("isSessionTokenActive", rs.getBoolean("is_active"));
				
				String firstName = rs.getString("first_name");
				json.put("isLogin", firstName != null && !firstName.trim().equalsIgnoreCase("null"));
			}
			
			rs.close();
		}
		
		return userId >= 1 ? json : null;
	}
}