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
        parseMap.put("URL", parse[1]);
        parseMap.put("Version", parse[2]);
        return parseMap;
    }
}
