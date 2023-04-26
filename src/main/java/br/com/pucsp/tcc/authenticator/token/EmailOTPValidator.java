package br.com.pucsp.tcc.authenticator.token;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.SqlQueries;
import br.com.pucsp.tcc.authenticator.exceptions.InvalidEmailException;
import br.com.pucsp.tcc.authenticator.exceptions.InvalidTokenException;
import br.com.pucsp.tcc.authenticator.exceptions.UnregisteredUserException;
import br.com.pucsp.tcc.authenticator.user.OTPManagerDB;
import br.com.pucsp.tcc.authenticator.utils.CreateToken;
import br.com.pucsp.tcc.authenticator.utils.DataValidator;
import br.com.pucsp.tcc.authenticator.utils.RespJSON;
import br.com.pucsp.tcc.authenticator.user.GetUserFromDB;
import br.com.pucsp.tcc.authenticator.user.SaveSessionTokenDB;

public class EmailOTPValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailOTPValidator.class);
	
	private static final int OTP_LENGTH = Integer.parseInt(System.getenv("OTP_LENGTH"));
    
	public String verify(final JSONObject body, final String userIP, final String loginDate) throws Exception {
		String userEmail = body.has("email") ? body.getString("email").trim().toLowerCase() : null;
		String userOTP = body.has("otp") ? body.getString("otp").trim().toUpperCase() : null;
		
		validateBody(userEmail, userOTP);
		
		try(OTPManagerDB OTPManager = new OTPManagerDB();
				SaveSessionTokenDB saveSessionToken = new SaveSessionTokenDB();
				GetUserFromDB getUserFromDB = new GetUserFromDB();) {
			JSONObject user = getUserFromDB.verify(userEmail);
			
			if(user == null || user.getInt("userId") == 0) {
				throw new UnregisteredUserException("Unable to validate OTP or Session Token to unregistered user");
			}
			
			LOGGER.info("User '{}' found in database", user.getInt("userId"));
			
			String resp = createResponse(user, userEmail, saveSessionToken);
			boolean isOTPUpdated = OTPManager.insert(SqlQueries.UPDATE_AUTH_OTP, userEmail, userOTP);
			
			if(isOTPUpdated) {
				return resp;
			}
		}
		
		return null;
	}
	
	private static String createResponse(JSONObject user, String userEmail, SaveSessionTokenDB saveSessionToken) {
		int userId = user.getInt("userId");
		String session = user.getString("session");
		boolean isSessionTokenActive = user.getBoolean("isSessionTokenActive");
		boolean isLogin = user.getBoolean("isLogin");
		
		if(isSessionTokenActive) {
			return RespJSON.createResp(userId, isLogin, session, isSessionTokenActive);
		}
		else {
			String userSession = CreateToken.generate("session");
			boolean isSessionTokenSaved = saveSessionToken.insert(userId, userEmail, userSession, true);
			if(isSessionTokenSaved) {
				return RespJSON.createResp(userId, isLogin, userSession, true);
			}
		}
		
		return null;
	}
	
	private static void validateBody(String userEmail, String userOTP) throws Exception {
		if(userEmail == null) {
			throw new InvalidEmailException("email is required but not sent");
		}
		if(!DataValidator.isValidEmail(userEmail)) {
			throw new InvalidEmailException("Invalid format for email");
        }
		
		if(userOTP == null) {
			throw new InvalidTokenException("otp is required but not sent");
		}
		if(!DataValidator.isValidToken(userOTP) || userOTP.length() != OTP_LENGTH) {
			throw new InvalidTokenException("Invalid otp format");
		}
	}
}