package webserver;

import java.io.DataOutputStream;
import java.io.IOException;

import model.User;

public class App {

	public static void route(DataOutputStream dos, String url)
	{
		response302Header(dos, url);
	}
	
	public synchronized static void InsertDB(String id, User user)
	{
		WebServer.user_db.put(id, user);
	}
	private static void response302Header(DataOutputStream dos, String url) {
		// TODO Auto-generated method stub
		try {
			dos.writeBytes("HTTP/1.1 302 Found \r\n");
			dos.writeBytes("Location: "+url+" \r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			
		}

	}
	
}
