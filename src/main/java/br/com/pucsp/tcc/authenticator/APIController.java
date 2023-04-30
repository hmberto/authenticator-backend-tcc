package br.com.pucsp.tcc.authenticator;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import br.com.pucsp.tcc.authenticator.rest.TestService;
import br.com.pucsp.tcc.authenticator.rest.RegisterEmailService;
import br.com.pucsp.tcc.authenticator.rest.CheckAccessLinkService;
import br.com.pucsp.tcc.authenticator.rest.CheckSessionService;
import br.com.pucsp.tcc.authenticator.rest.GetUserDataService;
import br.com.pucsp.tcc.authenticator.rest.LogoutService;
import br.com.pucsp.tcc.authenticator.rest.RegisterNameService;
import br.com.pucsp.tcc.authenticator.rest.ValidateAccessLinkService;
import br.com.pucsp.tcc.authenticator.rest.ValidateOtpService;

public class APIController {
	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);

		ServletContextHandler context = new ServletContextHandler();
		context.setContextPath("/api");
		
		context.addServlet(new ServletHolder(new TestService()), "/test");
		context.addServlet(new ServletHolder(new CheckAccessLinkService()), "/check/access-link/{emailToken}");
		context.addServlet(new ServletHolder(new CheckSessionService()), "/check/session");
		context.addServlet(new ServletHolder(new GetUserDataService()), "/users/{userEmail}");
		context.addServlet(new ServletHolder(new LogoutService()), "/logout");
		context.addServlet(new ServletHolder(new RegisterEmailService()), "/register/email");
		context.addServlet(new ServletHolder(new RegisterNameService()), "/register/name");
		context.addServlet(new ServletHolder(new ValidateAccessLinkService()), "/ValidateAccessLinkService");
		context.addServlet(new ServletHolder(new ValidateOtpService()), "/validate/otp");

		server.setHandler(context);
		server.start();
		server.join();
	}
}