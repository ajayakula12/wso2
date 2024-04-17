package com.uol.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.uol.constants.DBinfoConstants;
import com.uol.dto.ApiResponseDTO;

public class GetApiConfigDetails {

    private PreparedStatement preparedStatement = null;
    private Connection targetDBConnection = null;
    private List<ApiResponseDTO> apiInfoList = null;

    public List<ApiResponseDTO> getMasterApiConfigDetails(String subApiId,String apiQueryType,String tmfParams) throws SQLException {
        try {
            targetDBConnection = JDBCConnectionUtil.getUOLJndiConneciton();


            if (targetDBConnection != null) {
            	if(apiQueryType!=null) {
            	if(apiQueryType.equalsIgnoreCase("tmf_proc")) 
            	{
            		 preparedStatement = targetDBConnection.prepareCall(DBinfoConstants.API_MASTER_PROCEDURE);
                     preparedStatement.setString(1, subApiId);
                     
                     if(StringUtils.isEmpty(tmfParams))
                     {
                    	 preparedStatement.setString(2, ""); 
                     }else {
                     preparedStatement.setString(2, tmfParams);
                     }
            	}
            	}else
            	{
                preparedStatement = targetDBConnection.prepareStatement(DBinfoConstants.API_MASTER_QUERY);
                preparedStatement.setString(1, subApiId);
            	}
                
                boolean execute = preparedStatement.execute();
                System.out.println("execution status::==================" + execute);

                JsonArray resultsetJSONArray = null;

                if (execute) {
                    ResultSet resultSet = preparedStatement.getResultSet();
                    System.out.println("resultSet==================" + resultSet);

                    resultsetJSONArray = CommonUtils.prepareResultSetJSONObject(resultSet);

                    ObjectMapper mapper = new ObjectMapper();
                    apiInfoList = mapper.readValue(resultsetJSONArray.toString(),
                            new TypeReference<List<ApiResponseDTO>>() {});
                    resultSet.close();
                }
            }
        } catch (Exception e) {
            // Handle exception appropriately
            System.out.println("exception==================" + e);
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return apiInfoList;
    }

    private void closeResources() {
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (targetDBConnection != null) {
                targetDBConnection.close();
            }
        } catch (SQLException e) {
            // Handle exception appropriately or log it
            e.printStackTrace();
        }
    }
    
    
    public boolean validateUserAuthenticationDetails(String username,String password) throws SQLException {
      boolean userValidationStatus=false;
    	try {

            targetDBConnection = JDBCConnectionUtil.getUOLJndiConneciton();

            if (targetDBConnection != null) {           	
            	
               preparedStatement = targetDBConnection.prepareStatement(DBinfoConstants.API_USER_AUTH_QUERY);
               preparedStatement.setString(1, username);
               preparedStatement.setString(2, password);
            	
                
                ResultSet resultSet = preparedStatement.executeQuery();
                System.out.println("resultset size status::==================" + resultSet.getFetchSize());

              

                if (resultSet.next()) {
                    
                   
                    	userValidationStatus=true;	
                }else {
                    	userValidationStatus=false;
                    }
                    System.out.println("userValidationStatus==================" + userValidationStatus);

                    
                    resultSet.close();
                
            }
        } catch (Exception e) {
            // Handle exception appropriately
            System.out.println("exception==================" + e);
           
            return false;
        } finally {
            closeResources();
        }
        return userValidationStatus;
    }
}
