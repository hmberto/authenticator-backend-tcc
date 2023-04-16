package br.com.pucsp.tcc.authenticator.rest;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.user.CheckEmailAlreadyRegisteredDB;
import br.com.pucsp.tcc.authenticator.user.SaveUserDB;
import br.com.pucsp.tcc.authenticator.utils.CreateToken;
import br.com.pucsp.tcc.authenticator.utils.ValidateData;

@Path("/user/register/email")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RegisterEmail {
	private static final Logger LOGGER = LoggerFactory.getLogger(RegisterEmail.class);
	
	@POST
	public Response register(@Context HttpServletRequest request, String body) {
		try {
			String result = validateUserData(body);
			
			if(result != null) {
				return Response.ok(result).build();
			}
		} catch (Exception e) {
		    LOGGER.error("Error registering a new user", e);
		    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		
		return Response.status(Response.Status.FORBIDDEN).build();
	}
	
	private String validateUserData(String user) throws ClassNotFoundException, JSONException, SQLException {
		JSONObject userJSON = new JSONObject(user.toString());
		
		String email = userJSON.getString("email");
		
		if(!ValidateData.userEmail(email)) {
			return null;
		}
		
		CheckEmailAlreadyRegisteredDB checkEmailAlreadyRegisteredDB = new CheckEmailAlreadyRegisteredDB();
		String emailAlreadyExists = checkEmailAlreadyRegisteredDB.verify(email);
		if(emailAlreadyExists != null) {
			return emailAlreadyExists;
		}
		
		SaveUserDB saveUserDB = new SaveUserDB();
		
		String session = CreateToken.newToken(100);
		int userId = saveUserDB.insert("null", email, session);
		if(userId <= 0) {
			return null;
		}
		
		LOGGER.info("Email '" + email + "' registered in the database - user ID: " + userId);
		
		JSONObject json = new JSONObject();
        json.put("id_user", userId);
        json.put("token", session);
        
		return json.toString();
	}
}