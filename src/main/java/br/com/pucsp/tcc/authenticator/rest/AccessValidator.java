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
import br.com.pucsp.tcc.authenticator.token.EmailSessionTokenOrOTPValidator;
import br.com.pucsp.tcc.authenticator.utils.DataValidator;

@Path("/access-validator")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccessValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(AccessValidator.class);
	
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
		
		String userEmail = bodyJSON.getString("email").trim().toLowerCase();
		String userSessionTokenOrOTP = bodyJSON.getString("sessionTokenOrOTP").trim().toUpperCase();
		boolean isSelectedApprove = Boolean.parseBoolean(bodyJSON.getString("approve").trim().toLowerCase());
		
		if(!DataValidator.isValidEmail(userEmail)) {
			throw new InvalidEmailException("Invalid email format");
		}
		
		if(!DataValidator.isValidToken(userSessionTokenOrOTP)) {
			throw new InvalidSessionException("Invalid session token or OTP format");
		}
		
		EmailSessionTokenOrOTPValidator validateTokenEmail = new EmailSessionTokenOrOTPValidator();
		return validateTokenEmail.verify(userSessionTokenOrOTP, userEmail, isSelectedApprove);
	}
}