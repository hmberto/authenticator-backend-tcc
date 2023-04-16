package br.com.pucsp.tcc.authenticator.token;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.exceptions.BusinessException;
import br.com.pucsp.tcc.authenticator.user.CheckEmailAlreadyRegisteredDB;
import br.com.pucsp.tcc.authenticator.user.SaveActiveCodesDB;
import br.com.pucsp.tcc.authenticator.user.SaveActiveSessionsDB;
import br.com.pucsp.tcc.authenticator.user.SaveUserDB;
import br.com.pucsp.tcc.authenticator.utils.CreateToken;
import br.com.pucsp.tcc.authenticator.utils.EmailType;

public class EmailTokenSender {
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailTokenSender.class);
	
	public JSONObject send(String email, boolean isSelectedLink, boolean isSelectedCode) throws Exception {
	    try (CheckEmailAlreadyRegisteredDB checkEmailAlreadyRegisteredDB = new CheckEmailAlreadyRegisteredDB();
	         SaveUserDB saveUserDB = new SaveUserDB();
	         SaveActiveSessionsDB saveActiveSessionsDB = new SaveActiveSessionsDB();
	         SaveActiveCodesDB saveActiveCodesDB = new SaveActiveCodesDB()) {
	    	
	        String emailAlreadyExists = checkEmailAlreadyRegisteredDB.verify(email);
	        
	        JSONObject userExistsJSON = (emailAlreadyExists != null) ? new JSONObject(emailAlreadyExists) : null;
	        
	        String userToken = CreateToken.generate(100);
	        int userId;
	        if (userExistsJSON != null) {
	            userId = userExistsJSON.getInt("id_user");
	            LOGGER.info("Email '{}' already registered in the database - user ID: {}", email, userId);
	        } else {
	            userId = saveUserDB.insert("null", email, userToken);
	            if(userId >= 1) {
	            	LOGGER.info("Token requested for unregistered email '{}' in the database - user ID: {}", email, userId);
		            JSONObject json = new JSONObject()
		                    .put("id_user", userId)
		                    .put("token", userToken);
		            return json;
	            }
	            else {
	            	return null;
	            }
	        }
	        
	        if(isSelectedLink && isSelectedCode) {
	            throw new BusinessException("Both LINK and CODE selected as TRUE");
	        } else if(isSelectedLink) {
	            JSONObject json = new JSONObject()
	                    .put("id_user", userId)
	                    .put("token", userToken);
	            
	            int isSaved = saveActiveSessionsDB.insertActiveSession(userId, email, userToken, false);
	            
	            if(isSaved >= 1) {
	                sendToken(email, "", userToken, "link");
	                return json;
	            }
	        } else if (isSelectedCode) {
	            String userCode = CreateToken.generate(6);
	            String sql = "UPDATE active_codes \n"
	            		+ "SET active = true, code = ? \n"
	            		+ "WHERE id_user = (SELECT id_user FROM users WHERE email = ?);";
	            boolean isCodeSaved = saveActiveCodesDB.updateCode(sql, email, userCode);
	            
	            int isTokenSaved = saveActiveSessionsDB.insertActiveSession(userId, email, userToken, true);
	            
	            JSONObject json = new JSONObject()
	                    .put("id_user", userId)
	                    .put("token", userToken);

	            if (isCodeSaved && isTokenSaved >= 1) {
	                sendToken(email, userCode, "", "code");
	                return json;
	            }
	        } else {
	            throw new BusinessException("Both LINK and CODE selected as FALSE");
	        }

	        return null;
	    }
	}

	
	private static void sendToken(String email, String code, String token, String type) throws BusinessException {
		switch (type) {
        	case "link":
        		EmailType.sendEmailLink(email, token);
				break;
        	case "code":
        		EmailType.sendEmailCode(email, code);
				break;
        	default:
                throw new IllegalArgumentException("Invalid token type: " + type);
		}
	}
}