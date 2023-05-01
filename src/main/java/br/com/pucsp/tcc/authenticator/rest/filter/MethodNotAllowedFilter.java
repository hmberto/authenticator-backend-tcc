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
import java.util.HashMap;
import java.util.Map;

public class MethodNotAllowedFilter implements Filter {
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodNotAllowedFilter.class);

	private static final Map<String, String> ALLOWED_METHODS = new HashMap<>();

	static {
		ALLOWED_METHODS.put("/test", HttpMethod.GET);
		ALLOWED_METHODS.put("/api/test", HttpMethod.GET);
		ALLOWED_METHODS.put("/api/check/access-link/\\w+", HttpMethod.GET);
		ALLOWED_METHODS.put("/api/check/session", HttpMethod.POST);
		ALLOWED_METHODS.put("/api/users/\\S+", HttpMethod.GET);
		ALLOWED_METHODS.put("/api/logout", HttpMethod.PUT);
		ALLOWED_METHODS.put("/api/register/email", HttpMethod.POST);
		ALLOWED_METHODS.put("/api/register/name", HttpMethod.PUT);
		ALLOWED_METHODS.put("/api/validate/access-link", HttpMethod.POST);
		ALLOWED_METHODS.put("/api/validate/otp", HttpMethod.POST);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}

	@Override
	public void destroy() {}

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
		for (Map.Entry<String, String> entry : ALLOWED_METHODS.entrySet()) {
			String patternString = entry.getKey();
			String allowedMethod = entry.getValue();

			Pattern pattern = Pattern.compile(patternString);
			Matcher matcher = pattern.matcher(requestURI);

			if (matcher.matches() && 
					(httpMethod.equals(allowedMethod) || httpMethod.equals(HttpMethod.OPTIONS))) {
				return true;
			}
		}
		return false;
	}
}