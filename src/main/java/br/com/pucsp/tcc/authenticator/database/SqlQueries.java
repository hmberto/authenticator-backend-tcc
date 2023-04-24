package br.com.pucsp.tcc.authenticator.database;

public class SqlQueries {
	public static final String UPDATE_OTP_TABLE = "UPDATE otps \n"
			+ "SET is_active = true, otp = ? \n"
			+ "WHERE user_id = (SELECT user_id FROM users WHERE email = ?);";
	
	public static final String INSERT_USER = "INSERT INTO users (first_name, last_name, email) VALUES (?, ?, ?)";
	public static final String INSERT_OTP = "INSERT INTO otps (user_id, otp, is_active) VALUES (?, ?, true)";
	public static final String INSERT_SESSION = "INSERT INTO sessions (user_id, session, is_active) VALUES (?, ?, ?)";
	public static final String INSERT_EMAIL_VERIFICATION = "INSERT INTO email_verifications (user_id, is_confirmed) VALUES (?, false)";
	
	public static final String DELETE_EMAIL_VERIFICATION = "DELETE FROM email_verifications WHERE user_id = ? AND EXISTS (SELECT user_id FROM email_verifications WHERE user_id = ?)";
	public static final String DELETE_SESSION = "DELETE FROM sessions WHERE user_id = ? AND EXISTS (SELECT user_id FROM sessions WHERE user_id = ?)";
	public static final String DELETE_OTP = "DELETE FROM otps WHERE user_id = ? AND EXISTS (SELECT user_id FROM otps WHERE user_id = ?)";
	public static final String DELETE_USER = "DELETE FROM users WHERE user_id = ? AND EXISTS (SELECT user_id FROM users WHERE user_id = ?)";
	
	public static final String CHECK_EMAIL_ALREADY_REGISTERED = "SELECT sessions.user_id, sessions.session, sessions.is_active, users.first_name\n"
    		+ "FROM sessions\n"
    		+ "INNER JOIN users ON users.user_id = sessions.user_id\n"
    		+ "WHERE users.email = ?";
}