package br.com.pucsp.tcc.authenticator.utils;

import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Browser;

import java.util.HashMap;
import java.util.Map;

public class GetUserBrowser {
	public static String browser(String userAgent) {
		UserAgent ua = UserAgent.parseUserAgentString(userAgent);
		Browser browser = ua.getBrowser();

		String gettedBrouser = "Unknown".equals(browser.getGroup().getName()) ? "Desconhecido"
				: browser.getGroup().getName();
		return "Desconhecido".equals(gettedBrouser) ? verify(userAgent) : verify(gettedBrouser);
	}

	private static final Map<String, String> browserMap = new HashMap<>();
	static {
		browserMap.put("Chrome", "Google Chrome");
		browserMap.put("Firefox", "Firefox");
		browserMap.put("Opera", "Opera");
		browserMap.put("MSIE", "Internet Explorer");
		browserMap.put("Trident/", "Internet Explorer");
		browserMap.put("Safari", "Safari");
		browserMap.put("Edge", "Microsoft Edge");
		browserMap.put("Edg", "Microsoft Edge (Chromium-based)");
		browserMap.put("YaBrowser", "Yandex Browser");
		browserMap.put("UCBrowser", "UC Browser");
		browserMap.put("OPR", "Opera");
		browserMap.put("Brave", "Brave");
		browserMap.put("Vivaldi", "Vivaldi");
		browserMap.put("Maxthon", "Maxthon");
		browserMap.put("Avant Browser", "Avant Browser");
		browserMap.put("Insomnia", "Insomnia");
		browserMap.put("PostmanRuntime", "Postman");
	}

	private static String verify(String userBrowser) {
		for (String key : browserMap.keySet()) {
			if (userBrowser.toUpperCase().contains(key.toUpperCase())) {
				userBrowser = browserMap.get(key);
				break;
			}
		}

		return userBrowser;
	}
}