package configuration;

import java.io.DataOutputStream;

public class runtime {
	
	// TODO: Add other dorm switches/statuses here
	/* TODO: Make sure the room communicates with the server through TCP effectively. Create all the 
	 * necessary interfaces so that it is useful. */
	/* TODO: Make sure the Android client communicates with the server through HTTP effectively. Create all the
	 * necessary interfaces so that we can get all the info */
	
	// STUFF THAT THE USER WILL SEE AND INTERACT WITH
	public static byte[] lastTelemetry = null;
	public static boolean underLockdown = false;
	public static int numberOfPeopleInside = 0;
	
	private static DataOutputStream roomPipe = null; 
	public static boolean broadcast(String str){
		if(roomPipe == null){
			hybridserver.other.log("Room is offline - not transmitted");
			return false;
		}
		
		try {
			roomPipe.writeBytes(str + "\r\n");
			return true;
		} 
		catch (Exception e) {
			hybridserver.other.log("Error occured while transmitting data to the room");
			return false;
		}
	}
	public static void setRoomPipe(DataOutputStream str){
		roomPipe = str;
	}
}
