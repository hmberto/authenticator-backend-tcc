package br.com.pucsp.tcc.authenticator.rest.filter;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Base64;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.utils.ErrorResponse;
import br.com.pucsp.tcc.authenticator.utils.system.SystemDefaultVariables;

public class BasicAuthFilter implements Filter {
	private static final Logger LOGGER = LoggerFactory.getLogger(BasicAuthFilter.class);

	private static final String API_USER = SystemDefaultVariables.apiUser;
	private static final String API_PASS = SystemDefaultVariables.apiPass;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		String httpMethod = httpRequest.getMethod();
		if (httpMethod.equals(HttpMethod.OPTIONS)) {
			chain.doFilter(request, response);
			return;
		}

		String authHeader = httpRequest.getHeader("Authorization");
		if (authHeader == null) {
			authHeader = httpRequest.getHeader("authorization");
		}

		if (authHeader == null || !authHeader.startsWith("Basic ")) {
			httpResponse.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"Secure Area\"");

			String message = String.format("Request at '%s' refused  because it was not authenticated",
					httpRequest.getRequestURI());

			ErrorResponse.build(httpResponse, LOGGER, message, HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		String encodedCredentials = authHeader.substring("Basic ".length()).trim();
		byte[] decodedBytes = Base64.getDecoder().decode(encodedCredentials);
		String credentials = new String(decodedBytes, "UTF-8");

		if (!credentials.equals(API_USER + ":" + API_PASS)) {
			String message = String.format("Request at '%s' refused due to incorrect authentication credentials",
					httpRequest.getRequestURI());

			ErrorResponse.build(httpResponse, LOGGER, message, HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}

	@Override
	public void destroy() {}
}