package br.com.pucsp.tcc.authenticator.resources.users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.SqlQueries;
import br.com.pucsp.tcc.authenticator.utils.exceptions.DatabaseInsertException;

public class UndoChangesSaveUserDB {
	private static final Logger LOGGER = LoggerFactory.getLogger(UndoChangesSaveUserDB.class);

	public void recovery(Connection connection, int userId) throws SQLException, DatabaseInsertException {
		try (PreparedStatement statementUser = connection.prepareStatement(SqlQueries.DELETE_USER,
				Statement.RETURN_GENERATED_KEYS);
				PreparedStatement statementOTP = connection.prepareStatement(SqlQueries.DELETE_OTP,
						Statement.RETURN_GENERATED_KEYS);
				PreparedStatement statementSession = connection.prepareStatement(SqlQueries.DELETE_SESSION,
						Statement.RETURN_GENERATED_KEYS);
				PreparedStatement statementConfirmEmail = connection
						.prepareStatement(SqlQueries.DELETE_EMAIL_VERIFICATION, Statement.RETURN_GENERATED_KEYS)) {

			statementOTP.setInt(1, userId);
			statementOTP.setInt(2, userId);
			statementOTP.executeUpdate();

			statementSession.setInt(1, userId);
			statementSession.setInt(2, userId);
			statementSession.executeUpdate();

			statementConfirmEmail.setInt(1, userId);
			statementConfirmEmail.setInt(2, userId);
			statementConfirmEmail.executeUpdate();

			statementUser.setInt(1, userId);
			statementUser.setInt(2, userId);
			statementUser.executeUpdate();

			LOGGER.info("Changes undone: user '{}' removed from database - Cause: error during new database sign up",
					userId);
		}
	}
}