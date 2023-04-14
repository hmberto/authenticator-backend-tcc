package br.com.pucsp.tcc.authenticator.token;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

public class ValidateTokenEmail {
	private static String name = ValidateTokenEmail.class.getSimpleName();
	private static Logger log = Logger.getLogger(ValidateTokenEmail.class.getName());
	
	public boolean verify(JSONObject userData) {
		log.entering(name, "verify");
		
		boolean validate = true;
		
		String email = userData.getString("email");
		String token = userData.getString("token");
		String approve = userData.getString("approve");
		
		if(token.length() == 6) {
			ValidateTokenEmail.verifyLink(email, token, approve);
		}
		else if(token.length() == 100) {
			ValidateTokenEmail.verifyCode(email, token, approve);
		}
		else {
			log.log(Level.SEVERE, "ValidateTokenEmail.verify: Invalid token format");
			validate = false;
		}
		
		log.exiting(name, "verify");
		return validate;
	}
	
	private static void verifyLink(String email, String token, String approve) {
		
	}
	
	private static void verifyCode(String email, String token, String approve) {
		
	}
}