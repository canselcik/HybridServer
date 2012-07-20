package configuration;

import java.util.Date;

public class runtime {
	
	// TODO: Add other dorm switches/statuses here
	// TODO: Find a way for the AUTH DORM to connect with the server and recv/transmit realtime communique
	public static byte[] lastTelemetry = null;
	
		
	
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
