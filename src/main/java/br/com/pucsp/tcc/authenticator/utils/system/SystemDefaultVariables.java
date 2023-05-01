package br.com.pucsp.tcc.authenticator.utils.system;

public class SystemDefaultVariables {
	public static final String emailBox = getEnv("EMAIL_BOX", "contato@outlook.com");
	public static final String emailPass = getEnv("EMAIL_PASS", "password");
	public static final String emailServer = getEnv("EMAIL_SERVER", "OUTLOOK");
	public static final String dbHost = getEnv("DB_HOST", "db");
	public static final String dbName = getEnv("DB_NAME", "tcc-humberto");
	public static final String dbUser = getEnv("DB_USER", "user-db");
	public static final String dbPass = getEnv("DB_PASS", "user-pass");
	public static final String sqlScript = getEnv("SQL_SCRIPT", "db/v1_init.sql");
	public static final String siteHost = getEnv("SITE_HOST", "http://localhost:4200");
	public static final String timeZone = getEnv("TIME_ZONE", "America/Sao_Paulo");
	public static final String apiUser = getEnv("API_USER", "username");
	public static final String apiPass = getEnv("API_PASS", "password");
	public static final String contextPath = getEnv("CONTEXT_PATH", "/api");
	public static final int apiPort = Integer.parseInt(getEnv("API_PORT", "8080"));
	public static final int otpLength = Integer.parseInt(getEnv("OTP_LENGTH", "6"));
	public static final int sessionLength = Integer.parseInt(getEnv("SESSION_LENGTH", "100"));
	public static final int emailTokenLength = Integer.parseInt(getEnv("EMAIL_TOKEN_LENGTH", "50"));

	private static String getEnv(String name, String defaultValue) {
		String value = System.getenv(name);
		return value != null ? value : defaultValue;
	}
}