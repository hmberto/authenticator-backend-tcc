package br.com.pucsp.tcc.authenticator.mail;

import javax.mail.MessagingException;

import br.com.pucsp.tcc.authenticator.exceptions.BusinessException;

public class EmailType {
	public static void sendEmailCode(String email, String userCode) throws BusinessException {
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
			throw new BusinessException("Error sending email with OTP");
		}
	}
	
	public static void sendEmailLink(String email, String userSessionToken) throws BusinessException {
		String messageSubject = "Humberto Araújo - TCC PUC-SP: Link de acesso";
		String shortText = "Confirme que este é seu endereço de e-mail";
		String info = "Clique no link abaixo para liberar seu acesso ao site.<br><br>Se você não está tentando fazer login, desconsidere este e-mail.";
		String btnText = "Liberar Acesso";
		String btnLink = System.getenv("SITE_HOST") + "/confirm-access/" + email.trim().toLowerCase() + "/" + userSessionToken;
		String messageText = EmailTemplate.template(messageSubject, info, shortText, btnText, btnLink);
		
		EmailSender sendEmail = new EmailSender();
		
		try {
			sendEmail.confirmation(email.toLowerCase(), messageSubject, messageText);
		} catch (MessagingException e) {
			throw new BusinessException("Error sending email with session token");
		}
	}
}