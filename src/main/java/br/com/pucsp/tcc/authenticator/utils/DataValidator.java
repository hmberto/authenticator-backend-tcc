package br.com.pucsp.tcc.authenticator.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.utils.exceptions.*;
import br.com.pucsp.tcc.authenticator.utils.system.SystemDefaultVariables;

public class DataValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataValidator.class);

	private static final int EMAIL_TOKEN_LENGTH = SystemDefaultVariables.emailTokenLength;
	private static final int SESSION_LENGTH = SystemDefaultVariables.sessionLength;
	private static final int OTP_LENGTH = SystemDefaultVariables.otpLength;

	public static void isValidEmail(String email) {
		if (email == null) {
			String message = "email is required but not sent";

			LOGGER.error(message);
			throw new InvalidEmailException(message);
		}

		boolean searchForCharacters = email.toLowerCase().matches("[0-9a-zA-Z-_.]+@[0-9a-zA-Z-_.]+");

		if (!searchForCharacters) {
			String message = String.format("Invalid email format '%s'", email);

			LOGGER.error(message);
			throw new InvalidEmailException(message);
		}
	}

	public static void isValidName(String name, String nameType) {
		if (name == null) {
			String message = String.format("%s is required but not sent", nameType);

			LOGGER.error(message);
			throw new InvalidNameException(message);
		}

		boolean searchForCharacters = name.matches("[\\p{L}]+");

		if (!searchForCharacters) {
			String message = String.format("Invalid %s format '%s'", nameType, name);

			LOGGER.error(message);
			throw new InvalidNameException(message);
		}
	}

	public static void isValidToken(String token, String tokenType) {
		if (token == null) {
			String message = String.format("%s is required but not sent", tokenType);
			LOGGER.error(message);
			throw new InvalidTokenException(message);
		}

		int expectedLength;
		switch (tokenType) {
		case "otp":
			expectedLength = OTP_LENGTH;
			break;
		case "session":
			expectedLength = SESSION_LENGTH;
			break;
		case "token":
			expectedLength = EMAIL_TOKEN_LENGTH;
			break;
		default:
			throw new IllegalArgumentException(
					"Invalid token type: " + tokenType + " - token type must be session or otp");
		}

		boolean searchForCharacters = token.matches("[0-9A-Z]+");

		if (!searchForCharacters || token.length() != expectedLength) {
			String message = String.format("Invalid %s format '%s'", tokenType, token);
			LOGGER.error(message);
			throw new InvalidTokenException(message);
		}
	}
}