package br.com.pucsp.tcc.authenticator.database;

public class SqlQueries {
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
	
	public static final String UPDATE_OTP_TABLE = "UPDATE otps \n"
			+ "SET is_active = true, otp = ? \n"
			+ "WHERE user_id = (SELECT user_id FROM users WHERE email = ?)";
	
	public static final String UPDATE_SESSION = "UPDATE sessions \n"
	           + "JOIN users ON users.user_id = sessions.user_id \n"
	           + "SET sessions.is_active = true \n"
	           + "WHERE sessions.session = ? AND users.email = ? \n"
	           + "AND sessions.created_at >= DATE_SUB(NOW(), INTERVAL 30 MINUTE) \n"
	           + "AND sessions.is_active = false";
	
	public static final String UPDATE_OTP = "UPDATE otps\n"
	           + "JOIN email_verifications ON otps.user_id = email_verifications.user_id\n"
	           + "SET is_active = false, is_confirmed = true\n"
	           + "WHERE otps.otp = ?\n"
	           + "AND otps.is_active = true\n"
	           + "AND otps.user_id = (SELECT user_id FROM users WHERE email = ?)\n"
	           + "AND otps.updated_at >= DATE_SUB(NOW(), INTERVAL 5 MINUTE)";
	
	public static final String UPDATE_NAME = "UPDATE users SET first_name = ?, last_name = ?\n"
    		+ "WHERE email = ?\n"
    		+ "AND user_id IN (SELECT user_id FROM sessions WHERE session = ? AND is_active = true)";
	
	public static final String UPDATE_SESSION_LOGOUT_ONE = "UPDATE sessions \n"
    		+ "SET is_active = false \n"
    		+ "WHERE session = ? \n"
    		+ "AND is_active = true \n"
    		+ "AND user_id = (SELECT user_id FROM users WHERE email = ?)";
	
	public static final String UPDATE_SESSION_LOGOUT_ALL = "DELETE a\n"
    		+ "FROM sessions a\n"
    		+ "JOIN (\n"
    		+ "    SELECT session, user_id\n"
    		+ "    FROM sessions\n"
    		+ "    WHERE is_active = true AND session = ?\n"
    		+ ") b ON a.session != b.session AND a.user_id = b.user_id\n"
    		+ "WHERE a.user_id = (SELECT user_id FROM users WHERE email = ?)";
	
	public static final String UPDATE_SESSION_LOGOUT_ALL_NOT_USED = "DELETE a\n"
		    + "FROM sessions a\n"
		    + "JOIN (\n"
		    + "    SELECT session, user_id\n"
		    + "    FROM sessions\n"
		    + "    WHERE is_active = true AND session = ?\n"
		    + ") b ON a.user_id = b.user_id\n"
		    + "WHERE a.user_id = (SELECT user_id FROM users WHERE email = ?)";
}