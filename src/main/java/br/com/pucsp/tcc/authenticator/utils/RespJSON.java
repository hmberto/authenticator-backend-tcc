package br.com.pucsp.tcc.authenticator.utils;

import org.json.JSONObject;

public class RespJSON {
	public static String createResp(int userId, boolean isLogin, String session, boolean isSessionTokenActive) {
		String json = new JSONObject()
        		.put("userId", userId)
	            .put("isLogin", isLogin)
	            .put("session", session)
	            .put("isSessionTokenActive", isSessionTokenActive)
	            .toString();
		
		return json;
	}
}