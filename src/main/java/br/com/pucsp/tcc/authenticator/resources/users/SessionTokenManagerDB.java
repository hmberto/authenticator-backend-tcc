package br.com.pucsp.tcc.authenticator.resources.users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import br.com.pucsp.tcc.authenticator.database.SqlQueries;
import br.com.pucsp.tcc.authenticator.utils.exceptions.DatabaseInsertException;

public class SessionTokenManagerDB {
	public void insert(Connection connection, int userId, String userEmail, String userSessionToken, boolean isActive) throws SQLException, DatabaseInsertException {
		int sessionId = 0;
		
		try(PreparedStatement statementTimezone = connection.prepareStatement(SqlQueries.TIME_ZONE);
	    		PreparedStatement statement = connection.prepareStatement(SqlQueries.INSERT_SESSION, Statement.RETURN_GENERATED_KEYS);) {
	    	
	    	statementTimezone.executeUpdate();
	    	
	        statement.setInt(1, userId);
	        statement.setString(2, userSessionToken);
	        statement.setBoolean(3, isActive);
	        
	        statement.executeUpdate();
	        System.out.println("TESTE1");
	        ResultSet rs = statement.getGeneratedKeys();
	        if(rs.next()) {
	        	sessionId = rs.getInt(1);
	        }
	        
	        rs.close();
	    }
		System.out.println("TESTE2:" + sessionId);
		if(sessionId == 0) {
			throw new DatabaseInsertException("Session ID could not be generated");
		}
	}
}