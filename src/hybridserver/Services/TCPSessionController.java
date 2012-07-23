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
		stream.writeBytes(data + "\r\n");
	}

	
	public boolean isAuthenticated = false;
	public boolean isRoomSession = false;
	public int evaluate(String msg, String remoteAddr) throws IOException {
		if(!isAuthenticated){
			switch(other.authenticate(msg)){
				case 0: // FAILED
					other.log("FAILED log-in attempt");
					return 0; // Disconnect
				case 1: // USER
					other.log(msg.split(":")[0] + " logged in");
					send("+ AUTHENTICATED");
					isAuthenticated = true;
					return 1; // Keep-Alive but skip future succession
				case 2: // ROOM
					other.log("ROOM HAS JUST LOGGED IN -- necessary info will be forwarded to it");
					runtime.setRoomPipe(stream);
					send("+ ROOM_STATUS_ASSIGNED");
					isRoomSession = true;
					isAuthenticated = true;
					return 1; // Keep-Alive but skip future succession
			}
		}
		

		if( msg.equals("exit") || msg.equals("") ) {
			other.log(remoteAddr + " requested to disconnect");
			return 0;
		}
		else if ( msg.startsWith("broadcast ")) {  // Broadcast data to the room -- mostly for debugging purposes
			other.log("Data relayed to the room: " + msg.split(" ")[1]);
			
			if(runtime.broadcast("+ EVENT " + msg.split(" ")[1]))
				send("+ OK");
			else
				send("+ ERROR");
		}
		else if(msg.equals("ALIVE_SIGNAL") && isRoomSession){
			// TODO: Room is telling us that it is alive. Implement this into the system so that
			// we can see the last communication time with the room
		}
		else if( msg.startsWith("livedata") ){
			if(msg.equals("livedata_error")) {
				other.log("Room stated that there was an error. Using the last frame instead");
				return 1; // keep-alive
			}
			
			other.log("Done receiveing live telemetry data encoded as LexicalBSD-Base64");
			
			try {
				runtime.lastTelemetry = DatatypeConverter.parseBase64Binary(msg.replaceFirst("livedata", ""));
				send("+ CONFIRMED " + runtime.lastTelemetry.length);
				
				other.log("Successfully decoded the telemetry data");
			}
			catch (Exception e) { other.log("Error occured while decoding telemetry data: " + e.toString()); }
		}
		else {
			other.log(remoteAddr + " said " + msg);
			send("+ CONFIRMED " + msg.length());
		}

		return 1; // keep-alive if not specified otherwise
	}

}
