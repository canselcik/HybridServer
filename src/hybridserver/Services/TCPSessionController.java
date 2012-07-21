package hybridserver.Services;

import hybridserver.other;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int evaluate(String msg, String remoteAddr) throws IOException {
		if(!isAuthenticated){
			// Since we don't have a lot of users and we don't need the user:key pairs to be
			// changed often, we can just embed those into the code
			if(msg.contains(":")){
				String[] pair = msg.split(":");
				
				HashMap d = new HashMap<String, String>();
				d.put("can", "3627909A29C31381A071EC27F7C9CA97726182AED29A7DDD2E54353" +
							"322CFB30ABB9E3A6DF2AC2C20FE23436311D678564D0C8D305930575F60E2D3D048184D79");
				d.put("jordan", "3627909A29C31381A071EC27F7C9CA97726182AED29A7DDD2E54353" +
						"322CFB30ABB9E3A6DF2AC2C20FE23436311D678564D0C8D305930575F60E2D3D048184D79");
				d.put("room", "61F1559B07878560A72E897573621B5EFC34DAA75908F20E0046AF9FB8" +
							"61192425614E7CBCAAF107359FCE7E77B425704CDDC7CA5F523203B986A802E433AF7F");
				
				if(pair.length == 2){
					String hash = other.getSHA512Hash(pair[1]).toUpperCase();
					
					if(d.get(pair[0]).equals(hash)){ // Hash matches
						isAuthenticated = true;	
						
						if(pair[0].equals("room")){ // It is the room logging in
							other.log("ROOM HAS JUST LOGGED IN -- necessary info will be forwarded to it (" + remoteAddr + ")");
							
							runtime.setRoomPipe(stream);
							send("+ ROOM_STATUS_ASSIGNED");
						}
						else{ // It is a user logging in
							other.log(msg.split(":")[0] + " logged in");
							send("+ AUTHENTICATED");
						}
						return 1; // Keep-alive
					}
				}
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
			runtime.broadcast("+ EVENT " + msg.split(" ")[1]);
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
