package br.com.pucsp.tcc.authenticator;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import br.com.pucsp.tcc.authenticator.rest.TestAPI;
import br.com.pucsp.tcc.authenticator.cors.CORSFilter;
import br.com.pucsp.tcc.authenticator.rest.RegisterEmail;
import br.com.pucsp.tcc.authenticator.rest.RegisterName;
import br.com.pucsp.tcc.authenticator.rest.RequestToken;
import br.com.pucsp.tcc.authenticator.rest.ValidateToken;

@Produces("application/json")
@Consumes("application/json")
public class RestAPI {
	private final TestAPI testAPI = new TestAPI();
	private final RequestToken requestToken = new RequestToken();
	private final ValidateToken validateToken = new ValidateToken();
	private final RegisterEmail registerEmail = new RegisterEmail();
	private final RegisterName registerName = new RegisterName();
	private final CORSFilter CORSFilter = new CORSFilter();
	
	@GET
	public Response getTest() {
		return testAPI.test();
	}
	
	@POST
	@Path("/auth/request/token")
	public Response requestNewToken(@Context HttpServletRequest request, String body) {
		return requestToken.request(request, body);
	}
	
	@POST
	@Path("/auth/validate/token")
	public Response validateEmailToken(@Context HttpServletRequest request, String body) {
		return validateToken.validate(request, body);
	}
	
	@POST
	@Path("/user/register/email")
	public Response registerNewEmail(@Context HttpServletRequest request, String body) {
		return registerEmail.register(request, body);
	}
	
	@POST
	@Path("/user/register/name")
	public Response registerNewName(@Context HttpServletRequest request, String body) {
		return registerName.register(request, body);
	}
	
	@OPTIONS
	@Path("{path : .*}")
	public Response options() {
	    return CORSFilter.options();
	}
}