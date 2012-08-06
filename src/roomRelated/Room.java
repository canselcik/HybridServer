package roomRelated;

import hybridserver.other;
import java.io.DataOutputStream;

public class Room {
	
	private static DataOutputStream roomPipe = null; 
	public synchronized static boolean broadcast(String str){
		if(str.startsWith("+ EVENT ") && str.contains("--")){
			str = str.toUpperCase();
			String[] pair = str.substring(8).split("--");
			
			if(pair.length == 2){
				boolean newVal = false;
				boolean validCommand = false;
				if(pair[1].equals("TRUE")){
					newVal = true;
					validCommand = true;
				}
				else if(pair[1].equals("FALSE")){
					newVal = false;
					validCommand = true;
				}

				if(validCommand){
					if(pair[0].equals("ALARM")){
						Room.alarmOn = newVal;
						other.log("ALARM is locally set to " + pair[1]);
					}
					else if(pair[0].equals("LOCKDOWN")){
						Room.underLockdown = newVal;
						other.log("LOCKDOWN is locally set to " + pair[1]);
					}
				}
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
