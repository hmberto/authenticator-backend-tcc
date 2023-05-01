package br.com.pucsp.tcc.authenticator.resources.users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.SqlQueries;
import br.com.pucsp.tcc.authenticator.utils.DateTime;
import br.com.pucsp.tcc.authenticator.utils.exceptions.BusinessException;
import br.com.pucsp.tcc.authenticator.utils.exceptions.DatabaseInsertException;

public class EmailTokenManagerDB {
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailTokenManagerDB.class);

	public void insertToken(Connection connection, int userId, String userEmail, String emailToken, String requestIP,
			String requestBrowser, String requestOS) throws SQLException, DatabaseInsertException {
		int tokenId = 0;

		try (PreparedStatement statementTimezone = connection.prepareStatement(SqlQueries.TIME_ZONE);
				PreparedStatement statementDelete = connection.prepareStatement(SqlQueries.DELETE_EMAIL_TOKENS);
				PreparedStatement statementInsert = connection.prepareStatement(SqlQueries.INSERT_EMAIL_TOKEN,
						Statement.RETURN_GENERATED_KEYS);) {

			statementTimezone.executeUpdate();

			statementDelete.setInt(1, userId);
			int rowsDeleted = statementDelete.executeUpdate();
			LOGGER.info("'{}' access confirmations deleted from the database for user '{}'", rowsDeleted, userEmail);

			statementInsert.setInt(1, userId);
			statementInsert.setString(2, emailToken);
			statementInsert.setString(3, requestIP);
			statementInsert.setString(4, requestBrowser);
			statementInsert.setString(5, requestOS);
			statementInsert.setInt(6, userId);
			statementInsert.executeUpdate();

			ResultSet rs = statementInsert.getGeneratedKeys();
			if (rs.next()) {
				tokenId = rs.getInt(1);
			}

			rs.close();
		}

		if (tokenId == 0) {
			throw new DatabaseInsertException("Email Token ID could not be generated");
		}
	}

	public void updateToken(Connection connection, int userId, String userEmail, String userSessionToken,
			String userEmailToken) throws SQLException, DatabaseInsertException {
		int rowsUpdated = 0;

		try (PreparedStatement statementTimezone = connection.prepareStatement(SqlQueries.TIME_ZONE);
				PreparedStatement statementDelete = connection.prepareStatement(SqlQueries.DELETE_EMAIL_TOKENS);
				PreparedStatement statementUpdate = connection.prepareStatement(SqlQueries.UPDATE_APPROVE_LOGIN,
						Statement.RETURN_GENERATED_KEYS);) {

			statementTimezone.executeUpdate();

			statementDelete.setInt(1, userId);
			int rowsDeleted = statementDelete.executeUpdate();
			LOGGER.info("'{}' access confirmations deleted from the database for user '{}'", rowsDeleted, userEmail);

			statementUpdate.setString(1, userSessionToken);
			statementUpdate.setString(2, userEmail);
			statementUpdate.setString(3, userEmailToken);

			rowsUpdated = statementUpdate.executeUpdate();
		}

		if (rowsUpdated == 0) {
			throw new DatabaseInsertException("Email Token could not be updated");
		}
	}

	public JSONObject getToken(Connection connection, String userEmailToken) throws SQLException, BusinessException {
		try (PreparedStatement statementGet = connection.prepareStatement(SqlQueries.GET_EMAIL_TOKEN,
				Statement.RETURN_GENERATED_KEYS);) {
			JSONObject tokenInfo = new JSONObject();

			statementGet.setString(1, userEmailToken);

			ResultSet rs = statementGet.executeQuery();

			int userId = 0;
			if (rs.next()) {
				userId = rs.getInt("user_id");
				String date = rs.getString("created_at");
				String createdAt = DateTime.formatDate(date);

				tokenInfo.put("userId", userId);
				tokenInfo.put("requestIP", rs.getString("request_ip"));
				tokenInfo.put("requestBrowser", rs.getString("request_browser"));
				tokenInfo.put("requestOS", rs.getString("request_operational_system"));
				tokenInfo.put("isApproved", rs.getBoolean("is_approved"));
				tokenInfo.put("createdAt", createdAt);
			}

			if (userId == 0) {
				throw new BusinessException("Email Token does not exist in database");
			}

			return tokenInfo;
		}
	}
}