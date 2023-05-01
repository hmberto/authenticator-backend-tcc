package br.com.pucsp.tcc.authenticator.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataValidator.class);

	public static boolean isValidEmail(String email) {
		boolean validEmail = true;
		boolean searchForCharacters = email.toLowerCase().matches("[0-9a-zA-Z-_.]+@[0-9a-zA-Z-_.]+");

		if (!searchForCharacters) {
			LOGGER.error("Invalid email: {}", email);
			validEmail = false;
		}

		return validEmail;
	}

	public static boolean isValidUsername(String username) {
		boolean validUsername = true;
		boolean searchForCharacters = username.matches("[\\p{L}]+");

		if (!searchForCharacters) {
			LOGGER.error("Invalid username: {}", username);
			validUsername = false;
		}

		return validUsername;
	}

	public static boolean isValidToken(String token) {
		boolean validToken = true;
		boolean searchForCharacters = token.matches("[0-9A-Z]+");

		if (!searchForCharacters) {
			LOGGER.error("Invalid token: {}", token);
			validToken = false;
		}

		return validToken;
	}
}