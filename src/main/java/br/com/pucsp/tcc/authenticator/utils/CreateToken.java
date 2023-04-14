package br.com.pucsp.tcc.authenticator.utils;

public class CreateToken {
	public static String newToken(int sessionLength) {
		String alphaNumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		
		StringBuilder token = new StringBuilder();
		
		for(int i = 0; i < sessionLength; i++) {
			double random = Math.random();
			int myindex = (int)(alphaNumeric.length() * random);
			
			token.append(alphaNumeric.charAt(myindex));
		}
		
		return token.toString();
	}
}