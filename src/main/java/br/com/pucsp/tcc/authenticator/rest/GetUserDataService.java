package br.com.pucsp.tcc.authenticator.rest;

import java.io.IOException;
import java.sql.SQLException;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.resources.users.GetUserDataDB;
import br.com.pucsp.tcc.authenticator.utils.ErrorResponse;
import br.com.pucsp.tcc.authenticator.utils.exceptions.BusinessException;
import br.com.pucsp.tcc.authenticator.utils.exceptions.DatabaseInsertException;
import br.com.pucsp.tcc.authenticator.utils.exceptions.InvalidEmailException;
import br.com.pucsp.tcc.authenticator.utils.exceptions.InvalidNameException;
import br.com.pucsp.tcc.authenticator.utils.exceptions.InvalidTokenException;
import br.com.pucsp.tcc.authenticator.utils.exceptions.UnregisteredUserException;

@WebServlet("/users/{userEmail}")
public class GetUserDataService extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(GetUserDataService.class);

	@Override
	protected void doGet(final @Context HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		resp.setContentType(MediaType.APPLICATION_JSON);
		String userEmail = req.getParameter("userEmail");

		try {
			GetUserDataDB getUserData = new GetUserDataDB();
			String response = getUserData.user(userEmail).toString();

			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().write(response);
		} catch (JSONException e) {
			ErrorResponse.build(resp, LOGGER, "Invalid JSON payload", HttpServletResponse.SC_BAD_REQUEST);
		} catch (InvalidEmailException | InvalidTokenException | InvalidNameException | UnregisteredUserException
				| BusinessException e) {
			ErrorResponse.build(resp, LOGGER, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
		} catch (SQLException | DatabaseInsertException e) {
			ErrorResponse.build(resp, LOGGER, "An error occurred with the database",
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} catch (MessagingException e) {
			ErrorResponse.build(resp, LOGGER, "An error occurred while sending email",
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			ErrorResponse.build(resp, LOGGER, "Unknown error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
}