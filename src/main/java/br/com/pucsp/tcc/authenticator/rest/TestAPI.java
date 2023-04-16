package br.com.pucsp.tcc.authenticator.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TestAPI {
	@GET
	public Response test() {
		String res = new JSONObject().put("message", "Hello World!").toString();
		return Response.ok(res).build();
	}
}