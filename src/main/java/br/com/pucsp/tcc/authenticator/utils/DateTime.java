package br.com.pucsp.tcc.authenticator.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateTime {
	public static String date() {
		LocalDateTime agora = LocalDateTime.now();
		DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd 'de' MMM. 'de' yyyy HH:mm", new Locale("pt", "BR"));
		String loginDate = agora.format(formatador);
		
		return loginDate;
	}
}