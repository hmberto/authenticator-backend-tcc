package br.com.pucsp.tcc.authenticator.token;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;

import br.com.pucsp.tcc.authenticator.database.ConnDB;
import br.com.pucsp.tcc.authenticator.user.SaveActiveSessionsDB;
import br.com.pucsp.tcc.authenticator.utils.CreateToken;
import br.com.pucsp.tcc.authenticator.utils.EmailSender;
import br.com.pucsp.tcc.authenticator.utils.EmailTemplate;

public class SendTokenEmail {
	private static final String CLASS_NAME = SendTokenEmail.class.getSimpleName();
	private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);
	
	public boolean send(String email, String link, String code) {
		long userId = 0;
		try {
			userId = SendTokenEmail.getUserId(email);
		} catch (ClassNotFoundException | SQLException e) {
			LOGGER.log(Level.SEVERE, "Error getting id_user", e);
		}
		
		if(userId == 0) {
			return false;
		}
		
		if("true".equals(link) && "true".equals(code)) {
			LOGGER.log(Level.SEVERE, "SendTokenEmail.send: Both LINK and CODE selected as TRUE");
			return false;
		}
		else if("true".equals(link)) {
			String token = CreateToken.newToken(100);
			
			SaveActiveSessionsDB saveActiveSessionsDB = new SaveActiveSessionsDB();
			try {
				saveActiveSessionsDB.insertActiveSession(userId, email, token);
			} catch (ClassNotFoundException | SQLException e) {
				LOGGER.log(Level.SEVERE, "Error saving token", e);
			}
			
			SendTokenEmail.sendToken(email, link, code, token, "link");
		}
		else if("true".equals(code)) {
			String token = CreateToken.newToken(6);
			SendTokenEmail.sendToken(email, link, code, token, "code");
		}
		else {
			LOGGER.log(Level.SEVERE, "SendTokenEmail.send: Both LINK and CODE selected as FALSE");
			return false;
		}
		
		return true;
	}
	
	private static void sendToken(String email, String link, String code, String token, String type) {
		String messageSubject = null;
		String shortText = "Confirme que este é seu endereço de e-mail";
		String info = null;
		String btnText = null;
		String btnLink = null;
		String messageText = null;
		
		if("link".equals(type)) {
			messageSubject = "Humberto Araújo - TCC PUC-SP: Link de acesso";
			info = "Clique no link abaixo para liberar seu acesso ao site.<br><br>Se você não está tentando fazer login, desconsidere este e-mail.";
			btnText = "Liberar Acesso";
			btnLink = System.getenv("SITE_HOST") + "/auth/confirm-access/" + email.toLowerCase() + "/" + token;
		}
		else if("code".equals(type)) {
			messageSubject = "Humberto Araújo - TCC PUC-SP: Código de acesso";
			info = "Utilize o código abaixo para liberar seu acesso ao site.<br><br>Se você não está tentando fazer login, desconsidere este e-mail.";
			btnText = token;
			btnLink = System.getenv("SITE_HOST") + "/login";
		}
		
		messageText = EmailTemplate.template(messageSubject, info, shortText, btnText, btnLink);
		
		EmailSender sendEmail = new EmailSender();
		try {
			sendEmail.confirmation(email.toLowerCase(), messageSubject, messageText);
		} catch (MessagingException e) {
			LOGGER.log(Level.SEVERE, "Error sending email '" + email + "' with token", e);
		}
	}
	
	private static int getUserId(String email) throws ClassNotFoundException, SQLException {
		int userId = 0;
		
		Connection connection = null;
	    PreparedStatement statement = null;
	    ResultSet rs = null;
	    
		try {
	        connection = ConnDB.getConnection();
	        
	        String sql = "SELECT id_user FROM users WHERE email = ?";
	        
	        statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        statement.setString(1, email);
	        
	        rs = statement.executeQuery();
	        
	        while (rs.next()) {
	        	userId = rs.getInt("id_user");
	        }
	    }
	    catch (SQLException e) {
	        LOGGER.log(Level.SEVERE, "Error getting user", e);
	        throw new SQLException(e);
	    }
		finally {
		    if (rs != null) {
		        try {
		            rs.close();
		        } catch (SQLException e) {
		            LOGGER.log(Level.SEVERE, "Error closing result set", e);
		        }
		    }
		    if (statement != null) {
		        try {
		        	statement.close();
		        } catch (SQLException e) {
		            LOGGER.log(Level.SEVERE, "Error closing statement", e);
		        }
		    }
		    if (connection != null) {
		        try {
		            ConnDB.closeConnection(connection);
		        } catch (SQLException e) {
		            LOGGER.log(Level.SEVERE, "Error closing connection", e);
		        }
		    }
		}
		
	    return userId;
	}
}