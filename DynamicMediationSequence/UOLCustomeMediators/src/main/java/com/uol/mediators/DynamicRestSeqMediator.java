package com.uol.mediators;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.core.axis2.Axis2SynapseEnvironment;
import org.apache.synapse.mediators.AbstractMediator;
import org.apache.synapse.mediators.base.SequenceMediator;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uol.constants.DBinfoConstants;
import com.uol.dto.ApiResponseDTO;
import com.uol.utils.GetApiConfigDetails;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DynamicRestSeqMediator extends AbstractMediator {

	private String apiId;
	private String queryType;	
	private String sourceRequestID;
	private JSONObject aggregateResponse;

	
	
	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	public String getSourceRequestID() {
		return sourceRequestID;
	}

	public void setSourceRequestID(String sourceRequestID) {
		this.sourceRequestID = sourceRequestID;
	}

	

	public String getApiId() {
		return apiId;
	}

	public void setApiId(String apiId) {
		this.apiId = apiId;
	}

	public boolean mediate(MessageContext messageContext) {
		try {
			log.info("dynamic rest sequence mediator called::"+messageContext.getFrom());
			log.info("dynamic rest sequence mediator called fields::"+(String) messageContext.getProperty("tmf_fields"));
			log.info(sourceRequestID+"=>dynamic rest sequence mediator called subApiId::"+apiId);		
			
			aggregateResponse = new JSONObject();
			
			
			String tmfParams = (String) messageContext.getProperty("tmf_fields");

			String jsonPayloadToString = JsonUtil.jsonPayloadToString(
					((Axis2MessageContext) messageContext).getAxis2MessageContext());

			ObjectMapper mapper = new ObjectMapper();
			System.out.println("queryType"+queryType);
			
			// has to be learn below
			List<ApiResponseDTO> apiList = new GetApiConfigDetails().getMasterApiConfigDetails(apiId, queryType, tmfParams);
			 log.info("DTOObjectsAjay"+apiList);
			Map<String,String> clientResponse  = new HashMap<>();;
			if (apiList != null) {
				for (ApiResponseDTO apidto : apiList) {
					apidto.setSource_req_id(sourceRequestID);
					apidto.setChild_req_id(UUID.randomUUID().toString());
					apidto.setLogStatus(1);
					String apiInfoObj = mapper.writeValueAsString(apidto);
					
					 log.info("dto"+apidto.getSub_endpoint());
					  // Create a new message context for each iteration
		             Axis2SynapseEnvironment synEnv = (Axis2SynapseEnvironment) messageContext.getEnvironment();
		             org.apache.synapse.MessageContext newContext = synEnv.createMessageContext();
		            // Create a new Synapse message context for sequence invocation
		           // MessageContext sequenceCtx = synEnv.createMessageContext();
		             log.info("dbResponseAjay"+apiInfoObj);
		        
		            
		             JSONObject NBjson=new JSONObject(apiInfoObj);
		             JSONObject dbJson=new JSONObject(jsonPayloadToString);
		             JSONObject combinedJsonObject = new JSONObject();
		             combinedJsonObject.put("dbjson", NBjson);
		             combinedJsonObject.put("NBjson", dbJson);
		             log.info("clientResponseAjay "+clientResponse);
		             messageContext.setProperty("dbRequest", combinedJsonObject.toString());
//		             SequenceMediator sequenceMediator = (SequenceMediator) synEnv.getSynapseConfiguration().getSequence("testSeq");
//		             sequenceMediator.mediate(messageContext);
		             org.apache.synapse.Mediator sequenceMediator = synEnv.getSynapseConfiguration().getSequence("dynamicSBSequence");
		             sequenceMediator.mediate(messageContext);
		             String payload = newContext.getEnvelope().getBody().toString();
		                log.info("Payload Response: " + payload);
					
		              //  newContext.getEnvelope().detach(); 
		                
					
					//call sequence 
					
					//setting property 
					
//					JSONObject jsonResponse = new JSONObject(clientResponse.get("response"));
//					aggregateResponse.put(apidto.getProcess_name(), jsonResponse);

//					if ((Integer.parseInt(clientResponse.get("status"))) > 200 && apidto.getResponseStatus()) {
//						JSONObject jsonObj = new JSONObject();
//						jsonObj.put("status", "Request API CALL FAILED");
//						jsonObj.put("statusCode", clientResponse.get("status"));
//						jsonObj.put("message", "request failed");
//						aggregateResponse.put("Fault", jsonObj);
//						messageContext.setProperty("statusCode", 500);
//						
//						break;
//					}
					messageContext.setProperty("statusCode", 200);
				}
			} else {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("status", "BAD REQUEST");
				jsonObj.put("statusCode", "400");
				jsonObj.put("message", "Empty api config list:" + apiList);
				aggregateResponse.put("Fault", jsonObj);
				messageContext.setProperty("statusCode", 400);
			}
		} catch (Exception e) {
			handleException(e, messageContext);
		} finally {
			messageContext.setProperty("xmlResponse", convertResponse(aggregateResponse));
		}

		return true;
	}

	private void handleException(Exception e, MessageContext messageContext) {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("status", "Internal Server Error:Functional");
		jsonObj.put("statusCode", "500");
		jsonObj.put("message", e.getMessage());
		aggregateResponse.put("Fault", jsonObj);
		messageContext.setProperty("statusCode", 500);
	}

	private Map<String, String> makePostRequest(String sourceRequestID, String endpoint, String apiConfig,
			String inputNBPayload, String params) {
		Map<String, String> clientResObj = new HashMap<>();
		Map<String, String> mapObj = new HashMap<>();
		mapObj.put("ApiConfig", apiConfig);
		mapObj.put("NBPayload", inputNBPayload);
		if (params != null) {
			mapObj.put("Params", params);
		}

		String responseObj = null;
		try (CloseableHttpClient client = HttpClients.createDefault()) {
			String xmlObj = convert(mapObj);

			HttpPost post = new HttpPost(endpoint);
			post.setEntity(new StringEntity(xmlObj, ContentType.APPLICATION_XML));

			HttpResponse response = client.execute(post);
			clientResObj.put("status", Integer.toString(response.getStatusLine().getStatusCode()));

			HttpEntity entity = response.getEntity();
			if (entity != null) {
				responseObj = EntityUtils.toString(entity, "UTF-8");

				if (response.getStatusLine().getStatusCode() != 200) {
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("status", "Target System Error");
					jsonObj.put("statusCode", response.getStatusLine().getStatusCode());
					jsonObj.put("message", responseObj);
					jsonObj.put("endpoint", endpoint);
					responseObj = jsonObj.toString();
				}
			} else {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("status", "Response NULL");
				jsonObj.put("statusCode", response.getStatusLine().getStatusCode());
				jsonObj.put("message", "Didn't get any response");
				jsonObj.put("endpoint", endpoint);
				responseObj = jsonObj.toString();
			}
		} catch (IOException | JSONException e) {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("status", "Internal Server Error:REST CALL");
			jsonObj.put("statusCode", "500");
			jsonObj.put("message", e.getMessage());
			jsonObj.put("endpoint", endpoint);
			responseObj = jsonObj.toString();
		}

		clientResObj.put("response", responseObj);
		return clientResObj;
	}

	public static String convert(Map<String, String> json) throws JSONException {
		String res = "<?xml version=\"1.0\" encoding=\"ISO-8859-15\"?>\n<request>";
		for (Map.Entry<String, String> set : json.entrySet()) {
			JSONObject val = new JSONObject(set.getValue());
			res = res + "<" + set.getKey() + ">" + XML.toString(val) + "</" + set.getKey() + ">";
		}

		return res + "</request>";
	}

	public static String convertResponse(JSONObject json) throws JSONException {
		return XML.toString(json);
	}
}
