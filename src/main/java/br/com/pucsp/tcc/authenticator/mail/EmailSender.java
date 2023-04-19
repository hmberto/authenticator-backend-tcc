package br.com.pucsp.tcc.authenticator.mail;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.rest.RegisterEmail;

public class EmailSender {
	private static final Logger LOGGER = LoggerFactory.getLogger(RegisterEmail.class);
	
	public void confirmation(String destinatario, String messageSubject, String messageSend) throws MessagingException {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp-mail.outlook.com");
		props.put("mail.smtp.socketFactory.port", "587");
		props.put("mail.smtp.starttls.enable","true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "587");
		
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(System.getenv("EMAIL_BOX"), System.getenv("EMAIL_PASS"));
			}
		});
		
		session.setDebug(false);
		
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(System.getenv("EMAIL_BOX")));

			Address[] toUser = InternetAddress.parse(destinatario);
			
			message.setRecipients(Message.RecipientType.TO, toUser);
			message.setSubject(messageSubject);
			message.setContent(messageSend, "text/html; charset=UTF-8");
			
			Transport.send(message);
		} catch (MessagingException e) {
			throw new MessagingException(e.toString());
		}
	}
}