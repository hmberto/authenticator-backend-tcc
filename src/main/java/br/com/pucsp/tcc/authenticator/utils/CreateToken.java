package br.com.pucsp.tcc.authenticator.utils;

import java.util.Random;

public class CreateToken {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CHARACTERS_LENGTH = CHARACTERS.length();
    
    public static String generate(int tokenLength) {
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