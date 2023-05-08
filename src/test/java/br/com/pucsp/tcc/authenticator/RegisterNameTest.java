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

public class RegisterNameTest {
	@Test
	public void runInvalidEmailFormat() throws Exception {
		String session = CreateToken.generate("session");
		String email = "contato@teste@com";
		String firstName = "Teste";
		String lastName = "API";

		String body = new JSONObject()
				.put("email", email)
				.put("firstName", firstName)
				.put("lastName", lastName)
				.put("session", session)
				.toString();

		String message = String.format("Invalid email format '%s'", email);
		runTest(body, HttpServletResponse.SC_BAD_REQUEST, message);
	}
	
	@Test
	public void runInvalidFirstNameFormat() throws Exception {
		String session = CreateToken.generate("session");
		String email = "contato@teste.com";
		String firstName = "Teste!";
		String lastName = "API";

		String body = new JSONObject()
				.put("email", email)
				.put("firstName", firstName)
				.put("lastName", lastName)
				.put("session", session)
				.toString();

		String message = String.format("Invalid %s format '%s'", "first name", firstName);
		runTest(body, HttpServletResponse.SC_BAD_REQUEST, message);
	}
	
	@Test
	public void runInvalidLastNameFormat() throws Exception {
		String session = CreateToken.generate("session");
		String email = "contato@teste.com";
		String firstName = "Teste";
		String lastName = "API!";

		String body = new JSONObject()
				.put("email", email)
				.put("firstName", firstName)
				.put("lastName", lastName)
				.put("session", session)
				.toString();

		String message = String.format("Invalid %s format '%s'", "last name", lastName);
		runTest(body, HttpServletResponse.SC_BAD_REQUEST, message);
	}
	
	@Test
	public void runInvalidSessionTokenFormat() throws Exception {
		String session = "0";
		String email = "contato@teste.com";
		String firstName = "Teste";
		String lastName = "API";

		String body = new JSONObject()
				.put("email", email)
				.put("firstName", firstName)
				.put("lastName", lastName)
				.put("session", session)
				.toString();

		String message = String.format("Invalid %s format '%s'", "session", session);
		runTest(body, HttpServletResponse.SC_BAD_REQUEST, message);
	}
	
	private void runTest(String body, int status, String errorMessage) throws Exception {
		HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
		HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
		PrintWriter writer = Mockito.mock(PrintWriter.class);

		Mockito.when(req.getMethod()).thenReturn("PUT");
		Mockito.when(req.getRequestURI()).thenReturn("/api/register/name");
		Mockito.when(req.getPathInfo()).thenReturn("/api/register/name");
		Mockito.when(req.getRemoteAddr()).thenReturn("127.0.0.1");
		Mockito.when(resp.getWriter()).thenReturn(writer);

		StringReader stringReader = new StringReader(body);
		BufferedReader bufferedReader = new BufferedReader(stringReader);
		Mockito.when(req.getReader()).thenReturn(bufferedReader);

		RegisterNameService servlet = new RegisterNameService();
		servlet.service(req, resp);

		String errorJson = new JSONObject().put("Message", errorMessage).toString();

		Mockito.verify(resp).setStatus(status);
		Mockito.verify(resp).setContentType(MediaType.APPLICATION_JSON);
		Mockito.verify(writer).write(errorJson);
	}
}