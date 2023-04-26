package br.com.pucsp.tcc.authenticator.rest;

import java.sql.SQLException;

import javax.mail.MessagingException;
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
import br.com.pucsp.tcc.authenticator.exceptions.InvalidNameException;
import br.com.pucsp.tcc.authenticator.exceptions.InvalidTokenException;
import br.com.pucsp.tcc.authenticator.exceptions.UnregisteredUserException;
import br.com.pucsp.tcc.authenticator.user.UpdateUserNameDB;

@Path("/register-name")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RegisterName {
	private static final Logger LOGGER = LoggerFactory.getLogger(RegisterName.class);

	@POST
	public Response register(final @Context HttpServletRequest request, final String body) {
		JSONObject bodyJSON = new JSONObject(body);
		
		try {
			UpdateUserNameDB updateUserNameDB = new UpdateUserNameDB();
			boolean result = updateUserNameDB.update(bodyJSON);
			
			if(result) {
				return Response.ok().build();
			}
		}
		catch(JSONException e) {
			return buildErrorResponse("Invalid JSON payload", Response.Status.BAD_REQUEST);
		}
		catch(InvalidEmailException | InvalidTokenException | InvalidNameException | UnregisteredUserException | BusinessException e) {
			return buildErrorResponse(e.getMessage(), Response.Status.BAD_REQUEST);
		}
		catch(SQLException | DatabaseInsertException e) {
			return buildErrorResponse("An error occurred with the database", Response.Status.INTERNAL_SERVER_ERROR);
		}
		catch(MessagingException e) {
			return buildErrorResponse("An error occurred while sending email", Response.Status.INTERNAL_SERVER_ERROR);
		}
		catch(Exception e) {
			return buildErrorResponse("Unknown error", Response.Status.INTERNAL_SERVER_ERROR);
		}
		
		return buildErrorResponse("Unexpected error occurred while registering a new user", Response.Status.INTERNAL_SERVER_ERROR);
	}
	
	private Response buildErrorResponse(String message, Response.Status status) {
		String errorJson = new JSONObject().put("Error Message", message).toString();
		LOGGER.error(message);
		return Response.status(status).entity(errorJson).build();
	}
}