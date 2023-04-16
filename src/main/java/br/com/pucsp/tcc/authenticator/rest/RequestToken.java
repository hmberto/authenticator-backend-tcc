package br.com.pucsp.tcc.authenticator.rest;

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

import br.com.pucsp.tcc.authenticator.token.SendTokenEmail;
import br.com.pucsp.tcc.authenticator.utils.ValidateData;

@Produces("application/json")
@Consumes("application/json")
public class RequestToken {
	private static final Logger LOGGER = LoggerFactory.getLogger(RegisterEmail.class);
	
	@POST
	@Path("/auth/request/token")
	public Response request(@Context HttpServletRequest request, String body) {
		try {
			RequestToken requestToken = new RequestToken();
			boolean result = requestToken.newToken(body);
			
			if(result) {
				return Response.ok().build();
			}
		} catch (Exception e) {
			LOGGER.error("Error requesting new token", e);
		}
		
		return Response.status(Response.Status.FORBIDDEN).build();
	}
	
	private boolean newToken(String user) {
		JSONObject userJSON = new JSONObject(user.toString());
		
		String email = userJSON.getString("email");
		String link = userJSON.getString("link");
		String code = userJSON.getString("code");
		
		ValidateData validateEmail = new ValidateData();
		if(!validateEmail.userEmail(email)) {
			return false;
		}
		
		SendTokenEmail sendTokenEmail = new SendTokenEmail();
		boolean isTokenSent = sendTokenEmail.send(email, link, code);
		
		if(!isTokenSent) {
			return false;
		}
		
		return true;
	}
}