package br.com.pucsp.tcc.authenticator;

import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.pucsp.tcc.authenticator.rest.*;
import br.com.pucsp.tcc.authenticator.utils.CreateToken;

public class AppTest {
	@Test
	public void testMinhaServlet() throws Exception {
		String emailToken = CreateToken.generate("token");

	    HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
	    HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
	    PrintWriter writer = Mockito.mock(PrintWriter.class);
	    
	    System.out.println(emailToken);

	    Mockito.when(req.getMethod()).thenReturn("GET");
	    Mockito.when(req.getRequestURI()).thenReturn("/api/check/access-link/" + emailToken);
	    Mockito.when(req.getPathInfo()).thenReturn(emailToken);
	    Mockito.when(req.getRemoteAddr()).thenReturn("127.0.0.1");
	    Mockito.when(resp.getWriter()).thenReturn(writer);

	    CheckAccessLinkService servlet = new CheckAccessLinkService();
	    servlet.service(req, resp);

	    String errorJson = new JSONObject().put("Message", "Invalid Email Token format").toString();

	    Mockito.verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	    Mockito.verify(resp).setContentType(MediaType.APPLICATION_JSON);
	    Mockito.verify(writer).write(errorJson);
	}
}