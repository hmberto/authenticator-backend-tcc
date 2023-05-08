package br.com.pucsp.tcc.authenticator.resources.tokens;

import java.sql.Connection;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.ConnDB;
import br.com.pucsp.tcc.authenticator.resources.users.EmailTokenManagerDB;
import br.com.pucsp.tcc.authenticator.resources.users.FindUserDB;
import br.com.pucsp.tcc.authenticator.utils.DataValidator;
import br.com.pucsp.tcc.authenticator.utils.exceptions.UnregisteredUserException;

public class EmailTokenValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailTokenValidator.class);

	public void verify(final JSONObject body) throws Exception {
		String userEmail = body.has("email") ? body.getString("email").trim().toLowerCase() : null;
		String userSessionToken = body.has("sessionToken") ? body.getString("sessionToken").trim().toUpperCase() : null;
		String userEmailToken = body.has("emailToken") ? body.getString("emailToken").trim().toUpperCase() : null;
		boolean isSelectedApprove = body.has("approve") ? body.getBoolean("approve") : false;

		validateBody(userEmail, userSessionToken, userEmailToken, isSelectedApprove);

		if (!isSelectedApprove) {
			return;
		}

		try (ConnDB connDB = ConnDB.getInstance(); Connection connection = connDB.getConnection();) {

			FindUserDB getUserFromDB = new FindUserDB();
			JSONObject user = getUserFromDB.verify(connection, userEmail);

			if (user == null || user.getInt("userId") == 0) {
				throw new UnregisteredUserException("Unable to validate Email Token to unregistered user");
			}

			LOGGER.info("User '{}' found in database", user.getInt("userId"));

			EmailTokenManagerDB emailTokenManagerDB = new EmailTokenManagerDB();
			emailTokenManagerDB.updateToken(connection, user.getInt("userId"), userEmail, userSessionToken,
					userEmailToken);
		}
	}

	private static void validateBody(String userEmail, String userSessionToken, String userEmailToken,
			boolean isSelectedApprove) throws Exception {
		DataValidator.isValidEmail(userEmail);

		DataValidator.isValidToken(userSessionToken, "session");

		DataValidator.isValidToken(userEmailToken, "token");
	}
}