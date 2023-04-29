package br.com.pucsp.tcc.authenticator.utils;

import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.OperatingSystem;

public class GetUserOS {
	public static String os(String userAgent) {
		UserAgent ua = UserAgent.parseUserAgentString(userAgent);
		OperatingSystem os = ua.getOperatingSystem();
		
		return os.getName().equals("Unknown") ? "Desconhecido" : os.getName();
	}
}