package hybridserver.Services;
import hybridserver.other;
import java.io.DataOutputStream;
import java.io.File;

public class HTTPResponder {
	
	public static void evaluateHTTPRequest(String context, int method, DataOutputStream out) throws Exception {		
		String arguments = other.getArguments(context);
	    
	    if(method == 1) // If it is a GET request
	    {
	    	boolean knownToExist = false;
	    	// Empty argument means root and we should look for indexes...
	    	if( arguments.equals("") ) {
	    		if(new File("index.php").exists()){
	    			arguments = "index.php";
	    			knownToExist = true;
	    		}
	    		else if(new File("index.html").exists()){
	    			arguments = "index.html";
	    			knownToExist = true;
	    		}
	    		else if(new File("default.php").exists()){
	    			arguments = "default.php";
	    			knownToExist = true;
	    		}
	    		else if(new File("default.html").exists()){
	    			arguments = "default.html";
	    			knownToExist = true;
	    		}
	    	}
	    	
	    	// Let's handle the HEADER
	    	if( arguments.equals("live") || arguments.endsWith(".gif") || arguments.endsWith(".jpg") || arguments.endsWith(".png") )
	    		out.writeBytes( other.getHTTPHeader(200, 1) );
	    	else if( arguments.endsWith(".html") || arguments.endsWith(".php") )
	    		out.writeBytes( other.getHTTPHeader(200, 5) );
	    	else if( arguments.endsWith(".pdf") ) // TODO: PDF support still doesn't work
	    		out.writeBytes( other.getHTTPHeader(200, 4) );
	    	else // Assuming that every unidentified file is text/html
	    		out.writeBytes( other.getHTTPHeader(200, 5) ); 
	    	
	    	// Let's now write the actual data
	    	if (arguments.startsWith("relay&")) { // Relaying if it is a relay request
	    		other.log("RELAYED DATA FROM " + arguments.substring(6));
		    	out.writeBytes( other.getHTML(arguments.substring(6)).replace("<body>", "<body><center><p style='font-size:x-large;'>" +
		    			"												THIS SITE IS RELAYED THROUGH THE 'AUTH' SERVER</p></center>")
		    			.replace("</body>", "<center>" + other.getStatusInfo(true) + "</center></body>"));
	    	} 
	    	else if( arguments.equals("live") ){ // Checking if it is a "live" request
	    		byte[] image = other.readFile( new File("last.jpg") );  // TODO: Modify in way that it takes the image off of memory
	    		
	    		if (image.length == 0)
	    			image = other.readFromResource("error.jpg");
	    		else 
	    			out.write(image);	
	    	}
	    	// Writing the file - if it actually is a file and it exists
	    	else if( !arguments.equals("live") && (knownToExist || new File(arguments).exists()) ) {
	    		byte[] file = other.readFile( new File(arguments) );
	    		other.log("[DEBUG] READ " + file.length + " bytes from " + arguments + "... now streaming");
	    		out.write(file);
	    	}
	    	else { // If nothing fits - error page
	    		out.writeBytes("<html><body>Your HybridServer REQUEST is an invalid.<br>Your argument was " + arguments +
	    				"<br><br><br>AUTH HybridServer is capable of handling raw TCP connections and HTTP GET and HEAD requests.<br><br>" +
	    				"Here are the valid HTTP arguments:<br>-&nbsp;live<br>-&nbsp;Files on the filesystem<br>-&nbsp;relay&http://www.anysite.com<br><br><br>" + other.getStatusInfo(true)
	    				+ "</body></html>");
	    	}
	    } 
	    else // If it is a HEAD request
	    	out.writeBytes(other.getHTTPHeader(501, 0)); 
	}
	
	
	
}