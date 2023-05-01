package br.com.pucsp.tcc.authenticator.utils;

import java.util.Random;

import br.com.pucsp.tcc.authenticator.utils.system.SystemDefaultVariables;

public class CreateToken {
	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static final int CHARACTERS_LENGTH = CHARACTERS.length();

	private static final int OTP_LENGTH = SystemDefaultVariables.otpLength;
	private static final int SESSION_LENGTH = SystemDefaultVariables.sessionLength;
	private static final int EMAIL_TOKEN_LENGTH = SystemDefaultVariables.emailTokenLength;

	public static String generate(String tokenType) {
		switch (tokenType) {
		case "session":
			return create(SESSION_LENGTH);
		case "otp":
			return create(OTP_LENGTH);
		case "token":
			return create(EMAIL_TOKEN_LENGTH);
		default:
			throw new IllegalArgumentException("Invalid Token Type: {} - Expected value: session or otp");
		}
	}

	private static String create(int tokenLength) {
		StringBuilder tokenBuilder = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < tokenLength; i++) {
			int randomIndex = random.nextInt(CHARACTERS_LENGTH);
			char randomChar = CHARACTERS.charAt(randomIndex);
			tokenBuilder.append(randomChar);
		}
		return tokenBuilder.toString();
	}
}