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

	    // This is where we check what is wanted and send it back
	    if(method == 1){
	    	out.writeBytes(other.getHTTPHeader(200, 5));
	    	out.writeBytes("YOU REQUESTED: " + context.substring(start + 2, end));
	    }
	    else
	    	out.writeBytes(other.getHTTPHeader(501, 0));
	    
	}
	
	
}
