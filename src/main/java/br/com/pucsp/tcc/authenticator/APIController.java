package br.com.pucsp.tcc.authenticator;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class APIController extends HttpServlet {
	private static final Logger LOGGER = LoggerFactory.getLogger(APIController.class);
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) throws Exception {
		JettyService.start();
	}

	public void init(ServletConfig config) throws ServletException {
		try {
			JettyService.start();
		} catch (Exception e) {
			LOGGER.error("Error starting jetty", e);
		}
	}

	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		LOGGER.info("APIController called");
	}

	public void destroy() {
		super.destroy();
	}
}