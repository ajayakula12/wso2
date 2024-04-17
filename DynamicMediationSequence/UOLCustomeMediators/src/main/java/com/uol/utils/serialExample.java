package com.uol.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class serialExample {
	
	
	 
	
	
	
	
	
	public static void main(String[] args) {
		try {
			 // API endpoint URL
			 String endpoint = "http://10.10.94.51:8082/ord-nms/api/external/v1/pattern-list?pattern=9609886581&channel=moolee"; // Replace with your endpoint URL

	            // Create HttpClient instance
			  HttpClient client = HttpClients.custom()
	                    .setDefaultRequestConfig(RequestConfig.custom()
	                            .setProxy(new HttpHost("10.10.9.32", 8080)) // Set your proxy host and port
	                            .build())
	                    .build();

	            // Create GET request
				HttpPost post = new HttpPost(endpoint);
				  post.setHeader("username", "Ooredoo");
		            post.setHeader("password", "Oor@ch007");
	            // Execute GET request
				//String jsonBody="{\\\"key1\\\": \\\"value1\\\", \\\"key2\\\": \\\"value2\\\"}";
	            HttpResponse response = client.execute(post);
	          
//	            StringEntity strEntity = new StringEntity(jsonBody, ContentType.APPLICATION_JSON);
//	            post.setEntity(strEntity);

	            // Extract response status code
	            int statusCode = response.getStatusLine().getStatusCode();
	            System.out.println("Response Status Code: " + statusCode);

	            // Extract response body
	            HttpEntity entity = response.getEntity();
	            String responseBody = EntityUtils.toString(entity);
	            System.out.println(responseBody);
	               
	               
	            
		
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	 
}


