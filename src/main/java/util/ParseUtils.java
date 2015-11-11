package util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseUtils {

    public static Map<String, String> parseRequestLine(String request)
    {
    	/*
    	 * Request line 헤더 구: |method|sp|URL|sp|Version|cr|lf|
    	 */
    	String[] parse = request.split(" ");
        Map parseMap = new HashMap<String, String>();
        parseMap.put("method", parse[0]);
  
        int index = parse[1].indexOf("?");
        String requestPath  = parse[1];
        if(index > 0 )
        {
            requestPath = parse[1].substring(0, index);
            String params = parse[1].substring(index + 1);
        	//System.out.println("<help> :" + params);
            parseMap.put("params", params);
        }
        
        parseMap.put("URL", requestPath);
        parseMap.put("Version", parse[2]);
        return parseMap;
    }
    
    public static String getExt(String fullName)
    {
		int pos = fullName.lastIndexOf(".");
		if(pos > 0)
		{
		   String ext = fullName.substring( pos + 1 );
		   return ext;
		}
		return null;
    }
}
