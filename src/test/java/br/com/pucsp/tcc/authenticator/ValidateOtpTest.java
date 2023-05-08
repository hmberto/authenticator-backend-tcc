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

public class ValidateOtpTest {
	@Test
	public void runInvalidEmailFormat() throws Exception {
		String otp = CreateToken.generate("otp");
		String email = "contato@teste@com";

		String body = new JSONObject().put("email", email).put("otp", otp).toString();

		String message = String.format("Invalid email format '%s'", email);
		runTest(body, HttpServletResponse.SC_BAD_REQUEST, message);
	}

	@Test
	public void runInvalidOTPFormat() throws Exception {
		String otp = "0";
		String email = "contato@teste.com";

		String body = new JSONObject().put("email", email).put("otp", otp).toString();

		String message = String.format("Invalid %s format '%s'", "otp", otp);
		runTest(body, HttpServletResponse.SC_BAD_REQUEST, message);
	}

	@Test
	public void runNullEmail() throws Exception {
		String otp = CreateToken.generate("otp");

		String body = new JSONObject().put("otp", otp).toString();

		String message = "email is required but not sent";
		runTest(body, HttpServletResponse.SC_BAD_REQUEST, message);
	}

	@Test
	public void runNullOTP() throws Exception {
		String email = "contato@teste.com";

		String body = new JSONObject().put("email", email).toString();

		String message = String.format("%s is required but not sent", "otp");
		runTest(body, HttpServletResponse.SC_BAD_REQUEST, message);
	}

	private void runTest(String body, int status, String errorMessage) throws Exception {
		HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
		HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
		PrintWriter writer = Mockito.mock(PrintWriter.class);

		Mockito.when(req.getMethod()).thenReturn("POST");
		Mockito.when(req.getRequestURI()).thenReturn("/api/validate/otp");
		Mockito.when(req.getPathInfo()).thenReturn("/api/validate/otp");
		Mockito.when(req.getRemoteAddr()).thenReturn("127.0.0.1");
		Mockito.when(resp.getWriter()).thenReturn(writer);

		StringReader stringReader = new StringReader(body);
		BufferedReader bufferedReader = new BufferedReader(stringReader);
		Mockito.when(req.getReader()).thenReturn(bufferedReader);

		ValidateOtpService servlet = new ValidateOtpService();
		servlet.service(req, resp);

		String errorJson = new JSONObject().put("Message", errorMessage).toString();

		Mockito.verify(resp).setStatus(status);
		Mockito.verify(resp).setContentType(MediaType.APPLICATION_JSON);
		Mockito.verify(writer).write(errorJson);
	}
}