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
				String sql = "UPDATE active_sessions \n"
				           + "JOIN users ON users.id_user = active_sessions.id_user \n"
				           + "SET active_sessions.active = true \n"
				           + "WHERE active_sessions.token = ? AND users.email = ? \n"
				           + "AND active_sessions.created_at >= DATE_SUB(NOW(), INTERVAL 30 MINUTE) \n"
				           + "AND active_sessions.active = false;";
				
				validate = saveActiveCodesDB.updateCode(sql, userEmail, userSessionTokenOrOTP);
			}
			else if(userSessionTokenOrOTP.length() == OTP_LENGTH) {
				String sql = "UPDATE active_codes\n"
				           + "JOIN confirm_email ON active_codes.id_user = confirm_email.id_user\n"
				           + "SET active = false, confirmed = true\n"
				           + "WHERE active_codes.code = ?\n"
				           + "AND active_codes.active = true\n"
				           + "AND active_codes.id_user = (SELECT id_user FROM users WHERE email = ?)\n"
				           + "AND active_codes.updated_at >= DATE_SUB(NOW(), INTERVAL 5 MINUTE);\n";
				
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