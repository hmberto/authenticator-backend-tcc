package br.com.pucsp.tcc.authenticator.exceptions;

public class DatabaseInsertException extends Exception {
	private static final long serialVersionUID = 1L;
    
    public DatabaseInsertException(String message) {
        super(message);
    }	
}