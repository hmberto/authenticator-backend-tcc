package br.com.pucsp.tcc.authenticator.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.ConnDB;
import br.com.pucsp.tcc.authenticator.rest.RegisterEmail;
import br.com.pucsp.tcc.authenticator.utils.CreateToken;
import br.com.pucsp.tcc.authenticator.utils.EmailSender;
import br.com.pucsp.tcc.authenticator.utils.EmailTemplate;

public class SaveUserDB {
	private static final Logger LOGGER = LoggerFactory.getLogger(RegisterEmail.class);
	
	public int insert(String userName, String userEmail, String userSession) throws SQLException {
	    int userId = 0;

	    try (Connection connection = ConnDB.getConnection();
	    		PreparedStatement statementUser = connection.prepareStatement("INSERT INTO users (name, email) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
	    		PreparedStatement statementCode = connection.prepareStatement("INSERT INTO active_codes (id_user, code) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
	    		PreparedStatement statementSession = connection.prepareStatement("INSERT INTO active_sessions (id_user, token) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
	    		PreparedStatement statementConfirmEmail = connection.prepareStatement("INSERT INTO confirm_email (id_user, confirmed) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {

	        statementUser.setString(1, userName);
	        statementUser.setString(2, userEmail);

	        userId = insertDB(statementUser, connection);
	        if (userId <= 0) {
	            return 0;
	        }

	        String code = CreateToken.newToken(6);

	        statementCode.setInt(1, userId);
	        statementCode.setString(2, code);

	        int codeId = insertDB(statementCode, connection);
	        if (codeId <= 0) {
	            return 0;
	        }

	        statementSession.setInt(1, userId);
	        statementSession.setString(2, userSession);

	        int sessionId = insertDB(statementSession, connection);
	        if (sessionId <= 0) {
	            return 0;
	        }
	        
	        statementConfirmEmail.setInt(1, userId);
	        statementConfirmEmail.setBoolean(2, false);
	        int confirmEmailId = insertDB(statementConfirmEmail, connection);
	        if (confirmEmailId <= 0) {
	            return 0;
	        }

	        sendEmailCode(userEmail, code);
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
	
	private static void sendEmailCode(String email, String userCode) {
		String messageSubject = "Humberto Araújo - TCC PUC-SP: Código de acesso";
		String shortText = "Confirme que este é seu endereço de e-mail";
		String info = "Utilize o código abaixo para liberar seu acesso ao site.<br><br>Se você não está tentando fazer login, desconsidere este e-mail.";
		String btnText = userCode;
		String btnLink = System.getenv("SITE_HOST") + "/login";
		String messageText = EmailTemplate.template(messageSubject, info, shortText, btnText, btnLink);
		
		EmailSender sendEmail = new EmailSender();
		
		try {
			sendEmail.confirmation(email.toLowerCase(), messageSubject, messageText);
		} catch (MessagingException e) {
			LOGGER.error("Error sendding email", e);
		}
	}
}