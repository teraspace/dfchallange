package com.knomatic.weather.networking;


public interface CallBack {
	
	public void onInit();
	public void onFail(Response response);
	public void onSuccess(Response response);
}
