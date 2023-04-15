package br.com.pucsp.tcc.authenticator;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import br.com.pucsp.tcc.authenticator.rest.RegisterEmail;
import br.com.pucsp.tcc.authenticator.rest.RequestToken;
import br.com.pucsp.tcc.authenticator.token.ValidateTokenEmail;
import br.com.pucsp.tcc.authenticator.user.UpdateUserNameDB;
import br.com.pucsp.tcc.authenticator.utils.ValidateData;

@Produces("application/json")
@Consumes("application/json")
public class Rest {
	private static final String CLASS_NAME = Rest.class.getSimpleName();
	private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);
	
	@GET
	public Response getTest() {
		String res = new JSONObject().put("message", "Hello World!").toString();
		return Response.ok(res).build();
	}
	
	@POST
	@Path("/auth/request/token")
	public Response requestToken(@Context HttpServletRequest request, String email, String link, String code) {
		try {
			RequestToken requestToken = new RequestToken();
			String result = requestToken.request(email);
			
			if(result != null) {
				return Response.ok().build();
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.toString());
		}
		
		return Response.status(Response.Status.FORBIDDEN).build();
	}
	
	@POST
	@Path("/auth/validate/token")
	public Response confirmEmailToken(@Context HttpServletRequest request, String email, String token, String approve) {
		ValidateData validateData = new ValidateData();
		
		try {
			JSONObject userJSON = new JSONObject(email.toString());
			
			if(validateData.userEmail(userJSON.getString("email")) && validateData.userToken(userJSON.getString("token"))) {
				ValidateTokenEmail validateTokenEmail = new ValidateTokenEmail();
				boolean check = validateTokenEmail.verify(userJSON);
				
				if(check) {
					return Response.ok().build();
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.toString());
		}
		
		return Response.status(Response.Status.FORBIDDEN).build();
	}
	
	@POST
	@Path("/user/register/email")
	public Response registerEmail(@Context HttpServletRequest request, String email) {
		try {
			RegisterEmail registerEmail = new RegisterEmail();
			String result = registerEmail.newEmail(email);
			
			if(result != null) {
				return Response.ok(result).build();
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.toString());
		}
		
		return Response.status(Response.Status.FORBIDDEN).build();
	}
	
	@POST
	@Path("/user/register/name")
	public Response registerName(@Context HttpServletRequest request, String email, String name, String session) {
		ValidateData validateEmail = new ValidateData();
		UpdateUserNameDB updateUserNameDB = new UpdateUserNameDB();
		
		try {
			JSONObject userJSON = new JSONObject(email.toString());
			
			if(validateEmail.userEmail(userJSON.getString("email"))) {
				boolean updateName = updateUserNameDB.newName(userJSON.getString("name"), userJSON.getString("email"), userJSON.getString("session"));
				
				if(updateName) {
					return Response.ok().build();	
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.toString());
		}
		
		return Response.status(Response.Status.FORBIDDEN).build();
	}
	
	@OPTIONS
	@Path("{path : .*}")
	public Response options() {
	    return Response.ok("")
	            .header("Access-Control-Allow-Origin", "*")
	            .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
	            .header("Access-Control-Allow-Credentials", "true")
	            .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
	            .header("Access-Control-Max-Age", "1209600")
	            .build();
	}
}