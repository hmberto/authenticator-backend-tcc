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

import br.com.pucsp.tcc.authenticator.user.UpdateUserNameDB;
import br.com.pucsp.tcc.authenticator.utils.ValidateData;

@Path("/user/register/name")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RegisterName {
	private static final Logger LOGGER = LoggerFactory.getLogger(RegisterName.class);
	
	@POST
	public Response register(@Context HttpServletRequest request, String body) {
		try {
			boolean result = validateUserEmail(body);
			
			if (result) {
				return Response.ok().build();
			}
		} catch (Exception e) {
			LOGGER.error("Error registering a new name for the user", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		
		return Response.status(Response.Status.FORBIDDEN).build();
	}
	
	private boolean validateUserEmail(String body) throws ClassNotFoundException, JSONException, SQLException {
		JSONObject userJSON = new JSONObject(body.toString());
		
		String email = userJSON.getString("email");
		String name = userJSON.getString("name");
        String session = userJSON.getString("session");
        
		if(!ValidateData.userEmail(email)) {
			return false;
		}
		
		UpdateUserNameDB updateUserNameDB = new UpdateUserNameDB();
		return updateUserNameDB.newName(name, email, session);
	}
}