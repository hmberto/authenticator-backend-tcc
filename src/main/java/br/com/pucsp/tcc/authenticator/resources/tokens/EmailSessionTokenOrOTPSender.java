package br.com.pucsp.tcc.authenticator.resources.tokens;

import java.sql.Connection;

import javax.mail.MessagingException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.SqlQueries;
import br.com.pucsp.tcc.authenticator.resources.mail.EmailType;
import br.com.pucsp.tcc.authenticator.resources.users.EmailTokenManagerDB;
import br.com.pucsp.tcc.authenticator.resources.users.FindUserDB;
import br.com.pucsp.tcc.authenticator.resources.users.OTPManagerDB;
import br.com.pucsp.tcc.authenticator.resources.users.SaveUserDB;
import br.com.pucsp.tcc.authenticator.resources.users.SessionTokenManagerDB;
import br.com.pucsp.tcc.authenticator.utils.CreateToken;
import br.com.pucsp.tcc.authenticator.utils.DataValidator;
import br.com.pucsp.tcc.authenticator.utils.RespJSON;
import br.com.pucsp.tcc.authenticator.utils.exceptions.BusinessException;
import br.com.pucsp.tcc.authenticator.utils.exceptions.UnregisteredUserException;
import br.com.pucsp.tcc.authenticator.database.ConnDB;

public class EmailSessionTokenOrOTPSender {
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailSessionTokenOrOTPSender.class);

	public String send(final JSONObject body, final String userIp, final String userBrowser, final String userOS)
			throws Exception {
		String userEmail = body.has("email") ? body.getString("email").trim().toLowerCase() : null;
		boolean isSelectedLink = body.has("link") ? body.getBoolean("link") : false;
		boolean isSelectedOTP = body.has("otp") ? body.getBoolean("otp") : true;

		validateBody(userEmail, isSelectedLink, isSelectedOTP);

		try (ConnDB connDB = ConnDB.getInstance(); Connection connection = connDB.getConnection();) {

			FindUserDB getUserFromDB = new FindUserDB();
			JSONObject userJSON = getUserFromDB.verify(connection, userEmail);

			String userSession = CreateToken.generate("session");
			String emailToken = CreateToken.generate("token");
			String userOTP = CreateToken.generate("otp");

			if (userJSON == null) {
				LOGGER.info("Unregistered email '{}'", userEmail);

				SaveUserDB saveUserDB = new SaveUserDB();
				int userId = saveUserDB.insert(connection, "null", "null", userEmail, userSession, userOTP);

				if (userId == 0) {
					throw new UnregisteredUserException(
							"Unable to send OTP to an unregistered user and unable to register the user");
				}

				LOGGER.info("User '{}' created in database", userEmail);

				EmailType.sendEmailOTP(userEmail, userOTP, userIp);

				return RespJSON.createResp(userId, false, "null", false);
			}

			int userId = userJSON.getInt("userId");
			boolean isLogin = userJSON.getBoolean("isLogin");
			boolean sessionTokenActive = false;

			if (isSelectedLink && !isLogin) {
				throw new BusinessException("Link request denied for email '" + userEmail
						+ "' because the user did not complete the registration");
			} else if (!body.has("link") && !body.has("otp") && isLogin) {
				return RespJSON.createResp(userId, isLogin, "null", false);
			} else if (isSelectedLink && isLogin) {
				SessionTokenManagerDB saveSessionToken = new SessionTokenManagerDB();
				saveSessionToken.insertSession(connection, userId, userSession, sessionTokenActive);
				LOGGER.info("Session token created for user '{}'", userEmail);

				EmailTokenManagerDB saveEmailToken = new EmailTokenManagerDB();
				saveEmailToken.insertToken(connection, userId, userEmail, emailToken, userIp, userBrowser, userOS);
				LOGGER.info("Email token created for user '{}'", userEmail);

				sendToken(userEmail, "", userSession, emailToken, userIp, "session");

				return RespJSON.createResp(userId, isLogin, userSession, sessionTokenActive);
			} else if (isSelectedOTP) {
				OTPManagerDB saveActiveOTPDB = new OTPManagerDB();
				saveActiveOTPDB.insert(connection, SqlQueries.UPDATE_OTP_TABLE, userEmail, userOTP);
				LOGGER.info("OTP created for user '{}'", userEmail);

				sendToken(userEmail, userOTP, "", "", userIp, "otp");

				return RespJSON.createResp(userId, isLogin, "null", sessionTokenActive);
			}
		}

		throw new Exception("Unknown error occurred while sending email or user registration");
	}

	private static void sendToken(String userEmail, String userOTP, String userSessionToken, String userEmailToken,
			String userIP, String tokenType) throws MessagingException {
		switch (tokenType) {
		case "session":
			EmailType.sendEmailLink(userEmail, userSessionToken, userEmailToken);
			break;
		case "otp":
			EmailType.sendEmailOTP(userEmail, userOTP, userIP);
			break;
		default:
			throw new IllegalArgumentException("Invalid token type: " + tokenType + " - token type must be session or otp");
		}
	}

	private static void validateBody(String userEmail, boolean isSelectedLink, boolean isSelectedOTP) throws Exception {
		DataValidator.isValidEmail(userEmail);
		
		if (isSelectedLink && isSelectedOTP) {
			throw new BusinessException("Request denied for email '" + userEmail
					+ "' because it is not possible to request LINK and OTP at the same time");
		}
	}
}