package com.knomatic.weather.networking;



import java.util.Map;

import com.knomatic.weather.util.Util;


public class Response {
	
	public boolean success;
	public String code;
	public String message;
	public Object data;
	public String sourceData;
	

	
	public void fill(Map<String, String> fields, String format)
	{
		if (!Util.isEmpty(sourceData) && fields!=null) {
			
			if (ResponseFormat.JSON.value().equals(format)) {

			}
			
		}
	}
}