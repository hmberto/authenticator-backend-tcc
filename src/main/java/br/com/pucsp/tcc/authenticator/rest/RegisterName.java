package br.com.pucsp.tcc.authenticator.rest;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.pucsp.tcc.authenticator.user.UpdateUserNameDB;
import br.com.pucsp.tcc.authenticator.utils.ValidateData;

@Produces("application/json")
@Consumes("application/json")
public class RegisterName {
	private static final String CLASS_NAME = RegisterName.class.getSimpleName();
	private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);
	
	@POST
	@Path("/user/register/name")
	public Response register(@Context HttpServletRequest request, String body) {
		try {
			RegisterName registerName = new RegisterName();
			boolean result = registerName.newName(body);
			
			if(result) {
				return Response.ok(result).build();
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		
		return Response.status(Response.Status.FORBIDDEN).build();
	}
	
	private boolean newName(String body) throws ClassNotFoundException, JSONException, SQLException {
		ValidateData validateEmail = new ValidateData();
		JSONObject userJSON = new JSONObject(body.toString());
		
		if(validateEmail.userEmail(userJSON.getString("email"))) {
			UpdateUserNameDB updateUserNameDB = new UpdateUserNameDB();
			return updateUserNameDB.newName(userJSON.getString("name"), userJSON.getString("email"), userJSON.getString("session"));
		}
		
		return false;
	}
}