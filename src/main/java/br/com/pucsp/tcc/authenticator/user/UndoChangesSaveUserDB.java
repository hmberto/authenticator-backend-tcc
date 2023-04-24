package br.com.pucsp.tcc.authenticator.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.ConnDB;
import br.com.pucsp.tcc.authenticator.database.SqlQueries;
import br.com.pucsp.tcc.authenticator.exceptions.DatabaseInsertException;

public class UndoChangesSaveUserDB implements AutoCloseable {
	private static final Logger LOGGER = LoggerFactory.getLogger(UndoChangesSaveUserDB.class);
	
	public void recovery(int userId) throws SQLException, DatabaseInsertException {
		try (Connection connection = ConnDB.getConnection();
	    		PreparedStatement statementUser = connection.prepareStatement(SqlQueries.DELETE_USER, Statement.RETURN_GENERATED_KEYS);
	    		PreparedStatement statementOTP = connection.prepareStatement(SqlQueries.DELETE_OTP, Statement.RETURN_GENERATED_KEYS);
	    		PreparedStatement statementSession = connection.prepareStatement(SqlQueries.DELETE_SESSION, Statement.RETURN_GENERATED_KEYS);
	    		PreparedStatement statementConfirmEmail = connection.prepareStatement(SqlQueries.DELETE_EMAIL_VERIFICATION, Statement.RETURN_GENERATED_KEYS)) {
	    	
	    	statementOTP.setInt(1, userId);
	    	statementOTP.setInt(2, userId);
	    	insertDB(statementOTP, connection);
	        
	        statementSession.setInt(1, userId);
	        statementSession.setInt(2, userId);
	        insertDB(statementSession, connection);
	        
	        statementConfirmEmail.setInt(1, userId);
	        statementConfirmEmail.setInt(2, userId);
	        insertDB(statementConfirmEmail, connection);
	        
	        statementUser.setInt(1, userId);
	        statementUser.setInt(2, userId);
	        insertDB(statementUser, connection);
	        
	        LOGGER.info("Changes undone: user '{}' removed from database - Cause: error during new database sign up", userId);
	    } catch(SQLException e) {
	    	throw new DatabaseInsertException("Error removing user from database - User ID: " + userId);
	    }
	}
	
	private void insertDB(PreparedStatement statement, Connection connection) throws SQLException {
		statement.executeUpdate();
	    statement.close();
	}
	
	@Override
	public void close() throws Exception {}
}