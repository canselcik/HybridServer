package hybridserver;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class EntryPoint {
	
	
	static ServerSocket server = null;
	public static void main(String[] args) {
		System.out.println("########################################");
		System.out.println("##   AUTH ROOM ADMINISTRATION SUITE   ##");
		System.out.println("##    HTTP / RAW TCP HYBRID SERVER    ##");
		System.out.println("##         Author: Can Selcik         ##");
		System.out.println("########################################\n");
		
		if(args.length != 1){
			System.out.println("USAGE: java -jar hybrid.jar [port]");
			System.exit(-1);
		}
				
		other.log("Initializing the system");
		try {
			server = new ServerSocket(Integer.parseInt(args[0]));
		} 
		catch (IOException e) {
			other.log("Could not bind to port " + args[0]);
			System.exit(-1);
		}
		
		other.log("Server started. Now accepting clients from port " + args[0]);
		
		while (true) {	
			Socket clientSocket = null;
			try {
			    clientSocket = server.accept();
			    
			    Runnable r = new ClientClass(clientSocket);
			    new Thread(r).start();
			}
			catch (IOException e) {
			    System.out.println("Accept failed on port " + args[0]);
			}
		}
	}
	
	
}
