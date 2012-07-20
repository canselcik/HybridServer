package hybridserver.Services;
import hybridserver.other;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.xml.bind.DatatypeConverter;

import configuration.runtimeConfiguration;

public class EchoService{

	protected DataOutputStream stream = null;
	public EchoService(DataOutputStream stream) {
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
			runtimeConfiguration.telemetryData = new ByteArrayOutputStream();
			
			other.log("Now waiting for telemetry data encoded as LexicalBSD-Base64");
			send("+ PROCEED");
		}
		else if( msg.equals("stoplive") ){
			isRecv = false;

			if(runtimeConfiguration.telemetryData != null && runtimeConfiguration.telemetryData.size() > 0)
				runtimeConfiguration.lastTelemetry = runtimeConfiguration.telemetryData.toByteArray();
			else
				runtimeConfiguration.lastTelemetry = other.readFromResource("error.jpg");
			
			other.log("Done expecting telemetry data");
			send("+ CONFIRMED " + runtimeConfiguration.lastTelemetry.length);
		}
		else {
			if(!isRecv) {
				other.log(remoteAddr + " said " + msg.replaceAll("\r\n", ""));
				send("+ CONFIRMED " + msg.length());
			}
			else { // Now we are receiving live telemetry data
				try {
					byte[] recvData = DatatypeConverter.parseBase64Binary(msg);
					
					if(runtimeConfiguration.telemetryData != null && recvData != null && recvData.length != 0)
						runtimeConfiguration.telemetryData.write(recvData);
				}
				catch (Exception e) {
					other.log("Error occured while reading telemetry data: " + e.toString());
				}
			}
		}
		return 1; // keep-alive if not specified otherwise
	}

}
