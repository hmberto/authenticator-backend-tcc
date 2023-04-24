package br.com.pucsp.tcc.authenticator.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.ConnDB;
import br.com.pucsp.tcc.authenticator.database.SqlQueries;
import br.com.pucsp.tcc.authenticator.exceptions.DatabaseInsertException;
import br.com.pucsp.tcc.authenticator.mail.EmailType;
import br.com.pucsp.tcc.authenticator.utils.CreateToken;

public class SaveUserDB implements AutoCloseable {
	private static final Logger LOGGER = LoggerFactory.getLogger(SaveUserDB.class);
	
	public int insert(String userFirstName, String userLastName, String userEmail, String userSessionToken, String userIP, String loginDate) throws Exception {
		int userId = 0;
	    
	    try(Connection connection = ConnDB.getConnection();
	    		UndoChangesSaveUserDB undoChanges = new UndoChangesSaveUserDB();
	    		PreparedStatement statementUser = connection.prepareStatement(SqlQueries.INSERT_USER, Statement.RETURN_GENERATED_KEYS);
	    		PreparedStatement statementOTP = connection.prepareStatement(SqlQueries.INSERT_OTP, Statement.RETURN_GENERATED_KEYS);
	    		PreparedStatement statementSessionToken = connection.prepareStatement(SqlQueries.INSERT_SESSION, Statement.RETURN_GENERATED_KEYS);
	    		PreparedStatement statementEmailVerification = connection.prepareStatement(SqlQueries.INSERT_EMAIL_VERIFICATION, Statement.RETURN_GENERATED_KEYS)) {
	    	
	    	statementUser.setString(1, userFirstName);
	    	statementUser.setString(2, userLastName);
	    	statementUser.setString(3, userEmail);
	    	
	    	userId = insertDB(statementUser, connection);
	    	if(userId <= 0) {
	    		throw new DatabaseInsertException("Error inserting user '" + userEmail + "' into the database");
	    	}
	    	
	        String newUserOTP = CreateToken.generate("otp");

	        statementOTP.setInt(1, userId);
	        statementOTP.setString(2, newUserOTP);

	        int otpId = insertDB(statementOTP, connection);
	        if(otpId <= 0) {
	        	undoChanges.recovery(userId);
	        	throw new DatabaseInsertException("Error inserting OTP into the database for user '" + userId + "'");
	        }

	        statementSessionToken.setInt(1, userId);
	        statementSessionToken.setString(2, userSessionToken);
	        statementSessionToken.setBoolean(3, true);

	        int sessionId = insertDB(statementSessionToken, connection);
	        if(sessionId <= 0) {
	        	undoChanges.recovery(userId);
	        	throw new DatabaseInsertException("Error inserting session token into the database for user '" + userId + "'");
	        }
	        
	        statementEmailVerification.setInt(1, userId);
	        
	        int confirmEmailId = insertDB(statementEmailVerification, connection);
	        if(confirmEmailId <= 0) {
	        	undoChanges.recovery(userId);
	        	throw new DatabaseInsertException("Error inserting 'email_verifications' into the database for user '" + userId + "'");
	        }
	        
	        LOGGER.info("User '{}' created in database - userId: {}", userEmail, userId);
	        
	        EmailType.sendEmailOTP(userEmail, newUserOTP, userIP, loginDate);
	    } catch (SQLException e) {
	        throw new DatabaseInsertException("Error inserting user into database - Email: " + userEmail);
	    }

	    return userId;
	}

	private int insertDB(PreparedStatement statement, Connection connection) throws SQLException {
		try(ResultSet rs = statement.getGeneratedKeys()) {
			int result = statement.executeUpdate();
			if (rs.next()) {
				result = rs.getInt(1);
			}
			return result;
		}
		catch(SQLException e) {
			statement.close();
			throw e;
		}
	}
	
	@Override
	public void close() throws Exception {}
}