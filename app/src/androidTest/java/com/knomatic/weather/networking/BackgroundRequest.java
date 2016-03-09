package com.knomatic.weather.networking;

import android.os.AsyncTask;
import android.util.Log;

import java.lang.reflect.Field;


public class BackgroundRequest extends AsyncTask<Object, Object, Object> {

	public String UrlToRequest="";
	public RequestParams params;
	RestClient client = null;
	String usertoken= "0";
	String method;
	public BackgroundRequest(String method) {
		// TODO Auto-generated constructor stub
		this.method = method;
	}
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		UrlToRequest = "https://api.forecast.io/forecast/05f1e2688fea0d5ffde239442c68ff5f/";
		Log.d(getClass().getName(),"Bonos url " +UrlToRequest);
		client = new RestClient(UrlToRequest);
		//client.AddHeader("Content-type", params.contentType);
		if (this.params==null)
			return;


	}


	@Override
	protected Object doInBackground(Object... params) {
		// TODO Auto-generated method stub


		if(params.length>0){
			try {
				StringBuffer sb = new StringBuffer();
				Class<?> objClass = params[0].getClass();

				Field[] fields = objClass.getFields();
				String name = null;
				String nameField = null;
				Object value = null;
				for(Field field : fields) {
					name = field.getName();
					nameField = field.getAnnotations()[0].toString().split("=")[1].substring(0,field.getAnnotations()[0].toString().split("=")[1].length()-1);
					value = field.get(params[0]);
					if (value==null)continue;
					sb.append(nameField+ " - " +name + ": " + value.toString() + "\n");

					client.AddParam(nameField,(String)value);
				}
			} catch(Exception e) {
				e.printStackTrace();
				//return null;
			}
		}



		try {
			Log.d("BAck","req ");
			client.Execute(method);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return client.getResponse();
	}
	@Override
	protected void onPostExecute(Object result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}



}
