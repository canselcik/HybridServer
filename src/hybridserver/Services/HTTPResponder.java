package hybridserver.Services;
import hybridserver.other;
import java.io.DataOutputStream;
import java.io.File;

public class HTTPResponder {
	
	public static void evaluateHTTPRequest(String context, int method, DataOutputStream out) throws Exception {		
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
	    	if( !arguments.startsWith("live") || arguments.endsWith(".html") || 
	    									arguments.endsWith(".php") || arguments.endsWith(".css") || arguments.endsWith(".js") )
	    		out.writeBytes(other.getHTTPHeader(200, 5)); // HTML header
	    	else
	    		out.writeBytes(other.getHTTPHeader(200, 1)); // image header
	    	
	    	// Streaming a file from HDD
	    	if( arguments.equals("") ) {
	    		if(new File("index.php").exists())
	    			arguments = "index.php";
	    	}
	    	if(new File(arguments).exists()) {
	    		byte[] file = other.readFile( new File(arguments) );
	    		other.log("[DEBUG] READ " + file.length + " from " + arguments + "... now streaming");
	    		out.write(file);
	    	}
	    	
	    	
	    	// Relaying a web page
	    	if (arguments.startsWith("relay&")){
	    		other.log("RELAYED DATA FROM " + arguments.substring(6));
		    	out.writeBytes( other.getHTML(arguments.substring(6)).replace("<body>", "<body><center><p style='font-size:x-large;'>" +
		    			"												THIS SITE IS RELAYED THROUGH THE 'AUTH' SERVER</p></center>")
		    			.replace("</body>", "<center>" + other.getStatusInfo(true) + "</center></body>"));
	    	} // Sending the pre-determined URL (custom)
	    	else if(arguments.startsWith("live")){
	    		byte[] image = other.readFile( new File("last.jpg") );  // Looks for the newest frame
	    		
	    		if (image.length == 0)
	    			image = other.readFromResource("error.jpg");
	    		else 
	    			out.write(image);	
	    	}
	    	else { // Maybe the error page
	    		out.writeBytes("<html><body>Your HybridServer REQUEST:&nbsp;" + context.substring(start + 2, end) + "<br><br>" + other.getStatusInfo(true)
	    				+ "</body></html>");
	    	}
	    }
	    else
	    	out.writeBytes(other.getHTTPHeader(501, 0)); 
	}
	
	
	
}