package model;

import java.util.HashMap;
import java.util.Map;

public class Request {

	Request(){
		this._request_info = new HashMap<String, String>();
	}
	
	public void setHost(String host)
	{
		this._request_info.put("host", host);
	}
	
	public void setUserAgent(String uag)
	{
		this._request_info.put("User-Agent", uag);
	}
	
	private Map<String, String> _request_info;
}
