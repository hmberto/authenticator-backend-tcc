package br.com.pucsp.tcc.authenticator.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;

import br.com.pucsp.tcc.authenticator.database.ConnDB;
import br.com.pucsp.tcc.authenticator.utils.EmailSender;
import br.com.pucsp.tcc.authenticator.utils.EmailTemplate;

public class SaveActiveCodesDB {
	private static final String CLASS_NAME = SaveActiveCodesDB.class.getSimpleName();
	private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);
	
	public void insertActiveCode(long userId, String email, String userCode) throws ClassNotFoundException, SQLException {
	    LOGGER.entering(CLASS_NAME, "insertActiveCode");
	    
	    Connection connection = null;
	    PreparedStatement statement = null;
	    
	    try {
	        connection = ConnDB.connect();
	        
	        String sql = "INSERT INTO active_codes (id_user, code) VALUES (?, ?)";
	        
	        statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        statement.setInt(1, (int) userId);
	        statement.setString(2, userCode);
	        
	        statement.executeUpdate();
	    }
	    catch (SQLException e) {
	        LOGGER.log(Level.SEVERE, "Error inserting user", e);
	        throw new SQLException(e);
	    }
	    finally {
	        if (statement != null) {
	            try {
	                statement.close();
	                SaveActiveCodesDB.sendCode(email, userCode);
	            } catch (SQLException e) {
	                LOGGER.log(Level.SEVERE, "Error closing statement", e);
	            }
	        }
	        if (connection != null) {
	            ConnDB.disconnect();
	        }
	    }
	    
	    LOGGER.exiting(CLASS_NAME, "insertActiveCode");
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
			LOGGER.log(Level.SEVERE, "Error: " + e);
		}
	}
}