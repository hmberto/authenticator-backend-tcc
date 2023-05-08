package br.com.pucsp.tcc.authenticator;

import java.io.StringReader;
import java.io.PrintWriter;
import java.io.BufferedReader;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;
import org.junit.Test;
import org.mockito.Mockito;
import br.com.pucsp.tcc.authenticator.rest.*;
import br.com.pucsp.tcc.authenticator.utils.CreateToken;

public class CheckSessionTest {
	@Test
	public void runInvalidSessionTokenFormat() throws Exception {
		String email = "contato@teste.com";
		String session = "0";

		String body = new JSONObject().put("email", email).put("sessionToken", session).toString();

		String message = String.format("Invalid %s format '%s'", "session", session);
		runTest(body, message);
	}
	
	@Test
	public void runInvalidEmailFormat() throws Exception {
		String email = "contato@teste@com";
		String session = CreateToken.generate("session");

		String body = new JSONObject().put("email", email).put("sessionToken", session).toString();

		String message = String.format("Invalid email format '%s'", email);
		runTest(body, message);
	}
	
	@Test
	public void runUnregisteredUser() throws Exception {
		String email = "contato@teste.com";
		String session = CreateToken.generate("session");

		String body = new JSONObject().put("email", email).put("sessionToken", session).toString();

		runTest(body, "Unable to validate Session Token to unregistered user");
	}

	private void runTest(String body, String errorMessage) throws Exception {
		HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
		HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
		PrintWriter writer = Mockito.mock(PrintWriter.class);

		Mockito.when(req.getMethod()).thenReturn("POST");
		Mockito.when(req.getRequestURI()).thenReturn("/api/check/session");
		Mockito.when(req.getPathInfo()).thenReturn("/api/check/session");
		Mockito.when(req.getRemoteAddr()).thenReturn("127.0.0.1");
		Mockito.when(resp.getWriter()).thenReturn(writer);

		StringReader stringReader = new StringReader(body);
		BufferedReader bufferedReader = new BufferedReader(stringReader);
		Mockito.when(req.getReader()).thenReturn(bufferedReader);

		CheckSessionService servlet = new CheckSessionService();
		servlet.service(req, resp);

		String errorJson = new JSONObject().put("Message", errorMessage).toString();

		Mockito.verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);
		Mockito.verify(resp).setContentType(MediaType.APPLICATION_JSON);
		Mockito.verify(writer).write(errorJson);
	}
}