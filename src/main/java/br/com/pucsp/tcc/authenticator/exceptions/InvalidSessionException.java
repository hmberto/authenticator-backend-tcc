package br.com.pucsp.tcc.authenticator.exceptions;

public class InvalidSessionException extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;

    public InvalidSessionException(String message) {
        super(message);
    }
}