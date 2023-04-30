package br.com.pucsp.tcc.authenticator.rest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.json.JSONObject;

import br.com.pucsp.tcc.authenticator.utils.DateTime;
import br.com.pucsp.tcc.authenticator.utils.GetUserBrowser;
import br.com.pucsp.tcc.authenticator.utils.GetUserOS;
import br.com.pucsp.tcc.authenticator.utils.LocalhostIP;

@WebServlet("/")
public class TestService extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(final @Context HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		resp.setContentType(MediaType.APPLICATION_JSON);
		resp.setStatus(HttpServletResponse.SC_OK);
		
		String userAgent = req.getHeader("User-Agent");
		
		String date = DateTime.date();
		String userIp = LocalhostIP.get(req.getRemoteAddr());
		String userBrowser = GetUserBrowser.browser(userAgent);
		String userOS = GetUserOS.os(userAgent);
		
		String res = new JSONObject()
				.put("date", date)
				.put("ip", userIp)
				.put("browser", userBrowser)
				.put("os", userOS)
				.toString();

		resp.getWriter().write(res);
	}
}