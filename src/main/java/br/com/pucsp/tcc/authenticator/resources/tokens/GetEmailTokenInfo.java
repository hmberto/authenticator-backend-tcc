package br.com.pucsp.tcc.authenticator.resources.tokens;

import java.sql.Connection;

import org.json.JSONObject;
import br.com.pucsp.tcc.authenticator.database.ConnDB;
import br.com.pucsp.tcc.authenticator.resources.users.EmailTokenManagerDB;
import br.com.pucsp.tcc.authenticator.utils.DataValidator;

public class GetEmailTokenInfo {
	public JSONObject verify(final String userEmailToken, final String userIP) throws Exception {
		validateBody(userEmailToken);

		try (ConnDB connDB = ConnDB.getInstance(); Connection connection = connDB.getConnection();) {

			EmailTokenManagerDB emailTokenManagerDB = new EmailTokenManagerDB();
			JSONObject json = emailTokenManagerDB.getToken(connection, userEmailToken);

			if (json.getString("requestIP").equals(userIP)) {
				json.put("sameIP", true);
			}

			json.put("sameIP", false);

			return json;
		}
	}

	private static void validateBody(String userEmailToken) throws Exception {
		DataValidator.isValidToken(userEmailToken, "token");
	}
}