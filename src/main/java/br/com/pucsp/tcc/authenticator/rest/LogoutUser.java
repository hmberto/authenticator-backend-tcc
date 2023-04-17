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
import br.com.pucsp.tcc.authenticator.exceptions.InvalidSessionException;
import br.com.pucsp.tcc.authenticator.user.LogoutUserDB;
import br.com.pucsp.tcc.authenticator.utils.DataValidator;

@Path("/user/logout")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LogoutUser {
private static final Logger LOGGER = LoggerFactory.getLogger(LogoutUser.class);
	
	@POST
	public Response logout(@Context HttpServletRequest request, String body) {
		try {
			boolean result = validateUserData(body);
			
			if(result) {
				return Response.ok().build();
			}
		} catch (InvalidEmailException e) {
			String json = new JSONObject().put("Error Message", e.getMessage()).toString();
		    LOGGER.error("Error logging out user: Invalid email format", e);
		    return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
		} catch (InvalidSessionException e) {
			String json = new JSONObject().put("Error Message", e.getMessage()).toString();
		    LOGGER.error("Error logging out user: Invalid session token", e);
		    return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
		} catch (JSONException | SQLException e) {
		    LOGGER.error("Error logging out user", e);
		}
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
	}
	
	private boolean validateUserData(String body) throws InvalidEmailException, InvalidSessionException, JSONException, SQLException {
		JSONObject userJSON = new JSONObject(body);
		
		String email = userJSON.getString("email").trim().toLowerCase();
		String session = userJSON.getString("session").trim().toUpperCase();
		boolean isSelectedKillAll = Boolean.parseBoolean(userJSON.getString("kill_all").trim().toLowerCase());
		
		if(!DataValidator.isValidEmail(email)) {
			throw new InvalidEmailException("Invalid email format");
		}
		
		if(!DataValidator.isValidToken(session) || session.length() != 100) {
			throw new InvalidSessionException("Invalid session token");
		}
		
		LogoutUserDB logoutUserDB = new LogoutUserDB();
		return logoutUserDB.logout(email, session, isSelectedKillAll);
	}
}