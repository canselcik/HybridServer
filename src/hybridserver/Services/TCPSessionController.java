package hybridserver.Services;

import hybridserver.other;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.xml.bind.DatatypeConverter;

import configuration.runtime;

public class TCPSessionController{

	protected DataOutputStream stream = null;
	public TCPSessionController(DataOutputStream stream) {
		this.stream = stream;
	}
	
	public void send(String data) throws IOException{
		stream.writeBytes(data);
	}

	public boolean isRecv = false;
	public boolean isAuthenticated = false;
	public int evaluate(String msg, String remoteAddr) throws IOException {
		if(!isAuthenticated){
			// Since we don't have a lot of users and we don't need the user:key pairs to be
			// changed often, we can just embed those into the code
			if (msg.equals("can:12345") || msg.equals("jordan:12345")){
				isAuthenticated = true;	
				other.log(msg.split(":")[0] + " logged in");
				send("+ AUTHENTICATED");
				
				return 1; // Keep-alive
			}
			
			if(msg.equals("room:kp5g6d")){
				isAuthenticated = true;
				other.log("ROOM HAS JUST LOGGED IN -- necessary info will be forwarded to it (" + remoteAddr + ")");
				
				runtime.setRoomPipe(stream);
				send("+ ROOM_STATUS_ASSIGNED");
				
				return 1;
			}
			
			other.log("FAILED log-in attempt");
			return 0; // Disconnect if the user is still not authenticated at this point
		}
		
		
		if( msg.equals("exit") || msg.equals("") ) {
			other.log(remoteAddr + " requested to disconnect");
			return 0;
		}
		else if ( msg.startsWith("broadcast ")) {  // Broadcast data to the room -- for debugging purposes mostly
			other.log("Data relayed to the room: " + msg.split(" ")[1]);
			runtime.broadcast(msg.split(" ")[1]);
		}
		else if (msg.equals("status") ){
			other.log(remoteAddr + " asked for server status data");
			stream.writeBytes(other.getStatusInfo(false) + "\r\n");
		}
		else if( msg.equals("startlive") ){
			isRecv = true;
			
			other.log("Now waiting for telemetry data encoded as LexicalBSD-Base64");
			send("+ PROCEED");
		}
		else if( msg.equals("stoplive") ){
			isRecv = false;
			
			other.log("Confirmed done with the telemetry data - broadcasting...");
			send("+ CONFIRMED " + runtime.lastTelemetry.length);
		}
		else {
			if(!isRecv) {
				other.log(remoteAddr + " said " + msg.replaceAll("\r\n", ""));
				send("+ CONFIRMED " + msg.length());
			}
			else { // Now we are receiving live telemetry data
				try {
					runtime.lastTelemetry = DatatypeConverter.parseBase64Binary(msg); 
					
					other.log("Telemetry data is decoded - waiting for broadcast notice");
					send("+ CONFIRMED " + msg.length());
				}
				catch (Exception e) {
					other.log("Error occured while reading telemetry data: " + e.toString());
					isRecv = false;
					
					// Now let's get back the error message
					runtime.lastTelemetry = other.readFromResource("error.jpg");
				}
			}
		}
		return 1; // keep-alive if not specified otherwise
	}

}
