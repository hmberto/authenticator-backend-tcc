package br.com.pucsp.tcc.authenticator.token;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.SqlQueries;
import br.com.pucsp.tcc.authenticator.exceptions.InvalidEmailException;
import br.com.pucsp.tcc.authenticator.exceptions.InvalidSessionTokenOrOTPException;
import br.com.pucsp.tcc.authenticator.user.SessionTokenAndOTPManagerDB;
import br.com.pucsp.tcc.authenticator.utils.DataValidator;

public class EmailSessionTokenOrOTPValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailSessionTokenOrOTPValidator.class);
	
	private static final int OTP_LENGTH = Integer.parseInt(System.getenv("OTP_LENGTH"));
    private static final int SESSION_LENGTH = Integer.parseInt(System.getenv("SESSION_LENGTH"));
	
	public boolean verify(final JSONObject body, final String userIP, final String loginDate) throws Exception {
		boolean validate = false;
		
		String userEmail = body.has("email") ? body.getString("email").trim().toLowerCase() : null;
		String userSessionTokenOrOTP = body.getString("sessionTokenOrOTP").trim().toUpperCase();
		boolean isSelectedApprove = body.has("approve") ? body.getBoolean("approve") : false;
		
		validateBody(userEmail, userSessionTokenOrOTP, isSelectedApprove);
		
		try(SessionTokenAndOTPManagerDB saveSessionTokenAndOTP = new SessionTokenAndOTPManagerDB()) {
			if(userSessionTokenOrOTP.length() == SESSION_LENGTH && isSelectedApprove) {
				validate = saveSessionTokenAndOTP.insert(SqlQueries.UPDATE_SESSION, userEmail, userSessionTokenOrOTP);
			}
			else {
				validate = saveSessionTokenAndOTP.insert(SqlQueries.UPDATE_OTP, userEmail, userSessionTokenOrOTP);
			}
		}
		catch(Exception e) {
			LOGGER.error("Unknown error occurred while validating OTP or Session Token", e);
		}
		
		return validate;
	}
	
	private static void validateBody(String userEmail, String userSessionTokenOrOTP, boolean isSelectedApprove) throws Exception {
		if(userEmail == null) {
			throw new InvalidEmailException("Email is required but not sent");
		}
		if(!DataValidator.isValidEmail(userEmail)) {
			throw new InvalidEmailException("Invalid format for email '" + userEmail + "'");
        }
		if(!DataValidator.isValidToken(userSessionTokenOrOTP) || (userSessionTokenOrOTP.length() != OTP_LENGTH && userSessionTokenOrOTP.length() != SESSION_LENGTH)) {
			throw new InvalidSessionTokenOrOTPException("Invalid Session Token or OTP format");
		}
	}
}