package br.com.pucsp.tcc.authenticator.utils;

import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {
	private static String name = EmailSender.class.getSimpleName();
	private static Logger log = Logger.getLogger(EmailSender.class.getName());
	
	public void confirmation(String destinatario, String messageSubject, String messageSend) throws MessagingException {
		log.entering(name, "confirmation");
		
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
			log.exiting(name, "confirmation");
		} catch (MessagingException e) {
			throw new MessagingException(e.toString());
		}
	}
}