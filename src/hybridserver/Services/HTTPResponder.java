package hybridserver.Services;
import hybridserver.other;
import java.io.DataOutputStream;
import java.io.File;

import configuration.runtime;

public class HTTPResponder {
	
	public static void evaluateHTTPRequest(String context, int method, DataOutputStream out) throws Exception {		
		String arguments = other.getArguments(context);
	    
		if(method != 1){ // If it's not a GET request (tell Not implemented)
			out.writeBytes(other.getHTTPHeader(501, 0));
			return;
		}

		boolean knownToExist = false;
		
		if (arguments.equals("")) { // Empty argument means root and we should look for indexes, and if they exist within the FS
			if (new File("index.php").exists()) {
				arguments = "index.php";
				knownToExist = true;
			} else if (new File("index.html").exists()) {
				arguments = "index.html";
				knownToExist = true;
			} else if (new File("default.php").exists()) {
				arguments = "default.php";
				knownToExist = true;
			} else if (new File("default.html").exists()) {
				arguments = "default.html";
				knownToExist = true;
			}
		}

		// Let's handle the HEADER
		if (arguments.equals("live") || arguments.endsWith(".gif") || arguments.endsWith(".jpg") || arguments.endsWith(".png"))
			out.writeBytes(other.getHTTPHeader(200, 1));
		else // Assuming that every other file is text/html
			out.writeBytes(other.getHTTPHeader(200, 5)); 

		// Let's now write the actual data
		if (arguments.startsWith("relay?")) { // Relaying if it is a relay request
			other.log("RELAYED DATA FROM " + arguments.substring(6));
			out.writeBytes(other.getHTML(arguments.substring(6)).replace("<body>",
							"<body><center><p style='font-size:x-large;'>THIS SITE IS RELAYED THROUGH THE 'AUTH' SERVER</p></center>")
							.replace("</body>", "<center>" + other.getStatusInfo(true) + "</center></body>"));
		} 
		else if (arguments.startsWith("broadcast?")) { // TODO: Add authentication here
			String toRelay = arguments.replace("broadcast?", "");
			other.log("HTTP data relay request to the room: " + toRelay);
			
			if (runtime.broadcast("+ EVENT " + toRelay) == true)
				out.writeBytes("OK");
			else
				out.writeBytes("ERROR");
		} 
		else if (arguments.equals("live")) { // TODO: Add authentication
			try {
				runtime.broadcast("+ SEND_FRAME"); // Ordering new frame
				
				Thread.sleep(300); // Waiting for the frame to arrive TODO: To be calibrated
			}
			catch (Exception e) { other.log("Can't order for a new frame -- sending the last frame instead"); }
			
			if (runtime.lastTelemetry == null || runtime.lastTelemetry.length <= 0) {
				other.log("Video Telemetry frame doesn't exist -- error frame will be transmitted instead");
				runtime.lastTelemetry = other.readFromResource("error.jpg");
			}
			
			out.write(runtime.lastTelemetry);
			other.log("Relayed room video telemetry frame through HTTP");
		}
		else if (knownToExist || new File(arguments).exists()) { // Writing the file (if it is there)
			byte[] file = other.readFile(new File(arguments));
			other.log("Read " + file.length + " bytes from " + arguments + "... now streaming");
			
			out.write(file);
		} 
		else { // If nothing fits - error page
			other.log("Streaming the HTTP error page");
			out.writeBytes("<html><body>Your HybridServer REQUEST is invalid.<br>Your argument was "
					+ arguments
					+ "<br><br><br>AUTH HybridServer is capable of handling raw TCP connections and HTTP GET and HEAD requests.<br><br>"
					+ "Here are the valid HTTP arguments:<br>-&nbsp;live<br>-&nbsp;Files on the filesystem<br>-&nbsp;broadcast?argument"
					+ "<br>-&nbsp;relay?http://www.anysite.com<br><br><br>" + other.getStatusInfo(true) + "</body></html>");
		}
	}
	
	
	
}