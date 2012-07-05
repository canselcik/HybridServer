import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPResponder {
	public static void evaluateHTTPRequest(String context, int method, DataOutputStream out) throws Exception{		
		int start = 0;
	    int end = 0;
	    
	    for (int a = 0; a < context.length(); a++) {
	    	if (context.charAt(a) == ' ' && start != 0) {
	    		end = a;
	            break;
	        }
	        if (context.charAt(a) == ' ' && start == 0) {
	            start = a;
	        }
	    }
	    
	    String arguments = context.substring(start + 2, end);
	    
	    if(method == 1){
	    	out.writeBytes(other.getHTTPHeader(200, 5));
	    	if(arguments.startsWith("relay&")){
	    		other.log("RELAYED DATA FROM " + arguments.substring(6));
		    	out.writeBytes(getHTML( arguments.substring(6) ).replace("http://www.cselcik.com:120", "http://www.cselcik.com:120/relay&" + arguments.substring(6)));
	    	}
	    	else
	    		out.writeBytes("YOU REQUESTED: " + context.substring(start + 2, end));
	    }
	    else
	    	out.writeBytes(other.getHTTPHeader(501, 0));
	    
	}
	
	public static String getHTML(String urlToRead) {
	      URL url;
	      HttpURLConnection conn;
	      BufferedReader rd;
	      String line;
	      String result = "";
	      try {
	         url = new URL(urlToRead);
	         conn = (HttpURLConnection) url.openConnection();
	         conn.setRequestMethod("GET");
	         rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	         
	         while ((line = rd.readLine()) != null) {
	            result += line;
	         }
	         rd.close();
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	      return result;
	   }
	
	
}