package hybridserver;

import hybridserver.Services.TCPSessionController;
import hybridserver.Services.HTTPResponder;
import java.io.BufferedReader;
//import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import configuration.runtime;

public class HandleClientComm implements Runnable {
	
	
	private Socket cs = null;
	
	public HandleClientComm(Socket cs){
		this.cs = cs;
	}
	
	private String remoteAddr = null;
	public void run(){
		remoteAddr = cs.getInetAddress().toString();
		other.log("Client connected from " + remoteAddr);
		
		DataOutputStream out = null;
		BufferedReader in = null;
		String msg = null;
		//DataInputStream instr = null;
		
		try
		{		
			out = new DataOutputStream(cs.getOutputStream());
			//instr = new DataInputStream(cs.getInputStream());
			in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
		}
		catch(Exception e)
		{
			other.log("Error occured while getting the outputStream for " + remoteAddr);
			return;
		}
		
		// For RAW TCP communication, we are holding a session
		TCPSessionController tcp = new TCPSessionController(out);
		try {
			while ( (msg=in.readLine()) != null ) {   

				if(tcp.isRecv == false)
					other.log("[DEBUG] RECEIVED MESSAGE: --->" + msg);
				
				// Are we dealing with HTTP? If so, we will disconnect right after the request is processed
				if (msg.toUpperCase().startsWith("GET")){
					other.log("HTTP GET REQUEST FROM " + remoteAddr);
					runtime.incHttpAccess();
			    	HTTPResponder.evaluateHTTPRequest(msg, 1, out); // GET request defined by 1
			    	break;
				}
				else if (msg.toUpperCase().startsWith("HEAD")){
					other.log("HTTP HEAD REQUEST FROM " + remoteAddr);
					runtime.incHttpAccess();
			    	HTTPResponder.evaluateHTTPRequest(msg, 2, out); // HEAD request defined by 2
			        break;
				}
				
				// Or simple TCP?
				runtime.incTcpAccess();
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
