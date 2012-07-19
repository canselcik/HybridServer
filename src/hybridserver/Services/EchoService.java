package hybridserver.Services;
import hybridserver.other;

import java.io.DataOutputStream;
import java.io.IOException;

public class EchoService{

	protected DataOutputStream stream = null;
	public EchoService(DataOutputStream stream) {
		this.stream = stream;
	}
	
	public void send(String data) throws IOException{
		stream.writeBytes(data);
	}

	private boolean receiving = false;
	// TODO: Add the image upload through TCP feature
	// Have an indicator for upload start and upload end
	public int evaluate(String msg, String remoteAddr) throws IOException {
		if( msg.equals("exit") || msg.equals("") ) {
			other.log(remoteAddr + " requested to disconnect");
			return 0;
		}
		else if(msg.equals("status")){
			other.log(remoteAddr + " asked for server status data");
			stream.writeBytes(other.getStatusInfo(false) + "\r\n");
			return 1;
		}
		else {
			other.log(remoteAddr + " said " + msg.replaceAll("\r\n", ""));
			stream.writeBytes("RECV " + msg.replaceAll("\r\n", "") + " at " + other.getTime() + "\r\n");
			return 1;
		}
	}

}
