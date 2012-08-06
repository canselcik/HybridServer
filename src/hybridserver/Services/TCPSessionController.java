package hybridserver.Services;

import hybridserver.other;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.xml.bind.DatatypeConverter;
import roomRelated.Room;

public class TCPSessionController{

	protected DataOutputStream stream = null;
	public TCPSessionController(DataOutputStream stream) {
		this.stream = stream;
	}
	
	public synchronized void send(String data) throws IOException{
		synchronized(stream){
			//try { Thread.sleep(200); } catch (Exception e) { }
			stream.writeBytes(data + "\r\n");
			//stream.flush();
			//try { Thread.sleep(200); } catch (Exception e) { }
		}
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
					Room.setRoomPipe(stream);
					Room.broadcast("+ ROOM_STATUS_ASSIGNED");
					
					// Now we are sending the room flags so that the room can reconcile
					Room.broadcast("+ EVENT ALARM--" + String.valueOf(Room.alarmOn));
					Room.broadcast("+ EVENT LOCKDOWN--" + String.valueOf(Room.underLockdown));
					other.log("Reconciling with the room -> local variables are synced to the room");
					
					Room.lastRoomComm = other.getTime();
					
					isRoomSession = true;
					isAuthenticated = true;
					Room.roomIP = remoteAddr.substring(1);
					return 1; // Keep-Alive but skip future succession
			}
		}
		

		if( msg.equals("exit") || msg.equals("") ) {
			other.log(remoteAddr + " requested to disconnect");
			Room.isRoomConnected = false;
			return 0;
		}
		else if ( msg.startsWith("broadcast ")) {  // Broadcast data to the room -- mostly for debugging purposes
			other.log("Data relayed to the room: " + msg.split(" ")[1]);
						
			if(Room.broadcast("+ EVENT " + msg.split(" ")[1]))
				send("+ DELIVERY_SUCCESSFUL");
			else
				send("+ DELIVERY_ERROR");
		}
		else if (msg.equals("ALIVE_SIGNAL") && isRoomSession){
			Room.broadcast("+ CONFIRMED_SIGNAL");
			Room.lastRoomComm = other.getTime();
		}
		else if( msg.startsWith("livedata") ){
			if(msg.equals("livedata_error")) {
				other.log("Room stated that there was an error. Using the last frame instead");
				return 1; // early keep-alive
			}
			
			other.log("Done receiving live telemetry data encoded as LexicalBSD-Base64");
			
			try {
				Room.lastTelemetry = DatatypeConverter.parseBase64Binary(msg.replaceFirst("livedata", ""));
				Room.broadcast("+ CONFIRMED " + Room.lastTelemetry.length); // sending back the decoded file length but checking it on the otherside
																            // is futile since we are in a hurry
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
