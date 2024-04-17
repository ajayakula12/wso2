package com.uol.handler;

import org.apache.synapse.MessageContext;
import org.apache.synapse.rest.AbstractHandler;
import org.json.JSONObject;

import com.uol.utils.GetApiConfigDetails;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.core.axis2.Axis2Sender;

import java.sql.SQLException;
import java.util.Base64;
import java.util.Map;
public class UOLAuthenticationHandler extends AbstractHandler {

    public boolean handleRequest(MessageContext messageContext) {
    	org.apache.axis2.context.MessageContext axis2MessageContext
        = ((Axis2MessageContext) messageContext).getAxis2MessageContext();
    	 System.out.println("==========handler reqeust===========>");
        try {
            if (authenticate(messageContext)) {
                return true;
            }else
            {
            	
                  axis2MessageContext.setProperty(AuthConstants.HTTP_STATUS_CODE, AuthConstants.SC_FORBIDDEN);
                  axis2MessageContext.setProperty(AuthConstants.NO_ENTITY_BODY, true);
                  messageContext.setProperty(AuthConstants.RESPONSE, AuthConstants.TRUE);
                  messageContext.setTo(null);
                  Axis2Sender.sendBack(messageContext);
                  return false;
            }
        } catch (Exception e) {
        	  System.out.println("==========authHeader ex===========>"+e);
            e.printStackTrace();
        }
        return false;
    }

    public boolean handleResponse(MessageContext messageContext) {
    	System.out.println("==========handler response===========>");
    
        
        
        return true;  
    }

    
    public void handleAuthFailure()
    {
    	
    }
    
    public boolean authenticate(MessageContext synCtx) throws SQLException  {
    	
    	 String[] headers =getAuthorizationHeader(getTransportHeaders(synCtx));
        System.out.println("==========authHeader===========>"+headers);
        String username = headers[0];
        String password = headers[1];
        
       boolean userAuthStatus= new GetApiConfigDetails().validateUserAuthenticationDetails(username, password);
        if (userAuthStatus) {
            return true;
        }
        return false;
    }

    private String[] getAuthorizationHeader(Map headers) {
        
    	
    	System.out.println("============>headers:"+headers);
     // Decoding
        byte[] decodedBytes = Base64.getDecoder().decode((String)headers.get("uol-token"));
        String decodedCredentials = new String(decodedBytes);

        // Splitting into username and password
        String[] credentialsArray = decodedCredentials.split(":");
        
        return credentialsArray;

    }

    private Map getTransportHeaders(MessageContext messageContext) {
        return (Map) ((Axis2MessageContext) messageContext).getAxis2MessageContext().
        getProperty(org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS);
    }
}