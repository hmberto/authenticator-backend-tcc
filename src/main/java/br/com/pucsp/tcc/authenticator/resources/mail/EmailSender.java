package br.com.pucsp.tcc.authenticator.resources.mail;

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

import br.com.pucsp.tcc.authenticator.utils.system.SystemDefaultVariables;

public class EmailSender {
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailSender.class);

	private static final String EMAIL_SERVER = SystemDefaultVariables.emailServer;
	private static final String EMAIL_BOX = SystemDefaultVariables.emailBox;
	private static final String EMAIL_PASS = SystemDefaultVariables.emailPass;

	public void confirmation(String destinatario, String messageSubject, String messageSend) throws MessagingException {
		Properties props = new Properties();

		LOGGER.info("Email server configuration: " + EMAIL_SERVER);

		switch (EMAIL_SERVER) {
		case "OUTLOOK":
			props.put("mail.smtp.host", "smtp-mail.outlook.com");
			props.put("mail.smtp.socketFactory.port", "587");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.port", "587");
			break;
		case "GMAIL":
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.socketFactory.port", "465");
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.port", "465");
			break;
		default:
			throw new IllegalArgumentException("Invalid email server: " + EMAIL_SERVER);
		}

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(EMAIL_BOX, EMAIL_PASS);
			}
		});

		session.setDebug(false);

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(EMAIL_BOX));

		Address[] toUser = InternetAddress.parse(destinatario);

		message.setRecipients(Message.RecipientType.TO, toUser);
		message.setSubject(messageSubject);
		message.setContent(messageSend, "text/html; charset=UTF-8");

		LOGGER.info("Sending email from '{}' to '{}'", EMAIL_BOX, destinatario);

		Transport.send(message);
	}
}