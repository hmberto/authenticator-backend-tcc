package br.com.pucsp.tcc.authenticator.token;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.SqlQueries;
import br.com.pucsp.tcc.authenticator.exceptions.BusinessException;
import br.com.pucsp.tcc.authenticator.exceptions.InvalidEmailException;
import br.com.pucsp.tcc.authenticator.exceptions.UnregisteredUserException;
import br.com.pucsp.tcc.authenticator.mail.EmailType;
import br.com.pucsp.tcc.authenticator.user.GetUserFromDB;
import br.com.pucsp.tcc.authenticator.user.OTPManagerDB;
import br.com.pucsp.tcc.authenticator.user.SaveSessionTokenDB;
import br.com.pucsp.tcc.authenticator.user.SaveEmailTokenDB;
import br.com.pucsp.tcc.authenticator.user.SaveUserDB;
import br.com.pucsp.tcc.authenticator.utils.CreateToken;
import br.com.pucsp.tcc.authenticator.utils.DataValidator;
import br.com.pucsp.tcc.authenticator.utils.RespJSON;

public class EmailSessionTokenOrOTPSender {
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailSessionTokenOrOTPSender.class);
	
	public String send(final JSONObject body, final String userIP, final String userBrowser, final String userOS, final String loginDate) throws Exception {
		try(GetUserFromDB getUserFromDB = new GetUserFromDB();
	    		SaveUserDB saveUserDB = new SaveUserDB();
	    		SaveSessionTokenDB saveSessionToken = new SaveSessionTokenDB();
				SaveEmailTokenDB saveEmailToken = new SaveEmailTokenDB();
	    		OTPManagerDB saveActiveOTPDB = new OTPManagerDB()) {
			
			String userEmail = body.has("email") ? body.getString("email").trim().toLowerCase() : null;
			boolean isSelectedLink = body.has("link") ? body.getBoolean("link") : false;
			boolean isSelectedOTP = body.has("otp") ? body.getBoolean("otp") : true;
			
			validateBody(userEmail, isSelectedLink, isSelectedOTP);
			
			JSONObject userJSON = getUserFromDB.verify(userEmail);
	    	String userSession = CreateToken.generate("session");
	    	
	    	if(userJSON == null) {
	    		LOGGER.info("Unregistered email '{}'", userEmail);
	    		int userId = saveUserDB.insert("null", "null", userEmail, userSession, userIP, loginDate);
	    		
	    		if(userId >= 1) {
	    			String resp = RespJSON.createResp(userId, false, "null", false);
	    			return resp;
	    		}
	    		
	    		throw new UnregisteredUserException("Unable to send OTP to unregistered user - Unable to register user in database");
	    	}
	    	
	    	int userId = userJSON.getInt("userId");
	    	boolean isLogin = userJSON.getBoolean("isLogin");
	    	boolean  sessionTokenActive = false;
	    	
	    	if(isSelectedLink && !isLogin) {
	    		throw new BusinessException("Link request denied for email '" + userEmail + "' because the user did not complete the registration");
	    	}
	    	else if(isSelectedLink && isLogin) {
	    		boolean isSessionTokenSaved = saveSessionToken.insert(userId, userEmail, userSession, sessionTokenActive);
	    		boolean isEmailTokenSaved = saveEmailToken.insert(userId, userEmail, userIP, userBrowser, userOS);
	    		
	    		if(isSessionTokenSaved && isEmailTokenSaved) {
	    			sendToken(userEmail, "", userSession, userIP, loginDate, "session");
	    			
	    			String resp = RespJSON.createResp(userId, isLogin, userSession, sessionTokenActive);
	    			return resp;
	    		}
	    	}
	    	else if(isSelectedOTP) {
	    		String userOTP = CreateToken.generate("otp");
	    		boolean isOTPSaved = saveActiveOTPDB.insert(SqlQueries.UPDATE_OTP_TABLE, userEmail, userOTP);
	    		
	    		if(isOTPSaved) {
	    			sendToken(userEmail, userOTP, "", userIP, loginDate, "otp");
	    			
	    			String resp = RespJSON.createResp(userId, isLogin, "null", sessionTokenActive);
	    			return resp;
	    		}
	    	}
	    	else {
	        	throw new BusinessException("Request denied for email '" + userEmail + "' because LINK or OTP is required");
	        }
	    	
	    	throw new Exception("Unknown error occurred while sending email or user registration");
	    }
	}
	
	private static void sendToken(String userEmail, String userOTP, String userSessionToken, String userIP, String loginDate, String tokenType) throws BusinessException {
		switch(tokenType) {
			case "session":
				EmailType.sendEmailLink(userEmail, userSessionToken, userIP, loginDate);
				break;
			case "otp":
				EmailType.sendEmailOTP(userEmail, userOTP, userIP, loginDate);
				break;
			default:
				throw new IllegalArgumentException("Invalid token type: " + tokenType  + " - token type must be session or otp");
		}
	}
	
	private static void validateBody(String userEmail, boolean isSelectedLink, boolean isSelectedOTP) throws Exception {
		if(userEmail == null) {
			throw new InvalidEmailException("Email is required but not sent");
		}
		if(!DataValidator.isValidEmail(userEmail)) {
			throw new InvalidEmailException("Invalid format for email '" + userEmail + "'");
        }
		if(isSelectedLink && isSelectedOTP) {
    		throw new BusinessException("Request denied for email '" + userEmail + "' because it is not possible to request LINK and OTP at the same time");
    	}
    	if(!isSelectedLink && !isSelectedOTP) {
    		throw new BusinessException("Request denied for email '" + userEmail + "' because LINK or OTP is required");
    	}
	}
}