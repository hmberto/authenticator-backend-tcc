package br.com.pucsp.tcc.authenticator.rest;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.token.ValidateTokenEmail;
import br.com.pucsp.tcc.authenticator.utils.ValidateData;

@Produces("application/json")
@Consumes("application/json")
public class ValidateToken {
	private static final Logger LOGGER = LoggerFactory.getLogger(RegisterEmail.class);
	
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
			LOGGER.error("Error validating token", e);
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
