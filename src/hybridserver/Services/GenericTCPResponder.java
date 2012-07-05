package hybridserver.Services;
import java.io.DataOutputStream;
import java.io.IOException;


public class GenericTCPResponder {
	
	protected DataOutputStream stream = null;
	public GenericTCPResponder(DataOutputStream stream) {
		this.stream = stream;
	}
	
	public void send(String data) throws IOException{
		stream.writeBytes(data);
	}
	
}
