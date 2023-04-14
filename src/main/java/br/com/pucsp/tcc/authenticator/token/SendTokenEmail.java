package br.com.pucsp.tcc.authenticator.token;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;

import org.json.JSONObject;

import br.com.pucsp.tcc.authenticator.utils.CreateToken;
import br.com.pucsp.tcc.authenticator.utils.EmailSender;
import br.com.pucsp.tcc.authenticator.utils.EmailTemplate;

public class SendTokenEmail {
	private static String name = SendTokenEmail.class.getSimpleName();
	private static Logger log = Logger.getLogger(SendTokenEmail.class.getName());
	
	public boolean send(JSONObject userData) {
		log.entering(name, "send");
		
		boolean validate = true;
		
		String email = userData.getString("email");
		
		if("true".equals(userData.getString("link")) && "true".equals(userData.getString("code"))) {
			log.log(Level.SEVERE, "SendTokenEmail.send: Both LINK and CODE selected as TRUE");
			validate = false;
		}
		else if("true".equals(userData.getString("link"))) {
			SendTokenEmail.sendLink(email);
		}
		else if("true".equals(userData.getString("code"))) {
			SendTokenEmail.sendCode(email);
		}
		else {
			log.log(Level.SEVERE, "SendTokenEmail.send: Both LINK and CODE selected as FALSE");
			validate = false;
		}
		
		log.exiting(name, "send");
		return validate;
	}
	
	private static void sendLink (String email) {
		String token = CreateToken.newToken(100);
		
		String messageSubject = "Humberto Araújo - TCC PUC-SP: Link de acesso";
		String shortText = "Confirme que este é seu endereço de e-mail";
		String info = "Clique no link abaixo para liberar seu acesso ao site.<br><br>Se você não está tentando fazer login, desconsidere este e-mail.";
		String btnText = "Liberar Acesso";
		String btnLink = System.getenv("SITE_HOST") + "/auth/confirm-access/" + email.toLowerCase() + "/" + token;
		String messageText = EmailTemplate.template(messageSubject, info, shortText, btnText, btnLink);
		
		EmailSender sendEmail = new EmailSender();
		
		try {
			sendEmail.confirmation(email.toLowerCase(), messageSubject, messageText);
		} catch (MessagingException e) {
			log.log(Level.SEVERE, "Error: " + e);
		}
	}
	
	private static void sendCode (String email) {
		String token = CreateToken.newToken(6);
		
		String messageSubject = "Humberto Araújo - TCC PUC-SP: Código de acesso";
		String shortText = "Confirme que este é seu endereço de e-mail";
		String info = "Utilize o código abaixo para liberar seu acesso ao site.<br><br>Se você não está tentando fazer login, desconsidere este e-mail.";
		String btnText = token;
		String btnLink = System.getenv("SITE_HOST") + "/login";
		String messageText = EmailTemplate.template(messageSubject, info, shortText, btnText, btnLink);
		
		EmailSender sendEmail = new EmailSender();
		
		try {
			sendEmail.confirmation(email.toLowerCase(), messageSubject, messageText);
		} catch (MessagingException e) {
			log.log(Level.SEVERE, "Error: " + e);
		}
	}
}