package com.knomatic.weather.networking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestParams {
	public Map<String, Object> data;
	public Map<String, String> headers;
	//public Map<String, String> meta;
	public Map<String, String> ids;
	public List<Object> conditions;
	public List<Object> order;
	//public Number recursive;
	public String method;
	public String className;
	public String methodType;
	public String path;
	public String contentType;
	public String server;
	public boolean showNetworkState = true;
	
	public RequestParams()
	{
		data = new HashMap<String, Object>();
		ids = new HashMap<String, String>();
		conditions = new ArrayList<Object>();
		order = new ArrayList<Object>();
		headers = new HashMap<String, String>();
	}
}
