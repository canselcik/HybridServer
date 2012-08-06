package hybridserver;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class other {
	
	public static String getFreeMemory(){
		InputStream is = null;
		BufferedReader reader = null;
		String toReturn = "Only for Linux";
		
		log("Reading memory info upon request");
		
		try{
			is = Runtime.getRuntime().exec("cat /proc/meminfo").getInputStream();
			reader = new BufferedReader(new InputStreamReader(is));
						
			reader.readLine();
			toReturn = reader.readLine(); // getting the second line
			
			log("FreeMem request answer: " + toReturn);
			
			if(!toReturn.contains("MemFree"))
				toReturn = "Only for Linux";
			else
				toReturn = toReturn.replace("MemFree:", "").replace(" ","").replace("kB", " kB");
		}
		catch (Exception e){ log(e.toString()); toReturn = "Only for Linux"; }
		finally{
			if(reader != null)
				try { reader.close(); } catch (Exception e) {}
			if(is!=null)
				try { is.close(); } catch (Exception ea) {}
		}
		
		//String jvm = "";
		//try { jvm = " (" + String.valueOf(Runtime.getRuntime().freeMemory()) + ")"; } catch (Exception eaa) {}
		//return toReturn + jvm;
		return toReturn;
	}
	
	// can=123123&test=123123&ahaha
	public static HashMap<String, String> getIndividualArguments(String args){		
		HashMap<String, String> toReturn = new HashMap<String, String>();
		
		if(!args.contains("&") || !args.contains("="))
			return null;
		
		String[] pairs = args.split("&");
		
		for(int i = 0; i < pairs.length; i++){
			String[] varPairs = pairs[i].split("=");
			
			if(varPairs.length == 2)
				toReturn.put(varPairs[0], varPairs[1]);
		}
	
		return toReturn;
	}
	
	public static int authenticate(String msg) {
		// Since we don't have a lot of users and we don't need the user:key
		// pairs to be changed often, we can just embed those into the code
		if (msg.contains(":")) {
			String[] pair = msg.split(":");

			HashMap<String, String> d = new HashMap<String, String>();
			d.put("can",
					"3627909A29C31381A071EC27F7C9CA97726182AED29A7DDD2E54353"
							+ "322CFB30ABB9E3A6DF2AC2C20FE23436311D678564D0C8D305930575F60E2D3D048184D79");
			d.put("jordan",
					"1F66F6E85031B4B040D0019ECB929CC9945B9A5ABA4A26DDFE4AD84" 
							+ "F5AF08E6F03587C9B27E49EFE8589A8472108A04B868B7142A39FF4D42A9BAA6AA4028684");
			d.put("room",
					"61F1559B07878560A72E897573621B5EFC34DAA75908F20E0046AF9"
							+ "FB861192425614E7CBCAAF107359FCE7E77B425704CDDC7CA5F523203B986A802E433AF7F");

			if (pair.length == 2) {
				String hash = getSHA512Hash(pair[1]).toUpperCase();
				String correctHash = (String)d.get(pair[0]);
				
				if(correctHash == null)
					return 0;
				
				if (correctHash.equals(hash)) { // Hash matches
					if (pair[0].equals("room")) // It is the room logging in
						return 2;
					else  						// It is a user logging in
						return 1;
				}
			}
		}
		return 0; // Disconnect if the user is still not authenticated at this point
	}
	
	public static String getSHA512Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
 
            md.update(input.getBytes());
            byte[] mb = md.digest();
            String out = "";
            for (int i = 0; i < mb.length; i++) {
                byte temp = mb[i];
                String s = Integer.toHexString(new Byte(temp));
                while (s.length() < 2) {
                    s = "0" + s;
                }
                s = s.substring(s.length() - 2);
                out += s;
            }
            return out;
 
        } catch (Exception e) {
            log("Algorithm exception: " + e.toString());
        }
        
        return "ERROR";
	}
	
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

	public static String getTime() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		return sdf.format(cal.getTime());
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
