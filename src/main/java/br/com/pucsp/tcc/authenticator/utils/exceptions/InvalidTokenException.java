package br.com.pucsp.tcc.authenticator.utils.exceptions;

public class InvalidTokenException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;

	public InvalidTokenException(String message) {
		super(message);
	}
}