package br.com.pucsp.tcc.authenticator.resources.tokens;

import java.sql.Connection;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.ConnDB;
import br.com.pucsp.tcc.authenticator.resources.users.SessionTokenManagerDB;
import br.com.pucsp.tcc.authenticator.resources.users.FindUserDB;
import br.com.pucsp.tcc.authenticator.utils.DataValidator;
import br.com.pucsp.tcc.authenticator.utils.exceptions.UnregisteredUserException;

public class SessionTokenValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionTokenValidator.class);

	public JSONObject verify(final JSONObject body) throws Exception {
		String userEmail = body.has("email") ? body.getString("email").trim().toLowerCase() : null;
		String userSessionToken = body.has("sessionToken") ? body.getString("sessionToken").trim().toUpperCase() : null;
		JSONObject resp = new JSONObject();

		validateBody(userEmail, userSessionToken);

		try (ConnDB connDB = ConnDB.getInstance(); Connection connection = connDB.getConnection();) {

			FindUserDB getUserFromDB = new FindUserDB();
			JSONObject user = getUserFromDB.verify(connection, userEmail);

			if (user == null || user.getInt("userId") == 0) {
				throw new UnregisteredUserException("Unable to validate Session Token to unregistered user");
			}

			LOGGER.info("User '{}' found in database", user.getInt("userId"));

			SessionTokenManagerDB sessionTokenManager = new SessionTokenManagerDB();
			JSONObject session = sessionTokenManager.getSession(connection, user.getInt("userId"), userSessionToken);

			if (session.length() == 0) {
				resp.put("Status", "Invalid session token");
				return resp;
			} else if (session.getBoolean("isSessionTokenActive")) {
				resp.put("Status", "Valid session");
				return resp;
			} else {
				resp.put("Status", "Unconfirmed session");
				return resp;
			}
		}
	}

	private static void validateBody(String userEmail, String userSessionToken) throws Exception {
		DataValidator.isValidEmail(userEmail);

		DataValidator.isValidToken(userSessionToken, "session");
	}
}