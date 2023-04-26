package br.com.pucsp.tcc.authenticator.utils;

import java.util.HashMap;
import java.util.Map;

public class GetUserBrowser {
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

	public static String browser(String userAgent) {
		String os = "Desconhecido";
		userAgent = userAgent.toUpperCase();

		for (String key : browserMap.keySet()) {
			if (userAgent.contains(key.toUpperCase())) {
				os = browserMap.get(key);
				break;
			}
		}

		return os;
	}
}