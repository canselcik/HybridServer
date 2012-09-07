package roomRelated;

import hybridserver.other;
import java.io.DataOutputStream;

public class Room {
	
	private static DataOutputStream roomPipe = null; 
	public synchronized static boolean broadcast(String str){
		if(str.startsWith("+ EVENT ")){
			str = str.toUpperCase();
			String temp = str.substring(8);
			if(temp.equals("ALARM_ON")){
				Room.alarmOn = true;
				other.log("ALARM is locally set to TRUE");
			}
			else if(temp.equals("ALARM_OFF")){
				Room.alarmOn = false;
				other.log("ALARM is locally set to FALSE");
			}
			else if(temp.equals("LOCKDOWN_ON")){
				Room.underLockdown = true;
				other.log("LOCKDOWN is locally set to TRUE");
			}
			else if(temp.equals("LOCKDOWN_OFF")){
				Room.underLockdown = false;
				other.log("LOCKDOWN is locally set to FALSE");
			}
		}
		
		if(roomPipe == null){
			hybridserver.other.log("Room is offline - not transmitted");
			isRoomConnected = false;
			return false;
		}
		
		try {
			 synchronized(roomPipe){
				//try { Thread.sleep(200); } catch (Exception e) { }
				roomPipe.writeBytes(str + "\r\n");
				//roomPipe.flush();
				//try { Thread.sleep(200); } catch (Exception e) { }
			 }
			return true;
		} 
		catch (Exception e) {
			hybridserver.other.log("Error occured while transmitting data to the room");
			return false;
		}
	}
	
	public static boolean isRoomConnected = false;
	public static void setRoomPipe(DataOutputStream str){
		roomPipe = str;
		isRoomConnected = true;
	}
	
	
	// STUFF THAT THE USER WILL SEE AND INTERACT WITH (BUT NOT TRANSMITTED TO THE ROOMSERV)
	public static byte[] lastTelemetry = null;
	public static String lastRoomComm = "NULL";
	public static String roomIP = "NULL";
	public static String mainframeStartTime = "";	
	
	// TODO: Add other dorm switches/statuses here
	public static boolean underLockdown = false;
	public static boolean alarmOn = false;
	public static int numberOfPeopleInside = 0;
}
