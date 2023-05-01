package br.com.pucsp.tcc.authenticator.resources.users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.json.JSONObject;
import br.com.pucsp.tcc.authenticator.database.ConnDB;
import br.com.pucsp.tcc.authenticator.database.SqlQueries;
import br.com.pucsp.tcc.authenticator.utils.DataValidator;
import br.com.pucsp.tcc.authenticator.utils.exceptions.InvalidEmailException;

public class GetUserDataDB {
	public JSONObject user(String userEmail) throws Exception {
		JSONObject json = new JSONObject();
		int userId = 0;

		validateBody(userEmail);

		try (ConnDB connDB = ConnDB.getInstance();
				Connection connection = connDB.getConnection();
				PreparedStatement statement = connection.prepareStatement(SqlQueries.GET_USER);) {

			statement.setString(1, userEmail);

			ResultSet rs = statement.executeQuery();

			if (rs.next()) {
				userId = rs.getInt("user_id");
				json.put("userId", userId);
				json.put("firstName", rs.getString("first_name"));
				json.put("lastName", rs.getString("last_name"));
			}
		}

		return json.length() != 0 ? json : null;
	}

	private static void validateBody(String userEmail) throws Exception {
		if (!DataValidator.isValidEmail(userEmail)) {
			throw new InvalidEmailException("Invalid email format");
		}
	}
}