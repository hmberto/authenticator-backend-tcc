package br.com.pucsp.tcc.authenticator.exceptions;

public class InvalidEmailException extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;

    public InvalidEmailException(String message) {
        super(message);
    }
}