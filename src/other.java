import java.text.SimpleDateFormat;
import java.util.Calendar;

public class other {
	public static String getTime(){
		Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    
	    return sdf.format(cal.getTime());
	}
	
	public static void log(String s){
		System.out.println(getTime() + " -- " + s);
	}
	
	public static String getHTTPHeader(int return_code, int file_type) {
	    String s = "HTTP/1.0 ";
	    
	    switch (return_code) {
	      case 200:
	        s = s + "200 OK";
	        break;
	      case 400:
	        s = s + "400 Bad Request";
	        break;
	      case 403:
	        s = s + "403 Forbidden";
	        break;
	      case 404:
	        s = s + "404 Not Found";
	        break;
	      case 500:
	        s = s + "500 Internal Server Error";
	        break;
	      case 501:
	        s = s + "501 Not Implemented";
	        break;
	    }

	    s = s + "\r\n";
	    s = s + "Connection: close\r\n";
	    s = s + "Server: AUTH-HTTP-RawTCP-Hybrid\r\n";

	    switch (file_type) {
	      case 0:
	        break;
	      case 1:
	        s = s + "Content-Type: image/jpeg\r\n";
	        break;
	      case 2:
	        s = s + "Content-Type: image/gif\r\n";
	      case 3:
	        s = s + "Content-Type: application/x-zip-compressed\r\n";
	      default:
	        s = s + "Content-Type: text/html\r\n";
	        break;
	    }

	    s = s + "\r\n"; 
	    return s;
	}
	
}
