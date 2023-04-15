package br.com.pucsp.tcc.authenticator.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.sql.DriverManager;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnDB {
	private ConnDB () {}
	
	private static String name = ConnDB.class.getSimpleName();
	private static Logger log = Logger.getLogger(ConnDB.class.getName());
	
    private static Connection connection;
    private static Properties properties;

    private static Properties getProperties() {
    	log.entering(name, "getProperties");
    	
    	if (properties == null) {
    		properties = new Properties();
    		properties.setProperty("user", System.getenv("DB_USER"));
    		properties.setProperty("password", System.getenv("DB_PASS"));
    		properties.setProperty("MaxPooledStatements", "250");
    	}
    	
    	log.log(Level.INFO, "Properties setuped");
    	
    	log.exiting(name, "getProperties");
    	return properties;
    }

	public static Connection connect() throws SQLException, ClassNotFoundException {
		log.entering(name, "connect");
		
		if (connection == null) {
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				connection = DriverManager.getConnection(
						  "jdbc:mysql://" + System.getenv("DB_HOST") + "/" + System.getenv("DB_NAME") + "?sslMode=VERIFY_IDENTITY",
						  System.getenv("DB_USER"),
						  System.getenv("DB_PASS"));
				
				log.log(Level.INFO, "Connection started");
			} catch (SQLException e) {
				throw new SQLException(e.toString());
			}
		}
		
		log.exiting(name, "connect");
		return connection;
	}

	public static void disconnect() throws SQLException {
		log.entering(name, "disconnect");
		
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                
                log.log(Level.INFO, "Connection closed");
            } catch (SQLException e) {
            	throw new SQLException(e.toString());
            }
        }
        
        log.exiting(name, "disconnect");
    }
}