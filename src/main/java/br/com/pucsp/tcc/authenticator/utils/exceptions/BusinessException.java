package br.com.pucsp.tcc.authenticator.utils.exceptions;

public class BusinessException extends Exception {
	private static final long serialVersionUID = 1L;

	public BusinessException(String message) {
		super(message);
	}
}