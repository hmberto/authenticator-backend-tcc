package br.com.pucsp.tcc.authenticator;

import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;
import org.junit.Test;
import org.mockito.Mockito;
import br.com.pucsp.tcc.authenticator.rest.*;

public class GetUserDataTest {
	@Test
	public void runEmailDoesNotExist() throws Exception {
		String email = "/contato@teste.com";
		runTest(email, HttpServletResponse.SC_BAD_REQUEST, "Email does not exist in database");
	}

	@Test
	public void runInvalidEmailFormat() throws Exception {
		String email = "contato@teste@com";
		
		String message = String.format("Invalid email format '%s'", email);
		runTest("/" + email, HttpServletResponse.SC_BAD_REQUEST, message);
	}

	private void runTest(String email, int status, String errorMessage) throws Exception {
		HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
		HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
		PrintWriter writer = Mockito.mock(PrintWriter.class);

		Mockito.when(req.getMethod()).thenReturn("GET");
		Mockito.when(req.getRequestURI()).thenReturn("/users" + email);
		Mockito.when(req.getPathInfo()).thenReturn(email);
		Mockito.when(req.getRemoteAddr()).thenReturn("127.0.0.1");
		Mockito.when(resp.getWriter()).thenReturn(writer);

		GetUserDataService servlet = new GetUserDataService();
		servlet.service(req, resp);

		String errorJson = new JSONObject().put("Message", errorMessage).toString();

		Mockito.verify(resp).setStatus(status);
		Mockito.verify(resp).setContentType(MediaType.APPLICATION_JSON);
		Mockito.verify(writer).write(errorJson);
	}
}