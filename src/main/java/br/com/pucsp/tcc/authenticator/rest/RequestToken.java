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

import br.com.pucsp.tcc.authenticator.exceptions.BusinessException;
import br.com.pucsp.tcc.authenticator.exceptions.InvalidEmailException;
import br.com.pucsp.tcc.authenticator.token.EmailTokenSender;
import br.com.pucsp.tcc.authenticator.utils.DataValidator;

@Path("/auth/request/token")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RequestToken {
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestToken.class);
	
    @POST
    public Response request(@Context HttpServletRequest request, String body) {
        try {
            String result = validateUserData(request, body).toString();
            
            if(result != null) {
                return Response.ok(result).build();
            }
        } catch (InvalidEmailException e) {
			String json = new JSONObject().put("Error Message", e.getMessage()).toString();
		    LOGGER.error("Error registering a new name for the user: Invalid email format", e);
		    return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
		} catch (BusinessException e) {
			String json = new JSONObject().put("Error Message", e.getMessage()).toString();
		    LOGGER.error("Error registering a new user: Invalid choice for link or code", e);
		    return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
		} catch (SQLException e) {
        	String json = new JSONObject().put("Error Message", "Error registering a new user").toString();
            LOGGER.error("Error registering a new token", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(json).build();
        } catch (Exception e) {
            LOGGER.error("Error requesting new token", e);
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    private JSONObject validateUserData(@Context HttpServletRequest request, String body) throws Exception {
    	JSONObject userJSON = new JSONObject(body.toString());
    	
    	String email = userJSON.getString("email").trim().toLowerCase();
    	boolean isSelectedLink = Boolean.parseBoolean(userJSON.getString("link").trim().toLowerCase());
		boolean isSelectedCode = Boolean.parseBoolean(userJSON.getString("code").trim().toLowerCase());
		
        if (!DataValidator.isValidEmail(email)) {
        	throw new InvalidEmailException("Invalid email format");
        }
        
        EmailTokenSender sendTokenEmail = new EmailTokenSender();
        return sendTokenEmail.send(email, isSelectedLink, isSelectedCode);
    }

}