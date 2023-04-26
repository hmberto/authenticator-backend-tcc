package br.com.pucsp.tcc.authenticator.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import br.com.pucsp.tcc.authenticator.database.SqlQueries;
import br.com.pucsp.tcc.authenticator.exceptions.DatabaseInsertException;

public class SaveUserDB {
	public int insert(Connection connection, String userFirstName, String userLastName, String userEmail, String userSessionToken, String userOTP, String userIP, String loginDate) throws Exception {
		int userId = 0;
	    
		UndoChangesSaveUserDB undoChanges = new UndoChangesSaveUserDB();
		
	    try(PreparedStatement statementTimezone = connection.prepareStatement(SqlQueries.TIME_ZONE);
	    		PreparedStatement statementUser = connection.prepareStatement(SqlQueries.INSERT_USER, Statement.RETURN_GENERATED_KEYS);
	    		PreparedStatement statementOTP = connection.prepareStatement(SqlQueries.INSERT_OTP, Statement.RETURN_GENERATED_KEYS);
	    		PreparedStatement statementSessionToken = connection.prepareStatement(SqlQueries.INSERT_SESSION, Statement.RETURN_GENERATED_KEYS);
	    		PreparedStatement statementEmailVerification = connection.prepareStatement(SqlQueries.INSERT_EMAIL_VERIFICATION, Statement.RETURN_GENERATED_KEYS)) {
	    	
	    	statementTimezone.executeUpdate();
	    	
	    	statementUser.setString(1, userFirstName);
	    	statementUser.setString(2, userLastName);
	    	statementUser.setString(3, userEmail);
	    	
	    	userId = insertDB(statementUser, connection);
	    	if(userId <= 0) {
	    		throw new DatabaseInsertException("Error inserting user '" + userEmail + "' into the database");
	    	}
	    	
	        statementOTP.setInt(1, userId);
	        statementOTP.setString(2, userOTP);

	        int otpId = insertDB(statementOTP, connection);
	        if(otpId <= 0) {
	        	undoChanges.recovery(connection, userId);
	        	throw new DatabaseInsertException("Error inserting OTP into the database for user '" + userId + "'");
	        }

	        statementSessionToken.setInt(1, userId);
	        statementSessionToken.setString(2, userSessionToken);
	        statementSessionToken.setBoolean(3, true);

	        int sessionId = insertDB(statementSessionToken, connection);
	        if(sessionId <= 0) {
	        	undoChanges.recovery(connection, userId);
	        	throw new DatabaseInsertException("Error inserting session token into the database for user '" + userId + "'");
	        }
	        
	        statementEmailVerification.setInt(1, userId);
	        
	        int confirmEmailId = insertDB(statementEmailVerification, connection);
	        if(confirmEmailId <= 0) {
	        	undoChanges.recovery(connection, userId);
	        	throw new DatabaseInsertException("Error inserting 'email_verifications' into the database for user '" + userId + "'");
	        }
	    }

	    return userId;
	}

	private int insertDB(PreparedStatement statement, Connection connection) throws SQLException {
		int result = statement.executeUpdate();
		try(ResultSet rs = statement.getGeneratedKeys()) {
			if(rs.next()) {
				result = rs.getInt(1);
			}
		}
		statement.close();
		return result;
	}
}