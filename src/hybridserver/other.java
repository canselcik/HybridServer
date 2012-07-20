package hybridserver;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import configuration.runtime;

public class other {
	public static String getArguments(String context){
		int start = 0;
	    int end = 0;
	    
	    for (int a = 0; a < context.length(); a++) {
	    	if (context.charAt(a) == ' ' && start != 0) {
	    		end = a;
	            break;
	        }
	        if (context.charAt(a) == ' ' && start == 0) {
	            start = a;
	        }
	    }
	    
	    return context.substring(start + 2, end);
	}
	
	public static byte[] readFile(File file) {
		InputStream is = null;
		try{ is = new FileInputStream(file); }
		catch (Exception e) { return readFromResource("error.jpg"); }

	    long length = file.length();

	    if (length > Integer.MAX_VALUE) {
	    	try { is.close(); } catch(Exception e) { }
	    	return readFromResource("error.jpg"); // Too large
	    }

	    byte[] bytes = new byte[(int)length];

	    int offset = 0;
	    int numRead = 0;
	    try {
			while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
			    offset += numRead;
			}
		} 
	    catch (Exception e) {
	    	try { is.close(); } catch(Exception ex) { }
	    	return readFromResource("error.jpg");
		}

	    if (offset < bytes.length) {
	    	try { is.close(); } catch(Exception e) { }
	    	return readFromResource("error.jpg");
	    }

	    
	    try {
			is.close();
		} 
	    catch (Exception e) {
	    	return readFromResource("error.jpg");
		}
	    
	    return bytes;
	}
	
	public static byte[] readFromResource(String name) {
		InputStream is = null;
		
		try { is = ClassLoader.getSystemResourceAsStream(name); }
		catch (Exception e) { return new byte[0]; }

		long length = 0;
		
		try {
			length = is.available();
		}
		catch (Exception e){
			return new byte[0];
		}

	    if (length > Integer.MAX_VALUE) {
	        return new byte[0]; // Too large
	    }

	    byte[] bytes = new byte[(int)length];

	    int offset = 0;
	    int numRead = 0;
	    try {
			while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
			    offset += numRead;
			}
		} 
	    catch (Exception e) {
			return new byte[0];
		}

	    if (offset < bytes.length) {
	        return new byte[0];
	    }

	    
	    try {
			is.close();
		} 
	    catch (Exception e) {
			return new byte[0];
		}
	    
	    return bytes;
	}
	
	public static String getTime(){
		Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    
	    return sdf.format(cal.getTime());
	}
	
	public static String getStatusInfo(boolean isHTML){
		String toReturn = "<small>Server Information:<br>HTTP Access:&nbsp;" + String.valueOf(runtime.getHttpAccess()) +
				"<br>TCP Access:&nbsp;" + String.valueOf(runtime.getTcpAccess()) + 
				"<br>Uptime:&nbsp;" + runtime.showUptime() + "&nbsp;minutes</small>";
		
		if(!isHTML)
			toReturn = toReturn.replaceAll("<br>", "\r\n").replaceAll("&nbsp;", " ").replaceAll("<small>", "").replaceAll("</small>", "");
		
		return toReturn;
	}
	
	public static void log(String s) {
		System.out.println(getTime() + " -- " + s);
	}
		
	public static String getHTML(String urlToRead) {
	      URL url;
	      HttpURLConnection conn;
	      BufferedReader rd;
	      String line;
	      String result = "";
	      try {
	         url = new URL(urlToRead);
	         conn = (HttpURLConnection) url.openConnection();
	         conn.setRequestMethod("GET");
	         rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	         
	         while ((line = rd.readLine()) != null) {
	            result += line;
	         }
	         rd.close();
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	      return result;
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
	      case 4:
	    	s = s + "Content-Type: application/pdf\r\n";
	      default:
	        s = s + "Content-Type: text/html\r\n";
	        break;
	    }

	    s = s + "\r\n"; 
	    return s;
	}
	
}
