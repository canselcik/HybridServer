package hybridserver.Services;
import hybridserver.other;

import java.io.DataOutputStream;

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
		    	out.writeBytes( other.getHTML(arguments.substring(6)) );
	    	}
	    	else
	    		out.writeBytes("YOU REQUESTED: " + context.substring(start + 2, end));
	    }
	    else
	    	out.writeBytes(other.getHTTPHeader(501, 0)); 
	}
	
}