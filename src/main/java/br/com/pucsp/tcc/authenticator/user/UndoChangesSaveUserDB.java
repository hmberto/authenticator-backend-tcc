package br.com.pucsp.tcc.authenticator.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.ConnDB;

public class UndoChangesSaveUserDB {
	private static final Logger LOGGER = LoggerFactory.getLogger(UndoChangesSaveUserDB.class);
	
	public void recovery(int userId) throws SQLException {
	    try (Connection connection = ConnDB.getConnection();
	    		PreparedStatement statementUser = connection.prepareStatement("DELETE FROM sessions WHERE user_id = ? AND EXISTS (SELECT user_id FROM sessions WHERE user_id = ?);", Statement.RETURN_GENERATED_KEYS);
	    		PreparedStatement statementCode = connection.prepareStatement("DELETE FROM otps WHERE user_id = ? AND EXISTS (SELECT user_id FROM otps WHERE user_id = ?);", Statement.RETURN_GENERATED_KEYS);
	    		PreparedStatement statementSession = connection.prepareStatement("DELETE FROM email_verifications WHERE user_id = ? AND EXISTS (SELECT user_id FROM email_verifications WHERE user_id = ?);", Statement.RETURN_GENERATED_KEYS);
	    		PreparedStatement statementConfirmEmail = connection.prepareStatement("DELETE FROM users WHERE user_id = ? AND EXISTS (SELECT user_id FROM users WHERE user_id = ?);", Statement.RETURN_GENERATED_KEYS)) {
	    	
	        statementCode.setInt(1, userId);
	        statementCode.setInt(2, userId);
	        insertDB(statementCode, connection);
	        
	        statementSession.setInt(1, userId);
	        statementSession.setInt(2, userId);
	        insertDB(statementSession, connection);
	        
	        statementConfirmEmail.setInt(1, userId);
	        statementConfirmEmail.setInt(2, userId);
	        insertDB(statementConfirmEmail, connection);
	        
	        statementUser.setInt(1, userId);
	        statementUser.setInt(2, userId);
	        insertDB(statementUser, connection);
	        
	        LOGGER.info("Changes undone: user '" + userId + "' removed from database - Cause: error during new database sign up");
	    } catch (SQLException e) {
	        LOGGER.error("Error removing user from database - User ID: " + userId, e);
	    }
	}

	private void insertDB(PreparedStatement statement, Connection connection) throws SQLException {
	    statement.executeUpdate();
	    statement.close();
	}
}