import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

@WebServlet("/set")
public class Set extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
	 @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
		String sessionId = request.getParameter("sessionId");
		String apikey = request.getParameter("apikey");
		String endpoint = request.getParameter("endpoint");
		
		JsonObject configCred = DetectFace.settingMap.get(sessionId) == null?new JsonObject():DetectFace.settingMap.get(sessionId);
		if(apikey != null)
			configCred.addProperty("apikey", apikey);
		if(endpoint != null)
			configCred.addProperty("endpoint", endpoint);

		DetectFace.settingMap.put(sessionId, configCred);
		String output = "SET operation successful";
		
    	response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		out.println(output);
		
		out.close();
    }
}
