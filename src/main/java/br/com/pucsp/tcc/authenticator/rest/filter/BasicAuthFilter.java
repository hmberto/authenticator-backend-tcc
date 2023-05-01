package br.com.pucsp.tcc.authenticator.rest.filter;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Base64;
import javax.ws.rs.core.HttpHeaders;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicAuthFilter implements Filter {
	private static final Logger LOGGER = LoggerFactory.getLogger(BasicAuthFilter.class);

	private static final String API_USER = System.getenv("API_USER");
	private static final String API_PASS = System.getenv("API_PASS");

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		String authHeader = httpRequest.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Basic ")) {
			httpResponse.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"Secure Area\"");
			httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

			String message = String.format("Request at '%s' refused  because it was not authenticated",
					httpRequest.getRequestURI());
			LOGGER.warn(message);

			String res = new JSONObject().put("Message", message).toString();
			httpResponse.getWriter().write(res);

			return;
		}

		String encodedCredentials = authHeader.substring("Basic ".length()).trim();
		byte[] decodedBytes = Base64.getDecoder().decode(encodedCredentials);
		String credentials = new String(decodedBytes, "UTF-8");

		if (!credentials.equals(API_USER + ":" + API_PASS)) {
			httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);

			String message = String.format("Request at '%s' refused due to incorrect authentication credentials",
					httpRequest.getRequestURI());
			LOGGER.warn(message);

			String res = new JSONObject().put("Message", message).toString();
			httpResponse.getWriter().write(res);

			return;
		}

		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}
}