package com.uol.mediators;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

import com.uol.constants.DBinfoConstants;
import com.uol.utils.JDBCConnectionUtil;
import com.uol.utils.PropertiesUtil;

public class UOLResponseLogMediator extends AbstractMediator {
	
	    private String source_req_id;
	    private String child_req_id;
	    private String response;
	    private String tmf_response;

		private String status;
	    private int logStatus;


	    public String getTmf_response() {
			return tmf_response;
		}

		public void setTmf_response(String tmf_response) {
			this.tmf_response = tmf_response;
		}

		public int getLogStatus() {
			return logStatus;
		}

		public void setLogStatus(int logStatus) {
			this.logStatus = logStatus;
		}

	    public String getSource_req_id() {
			return source_req_id;
		}

		public void setSource_req_id(String source_req_id) {
			if(source_req_id!=null)
			{
				source_req_id=source_req_id.replace("urn:uuid:", "");
			}
			this.source_req_id = source_req_id;
		}

		public String getChild_req_id() {
			return child_req_id;
		}

		public void setChild_req_id(String child_req_id) {
			if(child_req_id!=null)
			{
				child_req_id=child_req_id.replace("urn:uuid:", "");
			}
			this.child_req_id = child_req_id;
		}

		public String getResponse() {
			return response;
		}

		public void setResponse(String response) {
			this.response = response;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

	@Override
	public boolean mediate(MessageContext messageContext) {
		Connection con=null;PreparedStatement pstmt=null;
		
		try {
			if(logStatus!=0)
			{
				 con = JDBCConnectionUtil.getUOLJndiConneciton();			

			String updateQuery = "UPDATE `uol_api_registry`.`api_log_handler` "
                    + "SET `response` = ?, `status` = ?,`tmf_response` = ?  WHERE `source_req_id` = ? AND `child_req_id` = ?";
			 pstmt = con.prepareStatement(updateQuery);
			pstmt.setString(1,response);
			pstmt.setString(2,status);
			pstmt.setString(3,tmf_response);
			pstmt.setString(4,source_req_id);
			pstmt.setString(5,child_req_id);
			
			
			int execute = pstmt.executeUpdate();
			log.error("requestLogUpdate=Meditor query status::"+execute);
			
			pstmt.close();
			con.close();
			}
		} catch (SQLException e) {
			log.error("exception raised in requestLogUpdate=Meditor::"+e);
			e.printStackTrace();
		}
		finally
		{
			return true;
		}
	}
}
