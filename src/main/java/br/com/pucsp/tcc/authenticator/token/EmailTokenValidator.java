package br.com.pucsp.tcc.authenticator.token;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.exceptions.InvalidSessionException;
import br.com.pucsp.tcc.authenticator.user.SaveActiveOTPDB;

public class EmailTokenValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailTokenValidator.class);
	
	private static final int OTP_LENGTH = Integer.parseInt(System.getenv("OTP_LENGTH"));
    private static final int SESSION_LENGTH = Integer.parseInt(System.getenv("SESSION_LENGTH"));
	
	public boolean verify(String userSessionTokenOrOTP, String userEmail, boolean isSelectedApprove) {
		boolean validate = false;
		
		try (SaveActiveOTPDB saveActiveCodesDB = new SaveActiveOTPDB()) {
			if(userSessionTokenOrOTP.length() == SESSION_LENGTH && isSelectedApprove) {
				String sql = "UPDATE sessions \n"
				           + "JOIN users ON users.user_id = sessions.user_id \n"
				           + "SET sessions.is_active = true \n"
				           + "WHERE sessions.session = ? AND users.email = ? \n"
				           + "AND sessions.created_at >= DATE_SUB(NOW(), INTERVAL 30 MINUTE) \n"
				           + "AND sessions.is_active = false;";
				
				validate = saveActiveCodesDB.updateCode(sql, userEmail, userSessionTokenOrOTP);
			}
			else if(userSessionTokenOrOTP.length() == OTP_LENGTH) {
				String sql = "UPDATE otps\n"
				           + "JOIN email_verifications ON otps.user_id = email_verifications.user_id\n"
				           + "SET is_active = false, is_confirmed = true\n"
				           + "WHERE otps.otp = ?\n"
				           + "AND otps.is_active = true\n"
				           + "AND otps.user_id = (SELECT user_id FROM users WHERE email = ?)\n"
				           + "AND otps.updated_at >= DATE_SUB(NOW(), INTERVAL 5 MINUTE);\n";
				
				validate = saveActiveCodesDB.updateCode(sql, userEmail, userSessionTokenOrOTP);
			}
			else {
				throw new InvalidSessionException("Invalid token format length: " + userSessionTokenOrOTP.length() + " - OTP code must contain " + OTP_LENGTH + "-digits and session token must contain " + SESSION_LENGTH + "-digits");
			}
		} catch (Exception e) {
			LOGGER.error("Error validating user OTP or Session Token", e);
		}
		
		return validate;
	}
}