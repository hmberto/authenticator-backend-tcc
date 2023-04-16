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

import br.com.pucsp.tcc.authenticator.exceptions.InvalidEmailException;
import br.com.pucsp.tcc.authenticator.exceptions.InvalidNameException;
import br.com.pucsp.tcc.authenticator.exceptions.InvalidSessionException;
import br.com.pucsp.tcc.authenticator.user.UpdateUserNameDB;
import br.com.pucsp.tcc.authenticator.utils.DataValidator;

@Path("/user/register/name")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RegisterName {
	private static final Logger LOGGER = LoggerFactory.getLogger(RegisterName.class);

	@POST
	public Response register(@Context HttpServletRequest request, String body) {
		try {
			boolean result = validateUserData(body);
			
			if(result) {
				return Response.ok().build();
			}
		} catch (InvalidNameException e) {
			String json = new JSONObject().put("Error Message", "Invalid name format").toString();
		    LOGGER.error("Error registering a new name for the user: Invalid name format", e);
		    return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
		} catch (InvalidEmailException e) {
			String json = new JSONObject().put("Error Message", "Invalid email format").toString();
		    LOGGER.error("Error registering a new name for the user: Invalid email format", e);
		    return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
		} catch (InvalidSessionException e) {
			String json = new JSONObject().put("Error Message", "Invalid session token").toString();
		    LOGGER.error("Error registering a new name for the user: Invalid session token", e);
		    return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
		} catch (JSONException | SQLException e) {
		    LOGGER.error("Error registering a new name for the user", e);
		}
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
	}
	
	private boolean validateUserData(String body) throws InvalidNameException, InvalidEmailException, InvalidSessionException, JSONException, SQLException {
		JSONObject userJSON = new JSONObject(body);
		
		String email = userJSON.getString("email").trim().toLowerCase();
		String name = userJSON.getString("name").trim().toLowerCase();
		String session = userJSON.getString("session");
		
		if(!DataValidator.isValidUsername(name)) {
			throw new InvalidNameException("Invalid name format");
		}
		
		if(!DataValidator.isValidEmail(email)) {
			throw new InvalidEmailException("Invalid email format");
		}
		
		if(!DataValidator.isValidToken(session)) {
			throw new InvalidSessionException("Invalid session token");
		}
		
		if(!name.matches("^[\\p{L}]+( [\\p{L}]+)+$")) {
			throw new InvalidNameException("Name must have two words");
		}
		
		UpdateUserNameDB updateUserNameDB = new UpdateUserNameDB();
		return updateUserNameDB.update(name, email, session);
	}
}