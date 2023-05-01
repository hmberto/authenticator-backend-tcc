package br.com.pucsp.tcc.authenticator.utils;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;

public class ErrorResponse {
	public static void build(HttpServletResponse resp, Logger LOGGER, String message, int status) throws IOException {
		String errorJson = new JSONObject().put("Message", message).toString();
		LOGGER.error(message);

		resp.setStatus(status);
		resp.getWriter().write(errorJson);
	}
}