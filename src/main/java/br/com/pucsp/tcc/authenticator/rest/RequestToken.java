package br.com.pucsp.tcc.authenticator.rest;

import org.json.JSONObject;

import br.com.pucsp.tcc.authenticator.token.SendTokenEmail;
import br.com.pucsp.tcc.authenticator.utils.ValidateData;

public class RequestToken {
	public String request(String user) {
		JSONObject userJSON = new JSONObject(user.toString());
		
		String email = userJSON.getString("email");
		String link = userJSON.getString("link");
		String code = userJSON.getString("code");
		
		ValidateData validateEmail = new ValidateData();
		if(!validateEmail.userEmail(email)) {
			return null;
		}
		
		SendTokenEmail sendTokenEmail = new SendTokenEmail();
		boolean isTokenSent = sendTokenEmail.send(email, link, code);
		
		if(!isTokenSent) {
			return null;
		}
		
		return "";
	}
}