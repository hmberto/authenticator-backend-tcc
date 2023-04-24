package br.com.pucsp.tcc.authenticator.exceptions;

public class InvalidSessionTokenOrOTPException extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;

    public InvalidSessionTokenOrOTPException(String message) {
        super(message);
    }
}