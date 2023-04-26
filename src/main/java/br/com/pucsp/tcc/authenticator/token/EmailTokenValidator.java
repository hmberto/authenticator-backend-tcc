package br.com.pucsp.tcc.authenticator.token;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.exceptions.InvalidEmailException;
import br.com.pucsp.tcc.authenticator.exceptions.InvalidTokenException;
import br.com.pucsp.tcc.authenticator.exceptions.UnregisteredUserException;
import br.com.pucsp.tcc.authenticator.user.EmailTokenManagerDB;
import br.com.pucsp.tcc.authenticator.utils.DataValidator;
import br.com.pucsp.tcc.authenticator.user.GetUserFromDB;
import br.com.pucsp.tcc.authenticator.user.SaveSessionTokenDB;

public class EmailTokenValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailTokenValidator.class);
	
	private static final int SESSION_LENGTH = Integer.parseInt(System.getenv("SESSION_LENGTH"));
    private static final int EMAIL_TOKEN_LENGTH = Integer.parseInt(System.getenv("EMAIL_TOKEN_LENGTH"));
    
	public boolean verify(final JSONObject body, final String userIP, final String loginDate) throws Exception {
		String userEmail = body.has("email") ? body.getString("email").trim().toLowerCase() : null;
		String userSessionToken = body.has("sessionTokenOrOTP") ? body.getString("sessionTokenOrOTP").trim().toUpperCase() : null;
		String userEmailToken = body.has("emailToken") ? body.getString("emailToken").trim().toUpperCase() : null;
		boolean isSelectedApprove = body.has("approve") ? body.getBoolean("approve") : false;
		
		validateBody(userEmail, userSessionToken, userEmailToken, isSelectedApprove);
		
		if(!isSelectedApprove) {
			return true;
		}
		
		try(EmailTokenManagerDB emailTokenManagerDB = new EmailTokenManagerDB();
				SaveSessionTokenDB saveSessionToken = new SaveSessionTokenDB();
				GetUserFromDB getUserFromDB = new GetUserFromDB();) {
			JSONObject user = getUserFromDB.verify(userEmail);
			
			if(user == null || user.getInt("userId") == 0) {
				throw new UnregisteredUserException("Unable to validate Email Token to unregistered user");
			}
			
			LOGGER.info("User '{}' found in database", user.getInt("userId"));
			
			boolean isEmailTokenUpdated = emailTokenManagerDB.insert(user.getInt("userId"), userEmail, userSessionToken, userEmailToken);
			
			if(isEmailTokenUpdated) {
				return true;
			}
		}
		
		return false;
	}
	
	private static void validateBody(String userEmail, String userSessionToken, String userEmailToken, boolean isSelectedApprove) throws Exception {
		if(userEmail == null) {
			throw new InvalidEmailException("email is required but not sent");
		}
		if(!DataValidator.isValidEmail(userEmail)) {
			throw new InvalidEmailException("Invalid format for email");
        }
		
		if(userSessionToken == null) {
			throw new InvalidTokenException("sessionToken is required but not sent");
		}
		if(!DataValidator.isValidToken(userSessionToken) || userSessionToken.length() != SESSION_LENGTH) {
			throw new InvalidTokenException("Invalid sessionToken format");
		}
		
		if(userEmailToken == null) {
			throw new InvalidTokenException("emailToken is required but not sent");
		}
		if(!DataValidator.isValidToken(userEmailToken) || userEmailToken.length() != EMAIL_TOKEN_LENGTH) {
			throw new InvalidTokenException("Invalid emailToken format");
		}
	}
}