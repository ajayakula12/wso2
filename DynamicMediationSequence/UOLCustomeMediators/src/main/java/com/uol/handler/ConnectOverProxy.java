package com.uol.handler;


import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.google.gson.JsonArray;
import com.uol.utils.CommonUtils;

public class ConnectOverProxy {
    public static void main(String[] args) {
        //new ConnectOverProxy().ConnectOverProxyMysql();
        new ConnectOverProxy().ConnectOverProxyORACLE();
    }

    public void ConnectOverProxyMysql() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = null;
            Properties info = new Properties();
           // info.put("proxy_type", "4"); // SSL Tunneling
           // info.put("proxy_host", "10.10.9.32");
           // info.put("proxy_port", "8080");
            info.put("user", "sysadm");
            info.put("password", "Sysadm@2023#");
            conn = DriverManager.getConnection("jdbc:mysql://10.10.94.145:3306/uol_api_registry",info);


            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from api_master");
            JsonArray obj=CommonUtils.prepareResultSetJSONObject(rs);
          
            System.out.println("Data- " + obj);
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException er) {
        	System.out.println("============er===="+er);
            er.printStackTrace();
        } catch (ClassNotFoundException e) {
        	System.out.println("==============e=="+e);
            e.printStackTrace();
        }

    }
    
    public void ConnectOverProxyORACLE() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection conn = null;
            Properties info = new Properties();
           // info.put("proxy_type", "4"); // SSL Tunneling
           // info.put("proxy_host", "10.10.9.32");
           // info.put("proxy_port", "8080");
            info.put("user", "SYSADM");
            info.put("password", "SYSADM");
            conn = DriverManager.getConnection("jdbc:oracle:thin:@10.10.6.32:1521/EBBUCO",info);


            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT owner, table_name FROM all_tables");
            JsonArray obj=CommonUtils.prepareResultSetJSONObject(rs);
            
            System.out.println("Data oracle- " + obj);
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException er) {
        	System.out.println("============er===="+er);
            er.printStackTrace();
        } catch (ClassNotFoundException e) {
        	System.out.println("==============e=="+e);
            e.printStackTrace();
        }

    }
}