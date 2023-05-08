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

public class NameManagerDB {
	private static final Logger LOGGER = LoggerFactory.getLogger(NameManagerDB.class);

	public void update(JSONObject body) throws Exception {
		String userFirstName = body.getString("firstName").trim();
		String userLastName = body.getString("lastName").trim();
		String userEmail = body.getString("email").trim().toLowerCase();
		String userSessionToken = body.getString("session").trim().toUpperCase();

		int rowsUpdated = 0;

		validateBody(userFirstName, userLastName, userEmail, userSessionToken);

		try (ConnDB connDB = ConnDB.getInstance();
				Connection connection = connDB.getConnection();
				PreparedStatement statement = connection.prepareStatement(SqlQueries.UPDATE_NAME,
						Statement.RETURN_GENERATED_KEYS);) {

			statement.setString(1, userFirstName);
			statement.setString(2, userLastName);
			statement.setString(3, userEmail);
			statement.setString(4, userSessionToken);

			rowsUpdated = statement.executeUpdate();
		}

		if (rowsUpdated == 0) {
			throw new BusinessException("Name could not be updated");
		}

		LOGGER.info("Name updated for user '{}'", userEmail);
	}

	private static void validateBody(String userFirstName, String userLastName, String userEmail,
			String userSessionToken) throws Exception {
		DataValidator.isValidName(userFirstName, "first name");

		DataValidator.isValidName(userLastName, "last name");

		DataValidator.isValidEmail(userEmail);

		DataValidator.isValidToken(userSessionToken, "session");
	}
}