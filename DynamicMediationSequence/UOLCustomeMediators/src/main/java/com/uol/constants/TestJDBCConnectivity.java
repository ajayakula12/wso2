package com.uol.constants;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestJDBCConnectivity {

	public static void main(String s[])
	{
		Connection connectionObj = null;

		try {
			
			String dbConnectionUrl = "jdbc:mysql://10.10.94.145:3306/uol_api_registry?user=sysadm&password=Sysadm@2023#";

			System.out.println("==========>db url name::" + dbConnectionUrl);
			
			// connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
			Class.forName("com.mysql.cj.jdbc.Driver");
			
			connectionObj = DriverManager.getConnection(dbConnectionUrl);

		} catch (Exception e) {
			// Handle database connection errors
			System.out.println("===============>"+e);
			e.printStackTrace();
			System.out.println(e);
		}

		System.out.println("connection object is==>" + connectionObj);
		
	}
}
