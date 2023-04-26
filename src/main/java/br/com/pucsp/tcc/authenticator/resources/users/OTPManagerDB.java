package br.com.pucsp.tcc.authenticator.resources.users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import br.com.pucsp.tcc.authenticator.utils.exceptions.DatabaseInsertException;

public class OTPManagerDB {
	public void insert(Connection connection, String sql, String userEmail, String userOTP) throws SQLException, DatabaseInsertException {
		int rowsUpdated = 0;
		
		try(PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	        statement.setString(1, userOTP);
	        statement.setString(2, userEmail);
	        
	        rowsUpdated = statement.executeUpdate();
	    }
		
		if(rowsUpdated == 0) {
        	throw new DatabaseInsertException("OTP could not be updated");
        }
	}
}