package com.knomatic.weather.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.util.Log;


/**
 * Esta clase es la encargada de acomodar los parametros para el Request dependiendo de su tipo
 * GET, POST, DELETE, UPDATE.
 * @author Carlos Patiño
 *
 */
public class RestClient {

	private ArrayList <NameValuePair> params;
	private ArrayList <NameValuePair> headers;
	private String strEntity;
	private String url;
	public final static String  METHOD_GET = "GET";
	public final static String  METHOD_GET2 = "GET2"; //Special for forecast.io
	public final static String METHOD_POST = "POST";
	public final static String METHOD_PUT = "PUT";
	public final static String METHOD_DELETE = "DELETE";

	private int responseCode;
	private String message;

	private String response;

	public String getResponse() {
		return response;
	}

	public String getErrorMessage() {
		return message;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public RestClient(String url)
	{
		this.url = url;
		params = new ArrayList<NameValuePair>();
		headers = new ArrayList<NameValuePair>();
	}

	public void AddParam(String name, String value)
	{
		params.add(new BasicNameValuePair(name, value));
	}

	public void AddHeader(String name, String value)
	{
		headers.add(new BasicNameValuePair(name, value));
	}
	public void AddEntity (String name){
		strEntity = name;
	}
	public void Execute(String method) throws Exception
	{
		if (method.equals(METHOD_GET))
		{

			//add parameters
			String combinedParams = "";
			if(!params.isEmpty()){
				combinedParams += "?";
				for(NameValuePair p : params)
				{

					String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(),"UTF-8");
					if(combinedParams.length() > 1)
					{
						combinedParams  +=  "&" + paramString;
					}
					else
					{
						combinedParams += paramString;
					}
				}
			}

			HttpGet request = new HttpGet(url + combinedParams);
			//add headers
			for(NameValuePair h : headers)
			{
				request.addHeader(h.getName(), h.getValue());
			}

			executeRequest(request, url);

		}
		if (method.equals(METHOD_GET2))
		{
			Log.d("Restclient","METHOD_GET2");
			//add parameters
			String combinedParams = "";
			if(!params.isEmpty()){
				//combinedParams += "?";
				for(NameValuePair p : params)
				{

					String paramString = URLEncoder.encode(p.getValue(),"UTF-8");
					if(combinedParams.length() > 1)
					{
						combinedParams  +=  "," + paramString;
					}
					else
					{
						combinedParams += paramString;
					}
				}
			}

			HttpGet request = new HttpGet(url + combinedParams);
			Log.d("RestClient","url " + url + combinedParams);
			//add headers
			for(NameValuePair h : headers)
			{
				request.addHeader(h.getName(), h.getValue());
			}

			executeRequest(request, url);

		}
		if (method.equals(METHOD_POST) )
		{

			HttpPost request = new HttpPost(url);

			//add headers
			for(NameValuePair h : headers)
			{
				request.addHeader(h.getName(), h.getValue());
			}

			if(!params.isEmpty()){
				request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			}

			try{
				if(strEntity.length()>0){
					request.setEntity(new StringEntity(strEntity, HTTP.UTF_8));}
			}catch(NullPointerException e){}

			executeRequest(request, url);
		}
		if (method.equals(METHOD_PUT)){

			HttpPut request = new HttpPut(url);
			//add headers
			for(NameValuePair h : headers)
			{
				request.addHeader(h.getName(), h.getValue());
			}

			if(!params.isEmpty()){
				request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			}

			executeRequest(request, url);
		}

	}

	private void executeRequest(HttpUriRequest request, String url)
	{

		HttpResponse httpResponse;

		HttpParams my_httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(my_httpParams, 2000);
		HttpConnectionParams.setSoTimeout(my_httpParams, 2000);
		DefaultHttpClient client = new DefaultHttpClient(my_httpParams);
		int tries=3;
		
		do{
			tries=tries-1;
			try {
				response = "e";
				httpResponse = client.execute(request);
				// Log.v("Peticion", "" + request.getAllHeaders()[1]);
				responseCode = httpResponse.getStatusLine().getStatusCode();
				message = httpResponse.getStatusLine().getReasonPhrase();

				HttpEntity entity = httpResponse.getEntity();

				if (entity != null) {

					InputStream instream = entity.getContent();
					response = convertStreamToString(instream);

					// Closing the input stream will trigger connection release
					instream.close();
				}
			}catch(ConnectException e){
				response = "e";
				System.out.println("RestClient response1: "+response);
			} catch(ConnectTimeoutException e){
				response = "e";
				System.out.println("RestClient response2: "+response);

			}catch(SocketTimeoutException e){
				response = "e";
				System.out.println("RestClient response3: "+response);

			}
			catch (ClientProtocolException e)  {
				response = "e";
				System.out.println("RestClient response4: "+response);

			} catch (IOException e) {
				response = "e";
				System.out.println("RestClient response5: "+response);


			}catch (Exception e){
				response = "e";
				System.out.println("RestClient response6: "+response + " " + e.getMessage());
			}
			finally{
				if(!response.equals("e"))
					return;

				System.out.println("RestClient Unknown excpetion: "+response + " " + tries);
				response = "Problemas de conexión";
				if (tries==0)
					try {
						org.json.JSONObject obj = new org.json.JSONObject();
						org.json.JSONObject objErrors = new org.json.JSONObject();
						org.json.JSONArray objArray = new org.json.JSONArray();
						org.json.JSONObject objNull = new org.json.JSONObject();
						objErrors.put("message", "No se recibió respuesta del servidor. Revise su conexión a internet o intente nuevamente.");
						objArray.put(0, objErrors);
						obj.put("success", false);
						obj.put("code", "API_REQUEST_SERVICE_FAILED");
						obj.put("message", "No se recibió respuesta del servidor. Revise su conexión a internet o intente nuevamente.");
						obj.put("errors", objArray);
						obj.put("data", objNull);
						response = obj.toString();
						System.out.println("RestClient Errores : "+response);

						throw new Exception( obj.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d ("RestClient","response intentos " + tries);
		}while(tries>0 || response.equals("e"));
		
		client.getConnectionManager().shutdown();

	}


	private static String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}