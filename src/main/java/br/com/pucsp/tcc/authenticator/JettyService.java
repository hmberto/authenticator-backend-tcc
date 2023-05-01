package br.com.pucsp.tcc.authenticator;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import br.com.pucsp.tcc.authenticator.rest.CheckAccessLinkService;
import br.com.pucsp.tcc.authenticator.rest.CheckSessionService;
import br.com.pucsp.tcc.authenticator.rest.GetUserDataService;
import br.com.pucsp.tcc.authenticator.rest.LogoutService;
import br.com.pucsp.tcc.authenticator.rest.RegisterEmailService;
import br.com.pucsp.tcc.authenticator.rest.RegisterNameService;
import br.com.pucsp.tcc.authenticator.rest.TestService;
import br.com.pucsp.tcc.authenticator.rest.ValidateAccessLinkService;
import br.com.pucsp.tcc.authenticator.rest.ValidateOtpService;
import br.com.pucsp.tcc.authenticator.rest.error.NotFoundServlet;
import br.com.pucsp.tcc.authenticator.rest.filter.BasicAuthFilter;
import br.com.pucsp.tcc.authenticator.rest.filter.MethodNotAllowedFilter;

public class JettyService {
	public static void start() throws Exception {
		Server server = new Server(8080);

		FilterHolder basicAuthFilter = new FilterHolder(new BasicAuthFilter());
		FilterHolder methodNotAllowedFilterHolder = new FilterHolder(new MethodNotAllowedFilter());

		ServletContextHandler rootContext = new ServletContextHandler();
		ServletContextHandler apiContext = new ServletContextHandler();

		rootContext.setContextPath("/");
		apiContext.setContextPath("/api");

		apiContext.addFilter(basicAuthFilter, "/*", EnumSet.of(DispatcherType.REQUEST));

		rootContext.addServlet(new ServletHolder(new NotFoundServlet(rootContext)), "/*");
		apiContext.addServlet(new ServletHolder(new NotFoundServlet(apiContext)), "/*");

		rootContext.addServlet(new ServletHolder(new TestService()), "/test");
		rootContext.addFilter(methodNotAllowedFilterHolder, "/test", EnumSet.of(DispatcherType.REQUEST));

		apiContext.addServlet(new ServletHolder(new TestService()), "/test");
		apiContext.addFilter(methodNotAllowedFilterHolder, "/test", EnumSet.of(DispatcherType.REQUEST));

		apiContext.addServlet(new ServletHolder(new RegisterEmailService()), "/register/email");
		apiContext.addFilter(methodNotAllowedFilterHolder, "/register/email", EnumSet.of(DispatcherType.REQUEST));

		apiContext.addServlet(new ServletHolder(new RegisterNameService()), "/register/name");
		apiContext.addFilter(methodNotAllowedFilterHolder, "/register/name", EnumSet.of(DispatcherType.REQUEST));

		apiContext.addServlet(new ServletHolder(new ValidateOtpService()), "/validate/otp");
		apiContext.addFilter(methodNotAllowedFilterHolder, "/validate/otp", EnumSet.of(DispatcherType.REQUEST));

		apiContext.addServlet(new ServletHolder(new ValidateAccessLinkService()), "/validate/access-link");
		apiContext.addFilter(methodNotAllowedFilterHolder, "/validate/access-link", EnumSet.of(DispatcherType.REQUEST));

		apiContext.addServlet(new ServletHolder(new CheckAccessLinkService()), "/check/access-link/*");
		apiContext.addFilter(methodNotAllowedFilterHolder, "/check/access-link/*", EnumSet.of(DispatcherType.REQUEST));

		apiContext.addServlet(new ServletHolder(new CheckSessionService()), "/check/session");
		apiContext.addFilter(methodNotAllowedFilterHolder, "/check/session", EnumSet.of(DispatcherType.REQUEST));

		apiContext.addServlet(new ServletHolder(new GetUserDataService()), "/users/*");
		apiContext.addFilter(methodNotAllowedFilterHolder, "/users/*", EnumSet.of(DispatcherType.REQUEST));

		apiContext.addServlet(new ServletHolder(new LogoutService()), "/logout");
		apiContext.addFilter(methodNotAllowedFilterHolder, "/logout", EnumSet.of(DispatcherType.REQUEST));

		ContextHandlerCollection contexts = new ContextHandlerCollection();
		contexts.setHandlers(new Handler[] { apiContext, rootContext });
		server.setHandler(contexts);

		server.start();
		server.join();
	}
}