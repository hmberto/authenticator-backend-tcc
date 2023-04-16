package br.com.pucsp.tcc.authenticator.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.rest.RegisterEmail;

public class ValidateData {
	private static final Logger LOGGER = LoggerFactory.getLogger(RegisterEmail.class);

	public static boolean userEmail(String email) {
		boolean validEmail = true;
		
		boolean searchForCharactersF = email.toLowerCase().matches("[0-9 a-z A-Z - _ .]+@[0-9 a-z A-Z - _ .]+");
		if(!searchForCharactersF) {
			LOGGER.error("Incorrect Email");
			validEmail = false; 
		}
		
		return validEmail;
	}
	
	public static boolean userToken(String token) {
		boolean validEmail = true;
		
		boolean searchForCharactersF = token.matches("[0-9A-Z]+");
		if(!searchForCharactersF) {
			LOGGER.error("Incorrect Token");
			validEmail = false; 
		}
		
		return validEmail;
	}
}