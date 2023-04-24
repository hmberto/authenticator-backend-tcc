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
import br.com.pucsp.tcc.authenticator.rest.LogoutUser;
import br.com.pucsp.tcc.authenticator.rest.RegisterName;
import br.com.pucsp.tcc.authenticator.rest.AccessRequester;
import br.com.pucsp.tcc.authenticator.rest.AccessValidator;

@Produces("application/json")
@Consumes("application/json")
public class RestAPI {
	private final TestAPI api = new TestAPI();
	private final AccessRequester newSessionTokenOrOTP = new AccessRequester();
	private final AccessValidator sessionTokenOrOTP = new AccessValidator();
	private final RegisterName newName = new RegisterName();
	private final LogoutUser logoutUser = new LogoutUser();
	private final CORSFilter CORSFilter = new CORSFilter();
	
	@GET
	public Response getTest() {
		return api.test();
	}
	
	@POST
	@Path("/access-requester")
	public Response requester(@Context HttpServletRequest request, String body) {
		return newSessionTokenOrOTP.request(request, body);
	}
	
	@POST
	@Path("/access-validator")
	public Response validator(@Context HttpServletRequest request, String body) {
		return sessionTokenOrOTP.validate(request, body);
	}
	
	@POST
	@Path("/register-name")
	public Response registerName(@Context HttpServletRequest request, String body) {
		return newName.register(request, body);
	}
	
	@POST
	@Path("/logout")
	public Response logout(@Context HttpServletRequest request, String body) {
		return logoutUser.logout(request, body);
	}
	
	@OPTIONS
	@Path("{path : .*}")
	public Response options() {
	    return CORSFilter.options();
	}
}