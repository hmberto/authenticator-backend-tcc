package br.com.pucsp.tcc.authenticator.utils.exceptions;

public class InvalidNameException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;

	public InvalidNameException(String message) {
		super(message);
	}
}