package br.com.pucsp.tcc.authenticator.resources.tokens;

import java.sql.Connection;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.ConnDB;
import br.com.pucsp.tcc.authenticator.resources.users.SessionTokenManagerDB;
import br.com.pucsp.tcc.authenticator.resources.users.GetUserFromDB;
import br.com.pucsp.tcc.authenticator.utils.DataValidator;
import br.com.pucsp.tcc.authenticator.utils.exceptions.InvalidEmailException;
import br.com.pucsp.tcc.authenticator.utils.exceptions.InvalidTokenException;
import br.com.pucsp.tcc.authenticator.utils.exceptions.UnregisteredUserException;

public class SessionTokenValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionTokenValidator.class);
	
	private static final int SESSION_LENGTH = Integer.parseInt(System.getenv("SESSION_LENGTH"));
    
	public JSONObject verify(final JSONObject body) throws Exception {
		String userEmail = body.has("email") ? body.getString("email").trim().toLowerCase() : null;
		String userSessionToken = body.has("sessionToken") ? body.getString("sessionToken").trim().toUpperCase() : null;
		JSONObject resp = new JSONObject();
		
		validateBody(userEmail, userSessionToken);
		
		try(ConnDB connDB = ConnDB.getInstance();
				Connection connection = connDB.getConnection();) {
			
			GetUserFromDB getUserFromDB = new GetUserFromDB();
			JSONObject user = getUserFromDB.verify(connection, userEmail);
			
			if(user == null || user.getInt("userId") == 0) {
				throw new UnregisteredUserException("Unable to validate Session Token to unregistered user");
			}
			
			LOGGER.info("User '{}' found in database", user.getInt("userId"));
			
			SessionTokenManagerDB sessionTokenManager = new SessionTokenManagerDB();
			JSONObject session = sessionTokenManager.getSession(connection, user.getInt("userId"), userSessionToken);
			
			if(session.length() == 0) {
				resp.put("Message", "Invalid session token");
				return resp;
			}
			else if(session.getBoolean("isSessionTokenActive")) {
				resp.put("Message", "Valid session");
				return resp;
			}
			else {
				resp.put("Message", "Unconfirmed session");
				return resp;
			}
		}
	}
	
	private static void validateBody(String userEmail, String userSessionToken) throws Exception {
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
	}
}