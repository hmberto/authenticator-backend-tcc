package br.com.pucsp.tcc.authenticator.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTime {
	private static final Logger LOGGER = LoggerFactory.getLogger(DateTime.class);
	
	public static String date() {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd 'de' MMM. 'de' yyyy HH:mm", new Locale("pt", "BR"));
		String loginDate = now.format(formatador);
		
		return loginDate;
	}
	
	public static String formatDate(String date) {
		SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat formatoSaida = new SimpleDateFormat("dd 'de' MMM. 'de' yyyy HH:mm");
		
		Date data;
		try {
			data = formatoEntrada.parse(date);
			String dataFormatada = formatoSaida.format(data);
			
			return dataFormatada + "h";
		} catch (ParseException e) {
			LOGGER.error("Error parsing the date: {}", e.getMessage());
		}
		
		return null;
	}
}