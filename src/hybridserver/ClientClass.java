package hybridserver;

import hybridserver.Services.EchoService;
import hybridserver.Services.HTTPResponder;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import configuration.runtimeConfiguration;

public class ClientClass implements Runnable {
	private Socket cs = null;
	
	public ClientClass(Socket cs){
		this.cs = cs;
	}
	
	private String remoteAddr = null;
	public void run(){
		remoteAddr = cs.getInetAddress().toString();
		other.log("Client connected from " + remoteAddr);
		
		DataOutputStream out = null;
		BufferedReader in = null;
		String msg = null;
		
		try
		{		
			out = new DataOutputStream(cs.getOutputStream());
			in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
		}
		catch(Exception e)
		{
			other.log("Error occured while initiating client");
		}
		
		
		// It needs to be here since we are holding a session (unlike HTTP)
		EchoService tcp = new EchoService(out);
		try {
			while ( (msg=in.readLine()) != null ) {   

				other.log("[DEBUG] RECV CONTENT: --->" + msg);
				
				// Are we dealing with HTTP? If so, we will disconnect right after the request is processed
				if (msg.toUpperCase().startsWith("GET")){
					other.log("HTTP GET REQUEST FROM " + remoteAddr);
					runtimeConfiguration.incHttpAccess();
			        HTTPResponder.evaluateHTTPRequest(msg, 1, out);
			        break;
				}
				else if(msg.toUpperCase().startsWith("HEAD")){
					other.log("HTTP HEAD REQUEST FROM " + remoteAddr);
					runtimeConfiguration.incHttpAccess();
			        HTTPResponder.evaluateHTTPRequest(msg, 2, out);
			        break;
				}
				
				runtimeConfiguration.incTcpAccess();
				
				// Or simple TCP?
				if(tcp.evaluate(msg, remoteAddr) == 0) // If we have 0, it means connection should be closed
					break;
			}
		}
		catch (Exception e) {
			other.log("Error occured while reading/evaluating:" + e.toString());
		}
		
		other.log(remoteAddr + " disconnected");
		
		try{
			in.close();
			out.close();
			out.flush();
			cs.close();
			other.log("Cleaned after " + remoteAddr);
		}
		catch(Exception e){
			other.log("Error in client thread");
		}
	}
	
	
}
