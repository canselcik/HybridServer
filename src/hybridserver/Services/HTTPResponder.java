package hybridserver.Services;
import hybridserver.EntryPoint;
import hybridserver.other;
import java.io.DataOutputStream;
import java.io.File;
import java.util.HashMap;

import roomRelated.Room;


public class HTTPResponder {
	
	public static void evaluateHTTPRequest(String context, int method, DataOutputStream out) throws Exception {		
		String arguments = other.getArguments(context);
	    
		if(method != 1){ // If it's not a GET request (tell Not Implemented)
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
		if (arguments.startsWith("live?") || arguments.endsWith(".gif") || arguments.endsWith(".jpg") || arguments.endsWith(".png"))
			out.writeBytes(other.getHTTPHeader(200, 1));
		else // Assuming that every other file is text/html
			out.writeBytes(other.getHTTPHeader(200, 5)); 

		// Let's now write the actual data
		if (arguments.startsWith("relay?")) { // Relaying if it is a relay request
			other.log("RELAYED DATA FROM " + arguments.substring(6));
			out.writeBytes(other.getHTML(arguments.substring(6)).replace("<body>",
							"<body><center><p style='font-size:x-large;'>THIS SITE IS RELAYED THROUGH THE 'AUTH' SERVER</p></center>")
							.replace("</body>", "<center><small>This page is brought to you by HybridServer</small></center></body>"));
		} 
		else if (arguments.startsWith("status?")){
			if(other.authenticate( arguments.replace("status?", "") ) == 1) { // TODO: Add more status stuff
				StringBuffer toDeliver = new StringBuffer();
				
				toDeliver.append("Room Connection Information\r\n");
				toDeliver.append(">>IP:\t\t\t\t\t\t\t\t\t\t\t\t" + Room.roomIP + "\r\n");
				toDeliver.append(">>Connection Alive:\t\t\t\t" + String.valueOf(Room.isRoomConnected).toUpperCase() + "\r\n");
				toDeliver.append(">>Last Contact:\t\t\t\t\t\t" + Room.lastRoomComm + "\r\n\r\n");
				
				toDeliver.append("Mainframe Information\r\n");
				toDeliver.append(">>Hostname:\t\t\t\t\t\t\t" + EntryPoint.host + "\r\n"); 
				toDeliver.append(">>Port:\t\t\t\t\t\t\t\t\t\t\t" + EntryPoint.port + "\r\n");
				toDeliver.append(">>Online since:\t\t\t\t\t\t" + Room.mainframeStartTime + "\r\n");
				toDeliver.append(">>Free OS Memory:\t\t\t\t" + other.getFreeMemory() + "\r\n");
				toDeliver.append(">>Free JVM Memory:\t\t\t" + String.valueOf(Runtime.getRuntime().freeMemory() / 1024) + " kB\r\n\r\n");
				
				toDeliver.append("---------------------------------------------\r\n\r\n");
				
				toDeliver.append("Room Flags\r\n"); 
				toDeliver.append(">>Current population:\t\t\t" + Room.numberOfPeopleInside + "\r\n");
				toDeliver.append(">>Alarm Status:\t\t\t\t\t\t" + String.valueOf(Room.alarmOn).toUpperCase() + "\r\n");
				toDeliver.append(">>Lockdown Status:\t\t\t" + String.valueOf(Room.underLockdown).toUpperCase() + "\r\n");
				
				out.writeBytes(toDeliver.toString());
				other.log("Fetching data for status request");
			}
			else {
				out.writeBytes("Authentication error!");
				other.log("Authentication error for status request");
			}
		}
		else if (arguments.startsWith("broadcast?")) { // TODO: Start logging last door unlock etc. -- maybe keep it in a database
			HashMap<String, String> args = other.getIndividualArguments(arguments.substring(10));
			
			if(args == null){
				other.log("Invalid arguments for the broadcast request");
				out.writeBytes("ARGUMENT_ERROR");
				return;
			}
			
			if(args.containsKey("val") && args.containsKey("auth")){
				if(other.authenticate(args.get("auth")) == 1){ // user authenticated
					String toRelay = args.get("val");
					
					other.log("HTTP data relay request to the room: " + toRelay);
					
					if (Room.broadcast("+ EVENT " + toRelay) == true)
						out.writeBytes("Immediate delivery successful");
					else
						out.writeBytes("Room offline - Scheduled future delivery");
				}
				else{
					other.log("Invalid user/password pair for broadcast request");
					out.writeBytes("Authentication Error");
				}
			}
			else{
				other.log("Invalid arguments for the broadcast request");
				out.writeBytes("ARGUMENT_ERROR");
			}

		} 
		else if (arguments.startsWith("live?")) {
			if(other.authenticate( arguments.replace("live?", "") ) == 1) {
				try {				
					Room.broadcast("+ SEND_FRAME"); // Ordering new frame
					
					Thread.sleep(300);
				}
				catch (Exception e) { other.log("Can't order for a new frame -- sending the last frame instead"); }
				
				if (Room.lastTelemetry == null || Room.lastTelemetry.length <= 0) {
					other.log("Video Telemetry frame doesn't exist -- error frame will be transmitted instead");
					Room.lastTelemetry = other.readFromResource("error.jpg");
				}
				
				out.write(Room.lastTelemetry);
				other.log("Relayed room video telemetry frame through HTTP");
			}
			else
			{
				out.write(other.readFromResource("auth_error.jpg"));
				other.log("Live telemetry authentication failed. Streaming auth_error frame");
			}
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
					+ "However, you have to pass multiple authentication layers and know the exact communication syntax.</body></html>");
		}
	}
	
	
	
}