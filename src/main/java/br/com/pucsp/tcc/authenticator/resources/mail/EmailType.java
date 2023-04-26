package br.com.pucsp.tcc.authenticator.resources.mail;

import javax.mail.MessagingException;

public class EmailType {
	public static void sendEmailOTP(String userEmail, String userOTP, String userIP, String loginDate) throws MessagingException {
		String messageSubject = "Humberto Araújo - TCC PUC-SP: Código de acesso";
		String shortText = "Confirme que este é seu endereço de e-mail";
		String info = "Utilize o código abaixo para liberar seu acesso ao site.<br><br>Se você não está tentando fazer login, desconsidere este e-mail.";
		String btnText = userOTP;
		String btnLink = "#";
		String messageText = EmailTemplate.template(messageSubject, info, shortText, btnText, btnLink);
		
		EmailSender sendEmail = new EmailSender();
		
		sendEmail.confirmation(userEmail.toLowerCase(), messageSubject, messageText);
	}
	
	public static void sendEmailLink(String userEmail, String userSessionToken, String userEmailToken) throws MessagingException {
		StringBuilder sb = new StringBuilder();
		sb.append(System.getenv("SITE_HOST"));
		sb.append("/confirm-access");
		sb.append("?token=");
		sb.append(userEmailToken);
		sb.append("&session=");
		sb.append(userSessionToken);
		sb.append("&email=");
		sb.append(userEmail.trim().toLowerCase());
		
		String messageSubject = "Humberto Araújo - TCC PUC-SP: Link de acesso";
		String shortText = "Confirme que este é seu endereço de e-mail";
		String info = "Clique no link abaixo para liberar seu acesso ao site.<br><br>Se você não está tentando fazer login, desconsidere este e-mail.";
		String btnText = "Liberar Acesso";
		String btnLink = sb.toString();
		String messageText = EmailTemplate.template(messageSubject, info, shortText, btnText, btnLink);
		
		EmailSender sendEmail = new EmailSender();
		
		sendEmail.confirmation(userEmail.toLowerCase(), messageSubject, messageText);
	}
}