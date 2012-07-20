package configuration;

import java.io.ByteArrayOutputStream;
import java.util.Date;

public class runtime {
	
	// TODO: Add other dorm switches/statuses here
	public static ByteArrayOutputStream telemetryData = null;
	public static byte[] lastTelemetry = null;
	
		
	
	private static Date init = null;
	private static int tcpAccess = 0;
	private static int httpAccess = 0;
	
	public runtime(){
		init = new Date();
	}
	
	public static String showUptime(){ // in minutes
		Date now = new Date();
		return String.valueOf((now.getTime()-init.getTime())/(60*1000));
	}
	
	public static int getTcpAccess() { return tcpAccess; }
	public static int getHttpAccess() { return httpAccess; }
	
	public static void incTcpAccess() { tcpAccess++; }
	public static void incHttpAccess() { httpAccess++; }
}
