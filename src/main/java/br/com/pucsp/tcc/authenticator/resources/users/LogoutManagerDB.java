package br.com.pucsp.tcc.authenticator.resources.users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.ConnDB;
import br.com.pucsp.tcc.authenticator.database.SqlQueries;
import br.com.pucsp.tcc.authenticator.utils.DataValidator;
import br.com.pucsp.tcc.authenticator.utils.exceptions.BusinessException;

public class LogoutManagerDB {
	private static final Logger LOGGER = LoggerFactory.getLogger(LogoutManagerDB.class);

	public void logout(final JSONObject body) throws Exception {
		String userEmail = body.getString("email").trim().toLowerCase();
		String userSessionToken = body.getString("session").trim().toUpperCase();
		boolean isSelectedKillAll = body.getBoolean("killAll");

		int rowsUpdated = 0;

		validateBody(userEmail, userSessionToken);

		String sql = isSelectedKillAll ? SqlQueries.UPDATE_SESSION_LOGOUT_ALL : SqlQueries.UPDATE_SESSION_LOGOUT_ONE;

		try (ConnDB connDB = ConnDB.getInstance();
				Connection connection = connDB.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {

			statement.setString(1, userSessionToken);
			statement.setString(2, userEmail);

			rowsUpdated = statement.executeUpdate();
		}

		if (rowsUpdated == 0) {
			throw new BusinessException("Email Token could not be updated");
		}

		String log = isSelectedKillAll ? "Deleted session tokens: '{}'" : "Session tokens disabled: '{}'";
		LOGGER.info(log, rowsUpdated);
	}

	private static void validateBody(String userEmail, String userSessionToken) throws Exception {
		DataValidator.isValidEmail(userEmail);

		DataValidator.isValidToken(userSessionToken, "session");
	}
}