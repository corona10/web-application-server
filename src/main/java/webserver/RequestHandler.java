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

import util.ParseUtils;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
	private static final String root = "./webapp"; 
	private static final String[] ContentTypes= {"html/text", "text/css"};
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
			ParseUtils parseutils = new ParseUtils();
			String request = null;
			String url = null;
			
			while(!(request = br.readLine()).equals(""))
			{
				/* 
				 * 정규 표현식으로 Request line 헤더임을 확인하고 맞으면 헤더 형식에 맞춰 파싱한다.
				 */
				//System.out.println(request);
				Pattern pattern = Pattern.compile("^(?:GET|POST|PUT|DELETE)\\s+.*");
				Matcher match = pattern.matcher(request);
				if(match.matches())
				{
					Map<String, String> result = parseutils.parseRequestLine(request);
					url = result.get("URL");
				}
			}
			File file = new File(root + url);
			DataOutputStream dos = new DataOutputStream(out);
			if(file.isFile())
			{
				byte[] body = Files.readAllBytes(new File(root + url).toPath());
				String ext = parseutils.getExt(url);
			    // 요청 URL이 css파일인 경우 text/css로 보내준다.
			    if(ext.equals("css"))
			    {
			        response200Header(dos, body.length, "text/css");
				}else{
					response200Header(dos, body.length, "text/html");			
				}
			    
			    responseBody(dos, body);
			}else{
				 response302Header(dos);
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void response302Header(DataOutputStream dos) {
		// TODO Auto-generated method stub
		try{
			dos.writeBytes("HTTP/1.1 302 Found \r\n");
		    dos.writeBytes("Location: /index.html \r\n");
		    dos.writeBytes("\r\n");
		}catch(IOException e)
		{
			log.error(e.getMessage());
		}
		
	}

	private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String ContentType) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: "+ContentType+";charset=utf-8\r\n");
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
	
	private void response404Header(DataOutputStream dos)
	{
		try{
			dos.writeBytes("HTTP/1.1 404 Not Found \r\n");
		}catch(IOException e)
		{
			log.error(e.getMessage());
		}
	}
}
