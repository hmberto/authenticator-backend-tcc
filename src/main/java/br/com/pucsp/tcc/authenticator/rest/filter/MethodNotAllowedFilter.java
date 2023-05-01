package br.com.pucsp.tcc.authenticator.rest.filter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.utils.ErrorResponse;

public class MethodNotAllowedFilter implements Filter {
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodNotAllowedFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		if (!(req instanceof HttpServletRequest)) {
			chain.doFilter(req, resp);
			return;
		}

		HttpServletRequest httpRequest = (HttpServletRequest) req;
		HttpServletResponse httpResponse = (HttpServletResponse) resp;
		String httpMethod = httpRequest.getMethod();

		if (!isHttpMethodAllowed(httpMethod, httpRequest.getRequestURI())) {
			String message = String.format("HTTP method %s is not supported by this URL %s", httpMethod,
					httpRequest.getRequestURI());

			ErrorResponse.build(httpResponse, LOGGER, message, HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return;
		}

		chain.doFilter(req, resp);
	}

	private boolean isHttpMethodAllowed(String httpMethod, String requestURI) {
		Pattern pattern1 = Pattern.compile("/api/check/access-link/\\w+");
		Matcher matcher1 = pattern1.matcher(requestURI);

		Pattern pattern2 = Pattern.compile("/api/users/\\S+");
		Matcher matcher2 = pattern2.matcher(requestURI);

		if (requestURI.equals("/test") && httpMethod.equals(HttpMethod.GET)) {
			return true;
		}
		if (requestURI.equals("/api/test") && httpMethod.equals(HttpMethod.GET)) {
			return true;
		}
		if (matcher1.matches() && httpMethod.equals(HttpMethod.GET)) {
			return true;
		}
		if (requestURI.equals("/api/check/session") && httpMethod.equals(HttpMethod.POST)) {
			return true;
		}
		if (matcher2.matches() && httpMethod.equals(HttpMethod.GET)) {
			return true;
		}
		if (requestURI.equals("/api/logout") && httpMethod.equals(HttpMethod.POST)) {
			return true;
		}
		if (requestURI.equals("/api/register/email") && httpMethod.equals(HttpMethod.POST)) {
			return true;
		}
		if (requestURI.equals("/api/register/name") && httpMethod.equals(HttpMethod.PUT)) {
			return true;
		}
		if (requestURI.equals("/api/validate/access-link") && httpMethod.equals(HttpMethod.POST)) {
			return true;
		}
		if (requestURI.equals("/api/validate/otp") && httpMethod.equals(HttpMethod.POST)) {
			return true;
		}

		return false;
	}
}