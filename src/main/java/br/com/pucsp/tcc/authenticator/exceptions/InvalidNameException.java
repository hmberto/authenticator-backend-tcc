package br.com.pucsp.tcc.authenticator.exceptions;

public class InvalidNameException extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;

    public InvalidNameException(String message) {
        super(message);
    }
}