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
		JSONObject json = new JSONObject();
		SaveUserDB saveUserDB = new SaveUserDB();
		ValidateData validateEmail = new ValidateData();
		SaveActiveCodesDB saveActiveCodesDB = new SaveActiveCodesDB();
		SaveConfirmEmailDB saveConfirmEmailDB = new SaveConfirmEmailDB();
		SaveActiveSessionsDB saveActiveSessionsDB = new SaveActiveSessionsDB();
		CheckEmailAlreadyRegisteredDB checkEmailAlreadyRegisteredDB = new CheckEmailAlreadyRegisteredDB();
		
		JSONObject userJSON = new JSONObject(user.toString());
		String email = userJSON.getString("email");
		
		if(!validateEmail.userEmail(email)) {
			return null;
		}
		
		String verifyEmail = checkEmailAlreadyRegisteredDB.verify(email);
		if(verifyEmail != null) {
			return verifyEmail;
		}
		
		long userId = saveUserDB.insertUser("null", email);
		
		String code = CreateToken.newToken(6);
		saveActiveCodesDB.insertActiveCode(userId, email, code);
		
		String token = CreateToken.newToken(100);
		saveActiveSessionsDB.insertActiveSession(userId, email, token);
		
		saveConfirmEmailDB.insertConfirmEmail(userId, email, false);
		
        json.put("id_user", userId);
        json.put("token", token);
        
		return json.toString();
	}
}