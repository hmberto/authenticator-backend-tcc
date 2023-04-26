package br.com.pucsp.tcc.authenticator.impl;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import br.com.pucsp.tcc.authenticator.utils.DateTime;
import br.com.pucsp.tcc.authenticator.utils.GetUserBrowser;
import br.com.pucsp.tcc.authenticator.utils.GetUserOS;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TestService {
	@GET
	public Response validateData(@Context HttpServletRequest request) {
		String userAgent = request.getHeader("User-Agent");
		
		String loginDate = DateTime.date();
		String userIP = request.getRemoteAddr();
		String userBrowser = GetUserBrowser.browser(userAgent);
		String userOS = GetUserOS.os(userAgent);
		
		String res = new JSONObject()
				.put("date", loginDate)
				.put("ip", userIP)
				.put("browser", userBrowser)
				.put("os", userOS)
				.toString();
		
		return Response.ok(res).build();
	}
}