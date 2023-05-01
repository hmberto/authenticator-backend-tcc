package br.com.pucsp.tcc.authenticator.resources.tokens;

import java.sql.Connection;

import org.json.JSONObject;
import br.com.pucsp.tcc.authenticator.database.ConnDB;
import br.com.pucsp.tcc.authenticator.resources.users.EmailTokenManagerDB;
import br.com.pucsp.tcc.authenticator.utils.DataValidator;
import br.com.pucsp.tcc.authenticator.utils.exceptions.InvalidTokenException;
import br.com.pucsp.tcc.authenticator.utils.system.SystemDefaultVariables;

public class GetEmailTokenInfo {
	private static final int EMAIL_TOKEN_LENGTH = SystemDefaultVariables.emailTokenLength;

	public JSONObject verify(final String userEmailToken, final String userIP) throws Exception {
		validateBody(userEmailToken);

		try (ConnDB connDB = ConnDB.getInstance(); Connection connection = connDB.getConnection();) {

			EmailTokenManagerDB emailTokenManagerDB = new EmailTokenManagerDB();
			JSONObject json = emailTokenManagerDB.getToken(connection, userEmailToken);

			if (json.getString("requestIP").equals(userIP)) {
				json.put("sameIP", true);
			}

			json.put("sameIP", true);

			return json;
		}
	}

	private static void validateBody(String userEmailToken) throws Exception {
		if (userEmailToken == null) {
			throw new InvalidTokenException("Email Token is required but not sent");
		}
		if (!DataValidator.isValidToken(userEmailToken) || userEmailToken.length() != EMAIL_TOKEN_LENGTH) {
			throw new InvalidTokenException("Invalid Email Token format");
		}
	}
}