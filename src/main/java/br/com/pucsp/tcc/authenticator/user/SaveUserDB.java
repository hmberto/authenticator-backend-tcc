package br.com.pucsp.tcc.authenticator.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.ConnDB;
import br.com.pucsp.tcc.authenticator.exceptions.BusinessException;
import br.com.pucsp.tcc.authenticator.mail.EmailType;
import br.com.pucsp.tcc.authenticator.utils.CreateToken;

public class SaveUserDB implements AutoCloseable {
	private static final Logger LOGGER = LoggerFactory.getLogger(SaveUserDB.class);
	
	public int insert(String userName, String userEmail, String userSessionToken) throws SQLException, BusinessException {
	    int userId = 0;
	    
	    UndoChangesSaveUserDB undoChanges = new UndoChangesSaveUserDB();
	    
	    try (Connection connection = ConnDB.getConnection();
	    		PreparedStatement statementUser = connection.prepareStatement("INSERT INTO users (name, email) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
	    		PreparedStatement statementCode = connection.prepareStatement("INSERT INTO active_codes (id_user, code, active) VALUES (?, ?, true)", Statement.RETURN_GENERATED_KEYS);
	    		PreparedStatement statementSession = connection.prepareStatement("INSERT INTO active_sessions (id_user, token, active) VALUES (?, ?, true)", Statement.RETURN_GENERATED_KEYS);
	    		PreparedStatement statementConfirmEmail = connection.prepareStatement("INSERT INTO confirm_email (id_user, confirmed) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {

	        statementUser.setString(1, userName);
	        statementUser.setString(2, userEmail);

	        userId = insertDB(statementUser, connection);
	        if(userId <= 0) {
	            return 0;
	        }

	        String newUserOTP = CreateToken.generate("otp");

	        statementCode.setInt(1, userId);
	        statementCode.setString(2, newUserOTP);

	        int codeId = insertDB(statementCode, connection);
	        if(codeId <= 0) {
	        	LOGGER.error("Error inserting 'active_codes' into the database for user '" + userId + "'. Trying to undo changes");
	        	undoChanges.recovery(userId);
	            return 0;
	        }

	        statementSession.setInt(1, userId);
	        statementSession.setString(2, userSessionToken);

	        int sessionId = insertDB(statementSession, connection);
	        if(sessionId <= 0) {
	        	LOGGER.error("Error inserting 'active_sessions' into the database for user '" + userId + "'. Trying to undo changes");
	        	undoChanges.recovery(userId);
	            return 0;
	        }
	        
	        statementConfirmEmail.setInt(1, userId);
	        statementConfirmEmail.setBoolean(2, false);
	        
	        int confirmEmailId = insertDB(statementConfirmEmail, connection);
	        if(confirmEmailId <= 0) {
	        	LOGGER.error("Error inserting 'confirm_email' into the database for user '" + userId + "'. Trying to undo changes");
	        	undoChanges.recovery(userId);
	            return 0;
	        }
	        
	        EmailType.sendEmailCode(userEmail, newUserOTP);
	    } catch (SQLException e) {
	        LOGGER.error("Error inserting user into database - Email: " + userEmail, e);
	    }

	    return userId;
	}

	private int insertDB(PreparedStatement statement, Connection connection) throws SQLException {
	    int result = statement.executeUpdate();
	    ResultSet rs = statement.getGeneratedKeys();

	    if (rs.next()) {
	        result = rs.getInt(1);
	    }

	    rs.close();
	    statement.close();
	    return result;
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}
}