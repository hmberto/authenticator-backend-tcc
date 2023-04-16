package br.com.pucsp.tcc.authenticator.token;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.exceptions.InvalidSessionException;
import br.com.pucsp.tcc.authenticator.user.SaveActiveCodesDB;

public class EmailTokenValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailTokenValidator.class);
	
	public boolean verify(String token, String email, boolean isSelectedApprove) {
		boolean validate = false;
		
		try (SaveActiveCodesDB saveActiveCodesDB = new SaveActiveCodesDB()) {
			if(token.length() == 100 && isSelectedApprove) {
				String sql = "UPDATE active_sessions \n"
				           + "JOIN users ON users.id_user = active_sessions.id_user \n"
				           + "SET active_sessions.active = true \n"
				           + "WHERE active_sessions.token = ? AND users.email = ? \n"
				           + "AND active_sessions.created_at >= DATE_SUB(NOW(), INTERVAL 30 MINUTE) \n"
				           + "AND active_sessions.active = false;";
				
				validate = saveActiveCodesDB.updateCode(sql, email, token);
			}
			else if(token.length() == 6) {
				String sql = "UPDATE active_codes\n"
				           + "JOIN confirm_email ON active_codes.id_user = confirm_email.id_user\n"
				           + "SET active = false, confirmed = true\n"
				           + "WHERE active_codes.code = ?\n"
				           + "AND active_codes.active = true\n"
				           + "AND active_codes.id_user = (SELECT id_user FROM users WHERE email = ?)\n"
				           + "AND active_codes.updated_at >= DATE_SUB(NOW(), INTERVAL 5 MINUTE);\n";
				
				validate = saveActiveCodesDB.updateCode(sql, email, token);
			}
			else {
				throw new InvalidSessionException("Invalid token format");
			}
		} catch (Exception e) {
			LOGGER.error("Error validating token", e);
		}
		
		return validate;
	}
}