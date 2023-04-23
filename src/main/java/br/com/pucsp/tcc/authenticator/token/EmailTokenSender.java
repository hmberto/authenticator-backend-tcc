package br.com.pucsp.tcc.authenticator.token;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.exceptions.BusinessException;
import br.com.pucsp.tcc.authenticator.mail.EmailType;
import br.com.pucsp.tcc.authenticator.user.CheckEmailAlreadyRegisteredDB;
import br.com.pucsp.tcc.authenticator.user.SaveActiveOTPDB;
import br.com.pucsp.tcc.authenticator.user.SaveActiveSessionsDB;
import br.com.pucsp.tcc.authenticator.user.SaveUserDB;
import br.com.pucsp.tcc.authenticator.utils.CreateToken;

public class EmailTokenSender {
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailTokenSender.class);
	
	public JSONObject send(String userEmail, String userIP, String loginDate, boolean isSelectedLink, boolean isSelectedOTP) throws Exception {
	    try (CheckEmailAlreadyRegisteredDB checkEmailAlreadyRegisteredDB = new CheckEmailAlreadyRegisteredDB();
	         SaveUserDB saveUserDB = new SaveUserDB();
	         SaveActiveSessionsDB saveActiveSessionsDB = new SaveActiveSessionsDB();
	         SaveActiveOTPDB saveActiveCodesDB = new SaveActiveOTPDB()) {
	    	
	        String emailAlreadyExists = checkEmailAlreadyRegisteredDB.verify(userEmail);
	        
	        JSONObject userJSON = (emailAlreadyExists != null) ? new JSONObject(emailAlreadyExists) : null;
	        
	        String userSession = CreateToken.generate("session");
	        int userId;
	        
	        if (userJSON != null) {
	            userId = userJSON.getInt("userId");
	            LOGGER.info("Email '{}' already registered in the database - user ID: {}", userEmail, userId);
	        }
	        else {
	            userId = saveUserDB.insert("null", "null", userEmail, userSession, userIP, loginDate);
	            
	            if(userId >= 1) {
	            	LOGGER.info("Token requested for unregistered email '{}' in the database - user ID: {}", userEmail, userId);
		            JSONObject json = new JSONObject()
		                    .put("userId", userId)
		                    .put("session", userSession)
		                    .put("isSessionTokenActive", "true")
		                    .put("isLogin", "false");
		            return json;
	            }
	            else {
	            	return null;
	            }
	        }
	        
	        if(isSelectedLink && isSelectedOTP) {
	            throw new BusinessException("Both LINK and CODE selected as TRUE");
	        } else if(isSelectedLink) {
	            JSONObject json = new JSONObject(emailAlreadyExists)
	                    .put("session", userSession)
	                    .put("isSessionTokenActive", "true");
	            
	            int isSaved = saveActiveSessionsDB.insertActiveSession(userId, userEmail, userSession, false);
	            
	            if(isSaved >= 1) {
	                sendToken(userEmail, "", userSession, userIP, loginDate, "session");
	                
	                return json;
	            }
	        } else if (isSelectedOTP) {
	            String userOTP = CreateToken.generate("otp");
	            String sql = "UPDATE otps \n"
	            		+ "SET is_active = true, otp = ? \n"
	            		+ "WHERE user_id = (SELECT user_id FROM users WHERE email = ?);";
	            
	            boolean isOTPSaved = saveActiveCodesDB.updateCode(sql, userEmail, userOTP);
	            int isTokenSaved = saveActiveSessionsDB.insertActiveSession(userId, userEmail, userSession, true);
	            
	            JSONObject json = new JSONObject(emailAlreadyExists)
	                    .put("session", userSession)
	            		.put("isSessionTokenActive", "true");
	            if (isOTPSaved && isTokenSaved >= 1) {
	                sendToken(userEmail, userOTP, "", userIP, loginDate, "otp");
	                
	                return json;
	            }
	        } else {
	            throw new BusinessException("Both LINK and CODE selected as FALSE");
	        }

	        return null;
	    }
	}

	private static void sendToken(String userEmail, String userOTP, String userSessionToken, String userIP, String loginDate, String tokenType) throws BusinessException {
		switch (tokenType) {
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
}