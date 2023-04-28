package br.com.pucsp.tcc.authenticator.resources.users;

import org.json.JSONObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import br.com.pucsp.tcc.authenticator.database.SqlQueries;
import br.com.pucsp.tcc.authenticator.utils.exceptions.DatabaseInsertException;

public class SessionTokenManagerDB {
	public void insertSession(Connection connection, int userId, String userSessionToken, boolean isActive)
			throws SQLException, DatabaseInsertException {
		int sessionId = 0;

		try(PreparedStatement statementTimezone = connection.prepareStatement(SqlQueries.TIME_ZONE);
				PreparedStatement statement = connection.prepareStatement(SqlQueries.INSERT_SESSION,
						Statement.RETURN_GENERATED_KEYS);) {

			statementTimezone.executeUpdate();

			statement.setInt(1, userId);
			statement.setString(2, userSessionToken);
			statement.setBoolean(3, isActive);

			statement.executeUpdate();
			
			ResultSet rs = statement.getGeneratedKeys();
			if(rs.next()) {
				sessionId = rs.getInt(1);
			}

			rs.close();
		}
		
		if(sessionId == 0) {
			throw new DatabaseInsertException("Session ID could not be generated");
		}
	}
	
	public JSONObject getSession(Connection connection, int userId, String userSessionToken)
			throws SQLException, DatabaseInsertException {
		JSONObject session = new JSONObject();
		
		try(PreparedStatement statementTimezone = connection.prepareStatement(SqlQueries.TIME_ZONE);
				PreparedStatement statement = connection.prepareStatement(SqlQueries.GET_SESSION,
						Statement.RETURN_GENERATED_KEYS);) {

			statementTimezone.executeUpdate();

			statement.setInt(1, userId);
			statement.setString(2, userSessionToken);

			ResultSet rs = statement.executeQuery();
			
			if(rs.next()) {
				int sessionId = rs.getInt("session_id");
				String sessionToken = rs.getString("session");
				boolean isSessionTokenActive = rs.getBoolean("is_active");
				
				session.put("sessionId", sessionId);
				session.put("sessionToken", sessionToken);
				session.put("isSessionTokenActive", isSessionTokenActive);
			}

			rs.close();
		}
		
		return session;
	}
}