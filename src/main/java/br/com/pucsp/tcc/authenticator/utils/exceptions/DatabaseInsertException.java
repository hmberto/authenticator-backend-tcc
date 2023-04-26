package br.com.pucsp.tcc.authenticator.utils.exceptions;

public class DatabaseInsertException extends Exception {
	private static final long serialVersionUID = 1L;
    
    public DatabaseInsertException(String message) {
        super(message);
    }	
}