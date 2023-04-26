package br.com.pucsp.tcc.authenticator.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pucsp.tcc.authenticator.database.ConnDB;
import br.com.pucsp.tcc.authenticator.database.SqlQueries;
import br.com.pucsp.tcc.authenticator.utils.CreateToken;

public class SaveEmailTokenDB implements AutoCloseable {
	private static final Logger LOGGER = LoggerFactory.getLogger(SaveEmailTokenDB.class);
	
	private Connection connection;
	
	public SaveEmailTokenDB() throws SQLException {
		this.connection = ConnDB.getConnection();
	}
	
	public boolean insert(int userId, String userEmail, String requestIP, String requestBrowser, String requestOS) {
	    PreparedStatement statementDelete = null;
	    PreparedStatement statementInsert = null;
	    int tokenId = 0;
	    
	    String emailToken = CreateToken.generate("token");
	    
	    try {
	        statementDelete = connection.prepareStatement(SqlQueries.DELETE_EMAIL_TOKENS);
	        statementDelete.setInt(1, userId);
	        int rowsDeleted = statementDelete.executeUpdate();
	        LOGGER.info("'{}' access confirmations deleted from the database for user '{}'", rowsDeleted, userEmail);
	        
        	statementInsert = connection.prepareStatement(SqlQueries.INSERT_EMAIL_TOKEN, Statement.RETURN_GENERATED_KEYS);
        	statementInsert.setInt(1, userId);
        	statementInsert.setString(2, emailToken);
        	statementInsert.setString(3, requestIP);
        	statementInsert.setString(4, requestBrowser);
        	statementInsert.setString(5, requestOS);
        	statementInsert.setInt(6, userId);
        	statementInsert.executeUpdate();
        	
        	ResultSet generatedKeys = statementInsert.getGeneratedKeys();
	        if (generatedKeys.next()) {
	        	tokenId = generatedKeys.getInt(1);
	        }
	    }
	    catch (SQLException e) {
	    	LOGGER.error("Error inserting Email Token into database - Email: " + userEmail, e);
	    }
	    finally {
		    if(statementDelete != null) {
		        try {
		        	statementDelete.close();
		        } catch (SQLException e) {
		        	LOGGER.error("Error closing statementDelete", e);
		        }
		    }
		    if(statementInsert != null) {
		        try {
		        	statementInsert.close();
		        } catch (SQLException e) {
		        	LOGGER.error("Error closing statementInsert", e);
		        }
		    }
		    if(connection != null) {
		        try {
		            ConnDB.closeConnection(connection);
		        } catch (SQLException e) {
		        	LOGGER.error("Error closing connection", e);
		        }
		    }
		}
	    
	    if(tokenId >= 1) {
	    	return true;
	    }
	    else {
	    	return false;
	    }
	}

	@Override
	public void close() throws Exception {
		if(connection != null) {
			ConnDB.closeConnection(connection);
		}
	}
}