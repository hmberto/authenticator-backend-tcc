package br.com.pucsp.tcc.authenticator.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ValidateData {
	private static String name = ValidateData.class.getSimpleName();
	private static Logger log = Logger.getLogger(ValidateData.class.getName());

	public boolean userEmail(String email) {
		log.entering(name, "userEmail");
		boolean validEmail = true;
		
		boolean searchForCharactersF = email.toLowerCase().matches("[0-9 a-z A-Z - _ .]+@[0-9 a-z A-Z - _ .]+");
		if(!searchForCharactersF) {
			log.log(Level.SEVERE, "ValidateData.userEmail: Incorrect Email");
			validEmail = false; 
		}

		log.exiting(name, "userEmail");
		return validEmail;
	}
	
	public boolean userToken(String token) {
		log.entering(name, "userToken");
		boolean validEmail = true;
		
		boolean searchForCharactersF = token.matches("[0-9A-Z]+");
		if(!searchForCharactersF) {
			log.log(Level.SEVERE, "ValidateData.userToken: Incorrect Token");
			validEmail = false; 
		}

		log.exiting(name, "userToken");
		return validEmail;
	}
}