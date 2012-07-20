package configuration;

import java.io.DataOutputStream;
import java.util.Date;

public class runtime {
	
	// TODO: Add other dorm switches/statuses here
	/* TODO: Find a way for the AUTH DORM to connect with the server and recv/transmit realtime communique. You need to sustain a 
	 * TCP connection with the room to do so. */
	/* TODO: Make sure the Android client communicates with the server through HTTP effectively. Create all the
	 * necessary interfaces so that we can get all the info */
	
	// STUFF THAT THE USER WILL SEE AND INTERACT WITH
	public static byte[] lastTelemetry = null;
	public static boolean underLockdown = false;
	public static int numberOfPeopleInside = 0;
	
	private static DataOutputStream roomPipe = null; 
	public static void broadcast(String str){
		if(roomPipe == null){
			hybridserver.other.log("Room is offline");
			return;
		}
		
		try {
			roomPipe.writeBytes(str);
		} 
		catch (Exception e) {
			hybridserver.other.log("Error occured while transmitting data to the room");
		}
	}
	public static void setRoomPipe(DataOutputStream str){
		roomPipe = str;
	}
	
	// INTERNAL SERVER STUFF (NOT REALLY THAT NECESSARY)
	private static Date init = null;
	private static int tcpAccess = 0;
	private static int httpAccess = 0;
	
	public runtime(){
		init = new Date();
	}
	
	public static Date now = null;
	public static String showUptime(){ // in minutes
		now = new Date();
		return String.valueOf((now.getTime()-init.getTime())/(60*1000));
	}
	
	public static int getTcpAccess() { return tcpAccess; }
	public static int getHttpAccess() { return httpAccess; }
	
	public static void incTcpAccess() { tcpAccess++; }
	public static void incHttpAccess() { httpAccess++; }
}
