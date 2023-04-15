package br.com.pucsp.tcc.authenticator.rest;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import br.com.pucsp.tcc.authenticator.token.ValidateTokenEmail;
import br.com.pucsp.tcc.authenticator.utils.ValidateData;

@Produces("application/json")
@Consumes("application/json")
public class ValidateToken {
	private static final String CLASS_NAME = ValidateToken.class.getSimpleName();
	private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);
	
	@POST
	@Path("/auth/validate/token")
	public Response validate(@Context HttpServletRequest request, String body) {
		try {
			ValidateToken validateToken = new ValidateToken();
			boolean result = validateToken.check(body);
			
			if(result) {
				return Response.ok().build();
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		
		return Response.status(Response.Status.FORBIDDEN).build();
	}
	
	private boolean check(String body) throws ClassNotFoundException, SQLException {
		ValidateData validateData = new ValidateData();
		JSONObject bodyJSON = new JSONObject(body.toString());
		
		if(validateData.userEmail(bodyJSON.getString("email")) && 
				validateData.userToken(bodyJSON.getString("token"))) {
			
			ValidateTokenEmail validateTokenEmail = new ValidateTokenEmail();
			return validateTokenEmail.verify(bodyJSON);
		}
		
		return false;
	}
}
