package com.uol.mediators.query;


import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

import com.google.gson.JsonArray;
import com.uol.utils.CommonUtils;
 
public class TestSql {
 
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Connection connectionObj = null;
 
		try {
			String dbConnectionUrl = "jdbc:mysql://172.16.110.240:3306/uol_api_registry?user=root&password=Adm!n@123";
 
			System.out.println("db url name::" + dbConnectionUrl);
			// connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
			Class.forName("com.mysql.cj.jdbc.Driver");
			connectionObj = DriverManager.getConnection(dbConnectionUrl);
			
			//PreparedStatement ps=connectionObj.prepareStatement("SELECT * FROM uol_api_registry.sub_api_master");
			PreparedStatement ps=connectionObj.prepareCall("{call uol_api_registry.SearchRecordsByTmfParams('read_customer','account')}");
			//ps.registerOutParameter(1, Types.VARCHAR);
			//ps.setString(1, "account,contact");
			
			//System.out.println(ps.executeQuery().getFetchSize());
			ResultSet b=ps.executeQuery();
			System.out.println("result============"+b);

			
				JsonArray ar= CommonUtils.prepareResultSetJSONObject(b);
 
				
				System.out.println("re=======>"+ar);
			
	}catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
	}
 
}
}
