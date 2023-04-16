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

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.exceptions.InvalidEmailException;
import br.com.pucsp.tcc.authenticator.exceptions.InvalidSessionException;
import br.com.pucsp.tcc.authenticator.token.EmailTokenValidator;
import br.com.pucsp.tcc.authenticator.utils.DataValidator;

@Path("/auth/validate/token")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ValidateToken {
	private static final Logger LOGGER = LoggerFactory.getLogger(ValidateToken.class);
	
	@POST
	public Response validate(@Context HttpServletRequest request, String body) {
		try {
			boolean result = validateUserData(body);
			
			if(result) {
				return Response.ok().build();
			}
		} catch (InvalidEmailException e) {
			String json = new JSONObject().put("Error Message", e.getMessage()).toString();
		    LOGGER.error("Error registering a new name for the user: Invalid email format", e);
		    return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
		} catch (InvalidSessionException e) {
			String json = new JSONObject().put("Error Message", e.getMessage()).toString();
		    LOGGER.error("Error registering a new name for the user: Invalid session token", e);
		    return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
		} catch (Exception e) {
			LOGGER.error("Error validating token", e);
		}
		
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
	}
	
	private boolean validateUserData(String body) throws SQLException {
		JSONObject bodyJSON = new JSONObject(body.toString());
		
		String token = bodyJSON.getString("token").trim().toUpperCase();
		String email = bodyJSON.getString("email").trim().toLowerCase();
		boolean isSelectedApprove = Boolean.parseBoolean(bodyJSON.getString("approve").trim().toLowerCase());
		
		if(!DataValidator.isValidEmail(email)) {
			throw new InvalidEmailException("Invalid email format");
		}
		
		if(!DataValidator.isValidToken(token)) {
			throw new InvalidSessionException("Invalid session token");
		}
		
		EmailTokenValidator validateTokenEmail = new EmailTokenValidator();
		return validateTokenEmail.verify(token, email, isSelectedApprove);
	}
}
