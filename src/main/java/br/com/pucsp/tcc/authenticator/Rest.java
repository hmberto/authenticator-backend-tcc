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

import br.com.pucsp.tcc.authenticator.token.SendTokenEmail;
import br.com.pucsp.tcc.authenticator.token.ValidateTokenEmail;
import br.com.pucsp.tcc.authenticator.user.SaveActiveCodesDB;
import br.com.pucsp.tcc.authenticator.user.SaveConfirmEmailDB;
import br.com.pucsp.tcc.authenticator.user.SaveUserDB;
import br.com.pucsp.tcc.authenticator.utils.CreateToken;
import br.com.pucsp.tcc.authenticator.utils.ValidateData;

@Produces("application/json")
@Consumes("application/json")
public class Rest {
	private static String name = Rest.class.getSimpleName();
	private static Logger log = Logger.getLogger(Rest.class.getName());
	
	@GET
	@Path("/")
	public Response getTest() {
		String test = "{\n"
				+ "    \"Hello\":\"World!\"\n"
				+ "}";
		
		return Response.ok(test).build();
	}
	
	@POST
	@Path("/auth/request/token")
	public Response emailToken(@Context HttpServletRequest request, String email, String link, String code) {
		log.entering(name, "emailToken");
		
		ValidateData validateEmail = new ValidateData();
		
		try {
			JSONObject userJSON = new JSONObject(email.toString());
			
			if(validateEmail.userEmail(userJSON.getString("email"))) {
				SendTokenEmail sendTokenEmail = new SendTokenEmail();
				boolean check = sendTokenEmail.send(userJSON);
				
				if(check) {
					return Response.ok().build();
				}	
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, e.toString());
		}
		
		log.exiting(name, "emailToken");
		return Response.status(Response.Status.FORBIDDEN).build();
	}
	
	@POST
	@Path("/auth/validate/token")
	public Response confirmEmailToken(@Context HttpServletRequest request, String email, String token, String approve) {
		log.entering(name, "confirmEmailToken");
		
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
			log.log(Level.SEVERE, e.toString());
		}
		
		log.exiting(name, "confirmEmailToken");
		return Response.status(Response.Status.FORBIDDEN).build();
	}
	
	@POST
	@Path("/user/register/email")
	public Response registerEmail(@Context HttpServletRequest request, String email) {
		log.entering(name, "registerEmail");
		
		ValidateData validateEmail = new ValidateData();
		SaveActiveCodesDB saveActiveCodesDB = new SaveActiveCodesDB();
		SaveConfirmEmailDB saveConfirmEmailDB = new SaveConfirmEmailDB();
		
		try {
			JSONObject userJSON = new JSONObject(email.toString());
			
			if(validateEmail.userEmail(userJSON.getString("email"))) {
				SaveUserDB saveUserDB = new SaveUserDB();
				long userId = saveUserDB.insertUser("null", userJSON.getString("email"));
				
				String token = CreateToken.newToken(6);
				
				saveActiveCodesDB.insertActiveCode(userId, userJSON.getString("email"), token);
				saveConfirmEmailDB.insertConfirmEmail(userId, userJSON.getString("email"), false);
				
				return Response.ok(userId).build();	
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, e.toString());
		}
		
		log.exiting(name, "registerEmail");
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