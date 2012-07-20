package hybridserver.Services;
import hybridserver.other;

import java.io.ByteArrayOutputStream;
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
	public int evaluate(String msg, String remoteAddr) throws IOException {
		if( msg.equals("exit") || msg.equals("") ) {
			other.log(remoteAddr + " requested to disconnect");
			return 0;
		}
		else if(msg.equals("status") ){
			other.log(remoteAddr + " asked for server status data");
			stream.writeBytes(other.getStatusInfo(false) + "\r\n");
		}
		else if( msg.equals("startlive") ){
			isRecv = true;
			runtime.telemetryData = new ByteArrayOutputStream();
			
			other.log("Now waiting for telemetry data encoded as LexicalBSD-Base64");
			send("+ PROCEED");
		}
		else if( msg.equals("stoplive") ){
			isRecv = false;

			if(runtime.telemetryData != null && runtime.telemetryData.size() > 0)
				runtime.lastTelemetry = runtime.telemetryData.toByteArray();
			else
				runtime.lastTelemetry = other.readFromResource("error.jpg");
			
			other.log("Done expecting telemetry data");
			send("+ CONFIRMED " + runtime.lastTelemetry.length);
		}
		else {
			if(!isRecv) {
				other.log(remoteAddr + " said " + msg.replaceAll("\r\n", ""));
				send("+ CONFIRMED " + msg.length());
			}
			else { // Now we are receiving live telemetry data
				try {
					byte[] recvData = DatatypeConverter.parseBase64Binary(msg);
					
					if(runtime.telemetryData != null && recvData != null && recvData.length != 0)
						runtime.telemetryData.write(recvData);
				}
				catch (Exception e) {
					other.log("Error occured while reading telemetry data: " + e.toString());
				}
			}
		}
		return 1; // keep-alive if not specified otherwise
	}

}
