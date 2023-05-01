package br.com.pucsp.tcc.authenticator.rest.error;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.utils.ErrorResponse;

public class NotFoundServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(NotFoundServlet.class);

	private ServletContextHandler[] handlers;

	public NotFoundServlet(ServletContextHandler... handlers) {
		this.handlers = handlers;
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getServletPath();
		boolean found = false;

		for (ServletContextHandler handler : handlers) {
			String contextPath = handler.getContextPath();
			if (path.startsWith(contextPath)) {
				String servletPath = path.substring(contextPath.length());
				ServletHolder holder = handler.getServletHandler().getServlet(servletPath);
				if (holder != null) {
					found = true;
					break;
				}
			}
		}

		if (!found) {
			String message = String.format("Invalid request path '%s'", req.getRequestURI());
			
			ErrorResponse.build(resp, LOGGER, message, HttpServletResponse.SC_NOT_FOUND);
		}
	}
}