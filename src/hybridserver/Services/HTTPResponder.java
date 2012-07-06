package hybridserver.Services;
import hybridserver.other;
import java.io.DataOutputStream;
import java.io.File;

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
	    	if(!arguments.startsWith("live"))
	    		out.writeBytes(other.getHTTPHeader(200, 5)); // HTML header
	    	else
	    		out.writeBytes(other.getHTTPHeader(200, 1)); // image header
	    	
	    	
	    	if (arguments.startsWith("relay&")){
	    		other.log("RELAYED DATA FROM " + arguments.substring(6));
		    	out.writeBytes( other.getHTML(arguments.substring(6)).replace("<body>", "<body><center><p style='font-size:x-large;'>" +
		    			"												THIS SITE IS RELAYED THROUGH THE 'AUTH' SERVER</p></center>")
		    			.replace("</body>", "<center>" + other.getStatusInfo(true) + "</center></body>"));
	    	}
	    	else if(arguments.startsWith("live")){
	    		byte[] image = other.readFile( new File("last.jpg") );  // Looks for the newest frame
	    		
	    		if (image.length == 0)
	    			image = other.readFromResource("error.jpg");
	    		else 
	    			out.write(image);	
	    	}
	    	else
	    		out.writeBytes("<html><body>YOU REQUESTED:&nbsp;" + context.substring(start + 2, end) + "<br><br>" + other.getStatusInfo(true)
	    				+ "</body></html>");
	    	
	    }
	    else
	    	out.writeBytes(other.getHTTPHeader(501, 0)); 
	}
	
}