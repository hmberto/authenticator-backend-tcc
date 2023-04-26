package br.com.pucsp.tcc.authenticator;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.com.pucsp.tcc.authenticator.impl.AuthorizeAccessLinkService;
import br.com.pucsp.tcc.authenticator.impl.CheckActiveSessionService;
import br.com.pucsp.tcc.authenticator.impl.LogoutService;
import br.com.pucsp.tcc.authenticator.impl.RegisterEmailService;
import br.com.pucsp.tcc.authenticator.impl.RegisterNameService;
import br.com.pucsp.tcc.authenticator.impl.TestService;
import br.com.pucsp.tcc.authenticator.impl.ValidateOtpService;
import br.com.pucsp.tcc.authenticator.utils.config.cors.CORSFilter;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class APIController {
	private final TestService testService = new TestService();
	private final RegisterEmailService registerEmailService = new RegisterEmailService();
	private final RegisterNameService registerNameService = new RegisterNameService();
	private final ValidateOtpService validateOtpService = new ValidateOtpService();
	private final AuthorizeAccessLinkService authorizeAccessLinkService = new AuthorizeAccessLinkService();
	private final CheckActiveSessionService checkActiveSessionService = new CheckActiveSessionService();
	private final LogoutService logoutService = new LogoutService();
	private final CORSFilter CORSFilter = new CORSFilter();
	
	@GET
	public Response test(@Context HttpServletRequest request) {
		return testService.validateData(request);
	}
	
	@POST
	@Path("/register-email")
	public Response registerEmail(@Context HttpServletRequest request, String body) {
		return registerEmailService.validateData(request, body);
	}
	
	@POST
	@Path("/register-name")
	public Response registerName(@Context HttpServletRequest request, String body) {
		return registerNameService.validateData(request, body);
	}
	
	@POST
	@Path("/validate-otp")
	public Response validateOtp(@Context HttpServletRequest request, String body) {
		return validateOtpService.validateData(request, body);
	}
	
	@POST
	@Path("/authorize-access-link")
	public Response authorizeAccessLink(@Context HttpServletRequest request, String body) {
		return authorizeAccessLinkService.validateData(request, body);
	}
	
	@POST
	@Path("/check-active-session")
	public Response checkActiveSession(@Context HttpServletRequest request, String body) {
		return checkActiveSessionService.validateData(request, body);
	}
	
	@POST
	@Path("/logout")
	public Response logout(@Context HttpServletRequest request, String body) {
		return logoutService.validateData(request, body);
	}
	
	@OPTIONS
	@Path("{path : .*}")
	public Response options() {
	    return CORSFilter.options();
	}
}