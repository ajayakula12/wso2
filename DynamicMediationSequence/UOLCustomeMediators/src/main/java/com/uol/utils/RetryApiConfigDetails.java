package com.uol.utils;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.apache.synapse.MessageContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.uol.constants.DBinfoConstants;
import com.uol.dto.ApiLogHandlerDto;
import com.uol.dto.ApiResponseDTO;

public class RetryApiConfigDetails {

	private PreparedStatement preparedStatement=null;
	private Connection targetDBConnection=null;
	private List<ApiLogHandlerDto> apiInfoList=null;
	public List<ApiLogHandlerDto> getApiRequestLogDetails(String sourceRequestId ) throws SQLException
	{
		try {
	    targetDBConnection=JDBCConnectionUtil.getUOLJndiConneciton();;
		if (targetDBConnection != null) {
		preparedStatement = targetDBConnection.prepareStatement(DBinfoConstants.REQUEST_RETRY_LOG_HANDLER_QUERY);
		preparedStatement.setString(1, sourceRequestId);
		boolean execute = preparedStatement.execute();
		JsonArray resultsetJSONArray=null;
		if (execute) {
			ResultSet resultSet = preparedStatement.getResultSet();
			resultsetJSONArray = CommonUtils.prepareResultSetJSONObject(resultSet);	
			ObjectMapper mapper = new ObjectMapper();
			apiInfoList = mapper.readValue(resultsetJSONArray.toString(), new TypeReference<List<ApiLogHandlerDto>>() {});
			resultSet.close();
		} 
		
		} 
		}catch (Exception e) {
			e.printStackTrace();
		}finally
		{
			preparedStatement.close();
			targetDBConnection.close();
		}
		return apiInfoList;
	}
}
