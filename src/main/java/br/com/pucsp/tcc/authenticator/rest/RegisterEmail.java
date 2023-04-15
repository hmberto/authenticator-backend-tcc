package br.com.pucsp.tcc.authenticator.rest;

import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.pucsp.tcc.authenticator.user.CheckEmailAlreadyRegisteredDB;
import br.com.pucsp.tcc.authenticator.user.SaveActiveCodesDB;
import br.com.pucsp.tcc.authenticator.user.SaveActiveSessionsDB;
import br.com.pucsp.tcc.authenticator.user.SaveConfirmEmailDB;
import br.com.pucsp.tcc.authenticator.user.SaveUserDB;
import br.com.pucsp.tcc.authenticator.utils.CreateToken;
import br.com.pucsp.tcc.authenticator.utils.ValidateData;

public class RegisterEmail {
	public String newEmail(String user) throws ClassNotFoundException, JSONException, SQLException {
		JSONObject userJSON = new JSONObject(user.toString());
		String email = userJSON.getString("email");
		
		ValidateData validateEmail = new ValidateData();
		if(!validateEmail.userEmail(email)) {
			return null;
		}
		
		CheckEmailAlreadyRegisteredDB checkEmailAlreadyRegisteredDB = new CheckEmailAlreadyRegisteredDB();
		String verifyEmail = checkEmailAlreadyRegisteredDB.verify(email);
		if(verifyEmail != null) {
			return verifyEmail;
		}
		
		SaveUserDB saveUserDB = new SaveUserDB();
		long userId = saveUserDB.insertUser("null", email);
		
		String code = CreateToken.newToken(6);
		SaveActiveCodesDB saveActiveCodesDB = new SaveActiveCodesDB();
		saveActiveCodesDB.insertActiveCode(userId, email, code);
		
		String token = CreateToken.newToken(100);
		SaveActiveSessionsDB saveActiveSessionsDB = new SaveActiveSessionsDB();
		saveActiveSessionsDB.insertActiveSession(userId, email, token);
		
		SaveConfirmEmailDB saveConfirmEmailDB = new SaveConfirmEmailDB();
		saveConfirmEmailDB.insertConfirmEmail(userId, email, false);
		
		JSONObject json = new JSONObject();
        json.put("id_user", userId);
        json.put("token", token);
        
		return json.toString();
	}
}