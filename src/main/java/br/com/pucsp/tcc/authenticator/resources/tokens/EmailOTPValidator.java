package br.com.pucsp.tcc.authenticator.resources.tokens;

import java.sql.Connection;
import java.sql.SQLException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.ConnDB;
import br.com.pucsp.tcc.authenticator.database.SqlQueries;
import br.com.pucsp.tcc.authenticator.resources.users.FindUserDB;
import br.com.pucsp.tcc.authenticator.resources.users.OTPManagerDB;
import br.com.pucsp.tcc.authenticator.resources.users.SessionTokenManagerDB;
import br.com.pucsp.tcc.authenticator.utils.CreateToken;
import br.com.pucsp.tcc.authenticator.utils.DataValidator;
import br.com.pucsp.tcc.authenticator.utils.RespJSON;
import br.com.pucsp.tcc.authenticator.utils.exceptions.DatabaseInsertException;
import br.com.pucsp.tcc.authenticator.utils.exceptions.InvalidEmailException;
import br.com.pucsp.tcc.authenticator.utils.exceptions.InvalidTokenException;
import br.com.pucsp.tcc.authenticator.utils.exceptions.UnregisteredUserException;
import br.com.pucsp.tcc.authenticator.utils.system.SystemDefaultVariables;

public class EmailOTPValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailOTPValidator.class);

	private static final int OTP_LENGTH = SystemDefaultVariables.otpLength;

	public String verify(final JSONObject body) throws Exception {
		String userEmail = body.has("email") ? body.getString("email").trim().toLowerCase() : null;
		String userOTP = body.has("otp") ? body.getString("otp").trim().toUpperCase() : null;

		validateBody(userEmail, userOTP);

		try (ConnDB connDB = ConnDB.getInstance(); Connection connection = connDB.getConnection();) {

			FindUserDB getUserFromDB = new FindUserDB();
			JSONObject user = getUserFromDB.verify(connection, userEmail);

			if (user == null || user.getInt("userId") == 0) {
				throw new UnregisteredUserException("Unable to validate OTP to unregistered user");
			}

			LOGGER.info("User '{}' found in database", user.getInt("userId"));

			OTPManagerDB OTPManager = new OTPManagerDB();
			OTPManager.insert(connection, SqlQueries.UPDATE_AUTH_OTP, userEmail, userOTP);

			SessionTokenManagerDB saveSessionToken = new SessionTokenManagerDB();
			return createResponse(connection, user, userEmail, saveSessionToken);
		}
	}

	private static String createResponse(Connection connection, JSONObject user, String userEmail,
			SessionTokenManagerDB saveSessionToken) throws DatabaseInsertException, SQLException {
		int userId = user.getInt("userId");
		String session = user.getString("session");
		boolean isSessionTokenActive = user.getBoolean("isSessionTokenActive");
		boolean isLogin = user.getBoolean("isLogin");

		if (isSessionTokenActive) {
			return RespJSON.createResp(userId, isLogin, session, isSessionTokenActive);
		} else {
			String userSession = CreateToken.generate("session");

			saveSessionToken.insertSession(connection, userId, userSession, true);

			return RespJSON.createResp(userId, isLogin, userSession, true);
		}
	}

	private static void validateBody(String userEmail, String userOTP) throws Exception {
		if (userEmail == null) {
			throw new InvalidEmailException("email is required but not sent");
		}
		if (!DataValidator.isValidEmail(userEmail)) {
			throw new InvalidEmailException("Invalid format for email");
		}

		if (userOTP == null) {
			throw new InvalidTokenException("otp is required but not sent");
		}
		if (!DataValidator.isValidToken(userOTP) || userOTP.length() != OTP_LENGTH) {
			throw new InvalidTokenException("Invalid otp format");
		}
	}
}