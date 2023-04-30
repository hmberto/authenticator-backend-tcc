package br.com.pucsp.tcc.authenticator;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import br.com.pucsp.tcc.authenticator.rest.TestService;

public class APIController {
	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);

		ServletContextHandler context = new ServletContextHandler();
		context.setContextPath("/");
		
		context.addServlet(new ServletHolder(new TestService()), "/test");
		context.addServlet(new ServletHolder(new TestService()), "/register/email");

		server.setHandler(context);
		server.start();
		server.join();
	}
}