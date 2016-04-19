package webserver;

import java.io.BufferedReader;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.User;
import util.HeaderParser;
import util.IOUtils;

public class RequestHandler extends Thread {
  private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
  private static final String root = "./webapp";
  private static final String[] ContentTypes = { "html/text", "text/css" };

  private Socket connection;

  public RequestHandler(Socket connectionSocket) {
    this.connection = connectionSocket;
  }

  public void run() {
    log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

    try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
      // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
      InputStreamReader isr = new InputStreamReader(in);
      BufferedReader br = new BufferedReader(isr);
      HeaderParser header = new HeaderParser();
      String request = null;
      String url = null;
      // 헤더 전체 읽기
      while (!(request = br.readLine()).equals("")) {
        /*
         * 정규 표현식으로 Request line 헤더임을 확인하고 맞으면 헤더 형식에 맞춰 파싱한다. 패킷의 첫번째 줄이 아래
         * 형식이면 빈줄이 나올 때까지는 헤더이다.
         */
        // System.out.println(request);
        if (header.isFirstHeader(request)) {
          header.setFirstRequestLine(request);
          url = header.get("URL");
        } else {
          header.setHeader(request);
          System.out.println(request);
        }
      }

      /*
       * 만약에 POST이면 바디도 읽는다 content-length을 참고해서 읽되 '\n'의 1바이트까지 생각해서 +1바이트를
       * 계산해서 읽어온 데이터 Request Body 이다.
       */
      if (header.get("method").equals("POST")) {
        int content_length = Integer.parseInt(header.get("Content-length"));
        String data = IOUtils.readData(br, content_length + 1);
        header.setrequestBody(data);
      }

      if (header != null) {
        File file = new File(root + url);
        DataOutputStream dos = new DataOutputStream(out);
        String route_url = header.get("url");

        if (file.isFile()) {
          byte[] body = Files.readAllBytes(new File(root + url).toPath());
          String ext = IOUtils.getExt(url);
          // 요청 URL이 css파일인 경우 text/css로 보내준다.
          if (ext.equals("css")) {
            response200Header(dos, body.length, "text/css", false);
          } else {
            response200Header(dos, body.length, "text/html", false);
          }

          responseBody(dos, body);
        } else {

          if (route_url.equalsIgnoreCase("/create")) {
            String[] user_info = header.get("params").split("&");
            String id = user_info[0].split("=")[1];
            String pw = user_info[1].split("=")[1];
            String name = user_info[2].split("=")[1];
            String email = user_info[3].split("=")[1];
            User user = new User(id, pw, name, email);
            App.InsertDB(id, user);
            App.route(dos, "/index.html", false);
          } else if (route_url.equalsIgnoreCase("/login")) {
            String[] user_info = header.get("params").split("&");
            System.out.println(header.get("params"));
            String id = user_info[0].split("=")[1];
            String pw = user_info[1].split("=")[1];
            if (App.FindUser(id, pw)) {
              App.route(dos, "/index.html", true);
            }
            App.route(dos, "/index.html", true);
          } else {
            response404Header(dos);
          }
        }
      }
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String ContentType, boolean isLogin) {
    try {
      dos.writeBytes("HTTP/1.1 200 OK \r\n");
      dos.writeBytes("Content-Type: " + ContentType + ";charset=utf-8\r\n");
      if (isLogin)
        dos.writeBytes("Set-Cookie: logined=true\r\n");
      dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
      dos.writeBytes("\r\n");
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  private void responseBody(DataOutputStream dos, byte[] body) {
    try {
      dos.write(body, 0, body.length);
      dos.writeBytes("\r\n");
      dos.flush();
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  private void response404Header(DataOutputStream dos) {
    try {
      dos.writeBytes("HTTP/1.1 404 Not Found \r\n");
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }
}
