package br.com.pucsp.tcc.authenticator.utils.exceptions;

public class UnregisteredUserException extends Exception {
	private static final long serialVersionUID = 1L;

	public UnregisteredUserException(String message) {
		super(message);
	}
}