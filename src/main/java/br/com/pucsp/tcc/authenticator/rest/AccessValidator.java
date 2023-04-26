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

import br.com.pucsp.tcc.authenticator.exceptions.BusinessException;
import br.com.pucsp.tcc.authenticator.exceptions.DatabaseInsertException;
import br.com.pucsp.tcc.authenticator.exceptions.InvalidEmailException;
import br.com.pucsp.tcc.authenticator.exceptions.InvalidTokenException;
import br.com.pucsp.tcc.authenticator.exceptions.UnregisteredUserException;
import br.com.pucsp.tcc.authenticator.token.EmailTokenValidator;
import br.com.pucsp.tcc.authenticator.utils.DateTime;

@Path("/access-validator")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccessValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(AccessValidator.class);
	
	@POST
	public Response validateAccess(final @Context HttpServletRequest request, final String body) {
		JSONObject bodyJSON = new JSONObject(body);
		
		String loginDate = DateTime.date();
    	String userIP = request.getRemoteAddr();
		
		try {
			EmailTokenValidator emailTokenValidator = new EmailTokenValidator();
			boolean resp = emailTokenValidator.verify(bodyJSON, userIP, loginDate);
			
			if(resp) {
				return Response.ok().build();
			}
		}
		catch(JSONException e) {
			return buildErrorResponse("Invalid JSON payload", Response.Status.BAD_REQUEST);
		}
		catch(InvalidEmailException | UnregisteredUserException | BusinessException | InvalidTokenException e) {
			return buildErrorResponse(e.getMessage(), Response.Status.BAD_REQUEST);
		}
		catch(SQLException | DatabaseInsertException e) {
			return buildErrorResponse("Unexpected error occurred while registering a new user", Response.Status.INTERNAL_SERVER_ERROR);
		}
		catch(Exception e) {
			return buildErrorResponse("Unexpected error occurred while registering a new user", Response.Status.INTERNAL_SERVER_ERROR);
		}
		
		return buildErrorResponse("Unexpected error occurred while registering a new user", Response.Status.INTERNAL_SERVER_ERROR);
	}
	
	private Response buildErrorResponse(String message, Response.Status status) {
		String errorJson = new JSONObject().put("Error Message", message).toString();
		LOGGER.error(message);
		return Response.status(status).entity(errorJson).build();
	}
}
