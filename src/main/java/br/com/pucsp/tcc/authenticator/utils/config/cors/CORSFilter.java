package br.com.pucsp.tcc.authenticator.utils.config.cors;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;

import br.com.pucsp.tcc.authenticator.utils.system.SystemDefaultVariables;

public class CORSFilter implements Filter {
	
	private static final String SITE_HOST = SystemDefaultVariables.siteHost;
	
	public void init(FilterConfig filterConfig) throws ServletException {}

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) resp;
		response.setHeader("Access-Control-Allow-Origin", SITE_HOST);
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
		response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept, origins");
		response.setHeader("Access-Control-Max-Age", "3600");
		filterChain.doFilter(req, resp);
	}

	public void destroy() {}
}