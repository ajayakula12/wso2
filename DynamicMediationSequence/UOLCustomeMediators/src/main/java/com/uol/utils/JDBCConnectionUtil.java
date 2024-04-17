package com.uol.utils;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.uol.constants.DBinfoConstants;
import com.uol.dto.QueryConfig;


public class JDBCConnectionUtil {
	private static final Log log = LogFactory.getLog(JDBCConnectionUtil.class);

	public static Connection getUOLJndiConneciton() {

		Connection conn=null;
		try {
			Context ctx = new InitialContext();
			DataSource dataSource = (DataSource) ctx.lookup(DBinfoConstants.UOL_JNDI_CONNECTION);
			conn=dataSource.getConnection();	
			return conn;
		} 
		catch (Exception e) {			
			log.error("UOL JNDI SQL Connection Failure Error:"+e.getMessage());
			return null;
		}

	}
	public static Connection getEBJndiConneciton() {

		Connection conn=null;
		try {
			Context ctx = new InitialContext();
			DataSource dataSource = (DataSource) ctx.lookup(DBinfoConstants.EB_JNDI_CONNECTION);
			conn=dataSource.getConnection();	
			return conn;
		} 
		catch (Exception e) {			
			log.error("UOL EB JNDI SQL Connection Failure Error:"+e.getMessage());
			return null;
		}

	}
	public static Connection getRTXJndiConneciton() {

		Connection conn=null;
		try {
			Context ctx = new InitialContext();
			DataSource dataSource = (DataSource) ctx.lookup(DBinfoConstants.RTX_JNDI_CONNECTION);
			conn=dataSource.getConnection();	
			return conn;
		} 
		catch (Exception e) {			
			log.error("UOL RTX JNDI SQL Connection Failure Error:"+e.getMessage());
			return null;
		}

	}
	public static Connection connectToDatabase(String systemName, String schemaName, Properties properties) {
		// Implement database connection logic based on the system name
		// For example, use different JDBC drivers and connection parameters for MySQL
		// and Oracle
		Connection connectionObj = null;

		try {
			String dbUrl = null, dbUser = null, dbPassword = null, dbDriver = null;
			if (properties != null) {
				dbUrl = properties.getProperty(systemName + ".db.url"); // Change this to your database URL
				dbUser = properties.getProperty(systemName + ".db.username");
				dbPassword = properties.getProperty(systemName + ".db.password");
				dbDriver = properties.getProperty(systemName +".db.driver");

			}
			String dbConnectionUrl = dbUrl + schemaName + "?user=" + dbUser + "&password=" + dbPassword;

			log.info("==========>db url name::" + dbConnectionUrl);

			// connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
			Class.forName(dbDriver);

			connectionObj = DriverManager.getConnection(dbConnectionUrl);

		} catch (Exception e) {
			// Handle database connection errors
			log.error("===============>"+e);
			e.printStackTrace();
			System.out.println(e);
		}

		log.info("connection object is==>" + connectionObj);
		return connectionObj;
	}

	// Method to fetch query configuration from the database based on query name
	public static QueryConfig getQueryConfigFromDatabase(String queryName, Properties properties) throws SQLException {
		// Load the properties file containing dynamic queries
		Connection connection=null;
		PreparedStatement preparedStatement=null;
		ResultSet resultSet=null;
		String dbUrl = null, dbUser = null, dbPassword = null, queryConfigPath = null, dbDriver=null;// Change this to your database
		// URL

		if (properties != null) {
			dbUrl = properties.getProperty(DBinfoConstants.dbUrl); // Change this to your database URL
			dbUser = properties.getProperty(DBinfoConstants.dbUser);
			dbPassword = properties.getProperty(DBinfoConstants.dbPassword);
			queryConfigPath = properties.getProperty(DBinfoConstants.queryConfigPath);
			dbDriver = properties.getProperty(DBinfoConstants.dbDriver);
		}

		QueryConfig queryConfig = null;
		try {
			// Establish a database connection


			Class.forName(dbDriver);

			connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
			log.info("query name::" + queryName);
			// Prepare and execute the SQL query to retrieve query configuration based on
			String sql =DBinfoConstants.ROOT_QUERY_PATH;
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, queryName);
			resultSet = preparedStatement.executeQuery();
			// Populate the query configuration
			queryConfig = new QueryConfig();
			while (resultSet.next()) {

				queryConfig.setSystemName(resultSet.getString(DBinfoConstants.DB_SYSTEM));
				queryConfig.setSchemaName(resultSet.getString(DBinfoConstants.QUERY_SCHEMA));
				queryConfig.addParameter(resultSet.getString(DBinfoConstants.DB_PARAMS));
				queryConfig.setPropertiesFile(queryConfigPath + resultSet.getString(DBinfoConstants.QUERY_PROPERTIES));

			}
			log.info("queryConfig obj " + queryConfig.getPropertiesFile());

		} catch (Exception e) {
			// Handle database-related exceptions
			e.printStackTrace();			
		}finally {
			// Close the database resources
			resultSet.close();
			preparedStatement.close();
			connection.close();
		}
		return queryConfig;
	}
}
