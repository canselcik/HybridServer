package hybridserver;
import java.sql.*;

public class SQLConnection {
	
	private Connection connect = null;
	String hostname = "";
	String username = "", password = "";
	public SQLConnection(String hostname, String username, String password){
		this.username = username;
		this.password = password;
		this.hostname = hostname;
	}
	
	public boolean connect(){
		try { 
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection("jdbc:mysql://" + hostname + "/feedback?"+ "user=" + username + "&password=" + password);
		}
		catch (Exception e) { return false; }
		
		return true;
	}
	
	public String[] execute(String query) throws SQLException{
		if(query == null || query.length() == 0 || connect == null || connect.isClosed())
			return null;
		
		PreparedStatement q = connect.prepareStatement(query);
		ResultSet r = q.executeQuery();
		
		return null; // TODO: Make DB connection work
	}
	
}
