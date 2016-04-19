package webserver;

import java.io.DataOutputStream;
import java.io.IOException;

import model.User;
import model.UserDB;

public class App {

  public static void route(DataOutputStream dos, String url,boolean isLogin) {
    response302Header(dos, url, isLogin);
  }

  public synchronized static void InsertDB(String id, User user) {
    if (UserDB.getUser(id) == null) {
      UserDB.addUser(user);
    }
  }

  public synchronized static boolean FindUser(String id, String pw) {
    System.out.println(id);
    System.out.println(pw);
    if (UserDB.getUser(id) != null) {
      User user = (User) UserDB.getUser(id);
      if (user.getPassword().equals(pw)) {
        return true;
      } else {
        return false;
      }
    }
    return false;
  }

  private static void response302Header(DataOutputStream dos, String url, boolean isLogin) {
    // TODO Auto-generated method stub
    try {
      dos.writeBytes("HTTP/1.1 302 Found \r\n");
      if(isLogin)
        dos.writeBytes("Set-Cookie: logined=true\r\n");
      dos.writeBytes("Location: " + url + " \r\n");
      dos.writeBytes("\r\n");
    } catch (IOException e) {

    }
  }

}
