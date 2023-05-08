package br.com.pucsp.tcc.authenticator;

import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.rest.*;
import br.com.pucsp.tcc.authenticator.utils.CreateToken;

public class CheckAccessLinkTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(CheckAccessLinkTest.class);

	@Test
	public void runInvalidEmailTokenFormat() {
		try {
			String emailToken = "/0";
			emailToken(emailToken, "Invalid Email Token format");
		} catch (Exception e) {
			LOGGER.error("'Invalid Email Token format' test failed", e);
		}
	}

	@Test
	public void runEmailTokenDoesNotExist() {
		try {
			String emailToken = "/" + CreateToken.generate("token");
			emailToken(emailToken, "Email Token does not exist in database");
		} catch (Exception e) {
			LOGGER.error("'Email Token does not exist in database' test failed", e);
		}
	}

	private void emailToken(String emailToken, String errorMessage) throws Exception {
		HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
		HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
		PrintWriter writer = Mockito.mock(PrintWriter.class);

		Mockito.when(req.getMethod()).thenReturn("GET");
		Mockito.when(req.getRequestURI()).thenReturn("/api/check/access-link/" + emailToken);
		Mockito.when(req.getPathInfo()).thenReturn(emailToken);
		Mockito.when(req.getRemoteAddr()).thenReturn("127.0.0.1");
		Mockito.when(resp.getWriter()).thenReturn(writer);

		CheckAccessLinkService servlet = new CheckAccessLinkService();
		servlet.service(req, resp);

		String errorJson = new JSONObject().put("Message", errorMessage).toString();

		Mockito.verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);
		Mockito.verify(resp).setContentType(MediaType.APPLICATION_JSON);
		Mockito.verify(writer).write(errorJson);
	}
}