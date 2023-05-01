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
import br.com.pucsp.tcc.authenticator.utils.exceptions.DatabaseInsertException;
import br.com.pucsp.tcc.authenticator.utils.exceptions.InvalidEmailException;
import br.com.pucsp.tcc.authenticator.utils.exceptions.InvalidTokenException;
import br.com.pucsp.tcc.authenticator.utils.system.SystemDefaultVariables;

public class LogoutManagerDB {
	private static final Logger LOGGER = LoggerFactory.getLogger(LogoutManagerDB.class);

	private static final int SESSION_LENGTH = SystemDefaultVariables.sessionLength;

	public boolean logout(final JSONObject body) throws Exception {
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
			throw new DatabaseInsertException("Email Token could not be updated");
		}

		String log = isSelectedKillAll ? "Deleted session tokens: " : "Session tokens disabled: ";
		LOGGER.info(log + rowsUpdated);
		return true;
	}

	private static void validateBody(String userEmail, String userSessionToken) throws Exception {
		if (!DataValidator.isValidEmail(userEmail)) {
			throw new InvalidEmailException("Invalid email format");
		}
		if (!DataValidator.isValidToken(userSessionToken) || userSessionToken.length() != SESSION_LENGTH) {
			throw new InvalidTokenException("Invalid Session Token format");
		}
	}
}