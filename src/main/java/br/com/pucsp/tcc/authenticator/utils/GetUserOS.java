package br.com.pucsp.tcc.authenticator.utils;

import java.util.HashMap;
import java.util.Map;

public class GetUserOS {
	private static final Map<String, String> osMap = new HashMap<>();
	static {
		osMap.put("Windows NT 10.0", "Windows");
		osMap.put("Windows NT 6.2", "Windows 8");
		osMap.put("Windows NT 6.1", "Windows 7");
		osMap.put("Windows NT 6.0", "Windows Vista");
		osMap.put("Windows NT 5.1", "Windows XP");
		osMap.put("Windows NT 5.0", "Windows 2000");
		osMap.put("Android", "Android");
		osMap.put("webOS", "webOS");
		osMap.put("iPhone", "iPhone");
		osMap.put("iPad", "iPad");
		osMap.put("iPod", "iPod");
		osMap.put("Windows Phone", "Windows Phone");
		osMap.put("BlackBerry", "BlackBerry");
		osMap.put("Mac", "Mac/iOS");
		osMap.put("X11", "Linux");
		osMap.put("Linux", "Linux");
	}

	public static String os(String userAgent) {
		String os = "Desconhecido";
		userAgent = userAgent.toUpperCase();

		for (String key : osMap.keySet()) {
			if (userAgent.contains(key.toUpperCase())) {
				os = osMap.get(key);
				break;
			}
		}

		return os;
	}
}