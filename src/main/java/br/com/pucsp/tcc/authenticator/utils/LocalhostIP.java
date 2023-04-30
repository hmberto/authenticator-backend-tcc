package br.com.pucsp.tcc.authenticator.utils;

public class LocalhostIP {
	public static String get(String ip) {
		
		if (ip.equals("[0:0:0:0:0:0:0:1]") || ip.equals("0:0:0:0:0:0:0:1") || ip.equals("localhost") || ip.equals("127.0.0.1")) {
			return "127.0.0.1";
		}
		
		return ip;
	}
}