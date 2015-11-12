package util;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HeaderParser {

	String requestBody;
    private Map headerMap;
    
    public HeaderParser()
    {
    	this.headerMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
    }
    
	public void setFirstRequestLine(String request) {
		/*
		 * Request line 헤더 구: |method|sp|URL|sp|Version|cr|lf|
		 */
		String[] parse = request.split(" ");
		this.headerMap.put("method", parse[0]);
		String requestPath = parse[1];

		if (this.headerMap.get("method").equals("GET")) {
			int index = parse[1].indexOf("?");
			if (index > 0) {
				requestPath = parse[1].substring(0, index);
				String params = parse[1].substring(index + 1);
				// System.out.println("<help> :" + params);
				this.headerMap.put("params", params);
			}
		}

		this.headerMap.put("URL", requestPath);
		this.headerMap.put("Version", parse[2]);
	}
	
	public boolean isFirstHeader(String request) {
		Pattern pattern = Pattern.compile("^(?:GET|POST|PUT|DELETE)\\s+.*");
		Matcher match = pattern.matcher(request);
		return match.matches();
	}

	public void setHeader(String request) {
		// TODO Auto-generated method stub
		String[] parseResult = request.split(": ");
		this.headerMap.put(parseResult[0], parseResult[1]);
	}
	
	public String get(String key)
	{
		return (String) headerMap.get(key);
	}
	
	public void setrequestBody(String body)
	{
		this.headerMap.put("params", body);
		this.requestBody = body;
	}
	
}
