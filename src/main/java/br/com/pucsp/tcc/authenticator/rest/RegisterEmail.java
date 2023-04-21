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
import br.com.pucsp.tcc.authenticator.user.CheckEmailAlreadyRegisteredDB;
import br.com.pucsp.tcc.authenticator.user.SaveActiveSessionsDB;
import br.com.pucsp.tcc.authenticator.user.SaveUserDB;
import br.com.pucsp.tcc.authenticator.utils.CreateToken;
import br.com.pucsp.tcc.authenticator.utils.DataValidator;

@Path("/user/register/email")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RegisterEmail {
	private static final Logger LOGGER = LoggerFactory.getLogger(RegisterEmail.class);
	
	@POST
	public Response register(@Context HttpServletRequest request, String body) {
		try {
            JSONObject userJSON = new JSONObject(body);
            String userEmail = userJSON.getString("email").trim().toLowerCase();
            String newUserSessionToken = CreateToken.generate("session");
            
            if(!DataValidator.isValidEmail(userEmail)) {
            	String json = new JSONObject().put("Error Message", "Invalid email format").toString();
            	return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
            }
            
            @SuppressWarnings("resource")
			CheckEmailAlreadyRegisteredDB checkEmailAlreadyRegisteredDB = new CheckEmailAlreadyRegisteredDB();
            String emailAlreadyExists = checkEmailAlreadyRegisteredDB.verify(userEmail);
            if(emailAlreadyExists != null) {
                JSONObject userExistsJSON = new JSONObject(emailAlreadyExists);
                LOGGER.info("Email '{}' already registered in the database - user ID: {}", userEmail, userExistsJSON.getInt("userId"));
                
                boolean isSessionTokenActive = Boolean.parseBoolean(userExistsJSON.getString("isSessionTokenActive").trim().toLowerCase());
                
                if(isSessionTokenActive) {
                	return Response.ok(emailAlreadyExists).build();
                }
                else {
                	int userId = userExistsJSON.getInt("userId");
    	            String name = userExistsJSON.getString("name");
    	            
    	            String isLoginOrRegistration = "true";
    	            if(name == "null") {
    	            	isLoginOrRegistration = "false";
    	            }
    	            
    	            String json = new JSONObject()
	            		.put("userId", userId)
	    	            .put("session", newUserSessionToken)
	    	            .put("isSessionTokenActive", "true")
	    	            .put("isLogin", isLoginOrRegistration)
	    	            .toString();
                	
                	@SuppressWarnings("resource")
					SaveActiveSessionsDB saveActiveSessionsDB = new SaveActiveSessionsDB();
                	int isSaved = saveActiveSessionsDB.insertActiveSession(userId, userEmail, newUserSessionToken, false);
                	if(isSaved >= 1) {
                		return Response.ok(json).build();
                	}
                }
            }
            
            @SuppressWarnings("resource")
			SaveUserDB saveUserDB = new SaveUserDB();
            int userId = saveUserDB.insert("null", userEmail, newUserSessionToken);
            
            if(userId <= 0) {
                throw new SQLException("User registration failed for email: " + userEmail);
            }
            
            LOGGER.info("Email '{}' registered in the database - user ID: {}", userEmail, userId);
            JSONObject json = new JSONObject()
                    .put("userId", userId)
                    .put("session", newUserSessionToken)
                    .put("isSessionTokenActive", "true")
                    .put("isLogin", "false");
            
            return Response.ok(json.toString()).build();
        } catch (InvalidEmailException e) {
        	String json = new JSONObject().put("Error Message", e.getMessage()).toString();
            LOGGER.error("Invalid email format", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
        } catch (SQLException e) {
        	String json = new JSONObject().put("Error Message", "Error registering a new user").toString();
            LOGGER.error("Error registering a new user", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(json).build();
        } catch (Exception e) {
        	String json = new JSONObject().put("Error Message", "Unexpected error occurred while registering a new user").toString();
            LOGGER.error("Unexpected error occurred while registering a new user", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(json).build();
        }
    }
}