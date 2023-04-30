package br.com.pucsp.tcc.authenticator.rest.error;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		Pattern pattern = Pattern.compile("/api/check/access-link/\\w+");
		Matcher matcher = pattern.matcher(path);

		boolean found = false;

		if (matcher.matches()) {
			found = true;
		} else {
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
		}

		if (!found) {
			LOGGER.warn("Received request for an unknown path '{}'", req.getRequestURI());
			resp.setContentType(MediaType.APPLICATION_JSON);
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);

			String res = new JSONObject().put("Message", "Invalid path " + req.getRequestURI()).toString();

			resp.getWriter().write(res.toString());
		}
	}
}