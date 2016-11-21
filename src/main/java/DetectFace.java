import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

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
	
    protected static Map<String, JsonObject> settingMap = new HashMap<String, JsonObject>();
	
    public String parameters = "{"
    		+ "\"apiKey\":\"\","
    		+ "\"text\":\"https://www.whitehouse.gov/sites/whitehouse.gov/files/images/first-family/44_barack_obama%5B1%5D.jpg\""
    		+ "}";
    
    public static void main(String[] args) {
    	DetectFace detecFaceClass = new DetectFace();
    	 JsonObject params = new JsonParser().parse(detecFaceClass.parameters).getAsJsonObject();
		 System.out.println(detecFaceClass.process(params, params.get("text").getAsString()));
    }
    
	 @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
		String sessionId = request.getParameter("sessionId");
		String imageLink = request.getParameter("text");

		JsonObject cred = settingMap.get(sessionId)==null?new JsonObject():settingMap.get(sessionId);
	
		JsonObject output = process(cred, imageLink);
		
    	response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		
		out.println(output);
		
		out.close();
    }
	
	private JsonObject process(JsonObject cred, String imageLink) {
		
		JsonObject output = new JsonObject();
		
		try {
			VisualRecognition service = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_19);
		    service.setApiKey(cred.get("apikey")==null?"":cred.get("apikey").getAsString()); 
		    service.setEndPoint(cred.get("endpoint")==null?"https://watson-api-explorer.mybluemix.net/visual-recognition/api":cred.get("endpoint").getAsString());
		    
		    VisualRecognitionOptions voptions = new VisualRecognitionOptions.Builder().url(imageLink).build();
		    
			DetectedFaces result = service.detectFaces(voptions).execute();
	        JsonObject rawOutput = new JsonParser().parse(result.toString()).getAsJsonObject();
	        
	        JsonObject face = rawOutput.get("images").getAsJsonArray().get(0).getAsJsonObject().get("faces").getAsJsonArray().get(0).getAsJsonObject();
	        
	        if(face.get("identity") == null)
	        	output.addProperty("name", "Cannot be identified");
	        else
	        	output.addProperty("name", face.get("identity").getAsJsonObject().get("name").getAsString() 
	        			+ " (" + face.get("identity").getAsJsonObject().get("score").getAsNumber() + ")");
	        
	        output.addProperty("gender", face.get("gender").getAsJsonObject().get("gender").getAsString()
	        		+ " (" + face.get("gender").getAsJsonObject().get("score").getAsNumber() + ")");
	        output.addProperty("age", face.get("age").getAsJsonObject().get("min").getAsInt() + "-" + face.get("age").getAsJsonObject().get("max").getAsInt()
	        		+ " (" + face.get("age").getAsJsonObject().get("score").getAsNumber() + ")");
	        
		} catch (Exception e) {
			output.addProperty("err", e.getMessage());
		}
		
		return output;
	}

	private static final long serialVersionUID = 1L;
}
