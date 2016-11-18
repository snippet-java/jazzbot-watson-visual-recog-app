package com.ibm.sample.jazzbot.app;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.DetectedFaces;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualRecognitionOptions;

@WebServlet("/detectface")
public class DetectFace extends HttpServlet {
    private static final long serialVersionUID = 1L;
	
	 @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
		String sessionId = request.getParameter("sessionId");
		String imageLink = request.getParameter("text");

		JsonObject cred = Set.settingMap.get(sessionId)==null?new JsonObject():Set.settingMap.get(sessionId);
		
		JsonObject output = new JsonObject();
		
		try {
			VisualRecognition service = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);
		    service.setApiKey(cred.get("apikey")==null?"":cred.get("apikey").getAsString()); 
		    service.setEndPoint(cred.get("endpoint")==null?"https://watson-api-explorer.mybluemix.net/visual-recognition/api":cred.get("endpoint").getAsString());
		    
		    VisualRecognitionOptions voptions = new VisualRecognitionOptions.Builder().url(imageLink).build();
		    
			DetectedFaces result = service.detectFaces(voptions).execute();
	        JsonObject rawOutput = new JsonParser().parse(result.toString()).getAsJsonObject();
	        
	        JsonObject face = rawOutput.get("images").getAsJsonArray().get(0).getAsJsonObject().get("faces").getAsJsonArray().get(0).getAsJsonObject();
	        
	        output.addProperty("name", face.get("identity").getAsJsonObject().get("name").getAsString() 
	        		+ " (" + face.get("identity").getAsJsonObject().get("score").getAsNumber() + ")");
	        output.addProperty("gender", face.get("gender").getAsJsonObject().get("gender").getAsString()
	        		+ " (" + face.get("gender").getAsJsonObject().get("score").getAsNumber() + ")");
	        output.addProperty("age", face.get("age").getAsJsonObject().get("min").getAsInt() + "-" + face.get("age").getAsJsonObject().get("max").getAsInt()
	        		+ " (" + face.get("age").getAsJsonObject().get("score").getAsNumber() + ")");
	        
		} catch (Exception e) {
			output.addProperty("err", e.getMessage());
		}
		
    	response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		
		out.println(output);
		
		out.close();
    }
}
