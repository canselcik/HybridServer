import java.io.DataOutputStream;
import java.io.IOException;

public class EchoService extends GenericTCPResponder {

	public EchoService(DataOutputStream stream) {
		super(stream);
	}

	public int evaluate(String msg, String remoteAddr) throws IOException {
		if( msg.equals("exit") || msg.equals("") )
		{
			other.log(remoteAddr + " requested to disconnected");
			return 0;
		}
		else {
			other.log(remoteAddr + " said " + msg.replaceAll("\r\n", ""));
			stream.writeBytes("RECV " + msg.replaceAll("\r\n", "") + " at " + other.getTime() + "\r\n");
			return 1;
		}
	}

}
