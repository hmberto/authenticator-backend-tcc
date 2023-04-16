package br.com.pucsp.tcc.authenticator.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.ConnDB;
import br.com.pucsp.tcc.authenticator.rest.RegisterEmail;
import br.com.pucsp.tcc.authenticator.utils.EmailSender;
import br.com.pucsp.tcc.authenticator.utils.EmailTemplate;

public class SaveActiveCodesDB {
	private static final Logger LOGGER = LoggerFactory.getLogger(RegisterEmail.class);
	
	public void insertActiveCode(long userId, String email, String userCode) throws ClassNotFoundException, SQLException {
	    Connection connection = null;
	    PreparedStatement statement = null;
	    
	    try {
	        connection = ConnDB.getConnection();
	        
	        String sql = "INSERT INTO active_codes (id_user, code) VALUES (?, ?)";
	        
	        statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        statement.setInt(1, (int) userId);
	        statement.setString(2, userCode);
	        
	        statement.executeUpdate();
	    }
	    catch (SQLException e) {
	    	LOGGER.error("Error inserting user", e);
	    }
	    finally {
	    	SaveActiveCodesDB.sendCode(email, userCode);
	    	
		    if (statement != null) {
		        try {
		        	statement.close();
		        } catch (SQLException e) {
		        	LOGGER.error("Error closing statement", e);
		        }
		    }
		    if (connection != null) {
		        try {
		            ConnDB.closeConnection(connection);
		        } catch (SQLException e) {
		        	LOGGER.error("Error closing connection", e);
		        }
		    }
		}
	}
	
	private static void sendCode (String email, String userCode) {
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