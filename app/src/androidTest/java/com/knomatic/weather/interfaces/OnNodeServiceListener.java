package com.knomatic.weather.interfaces;

public interface OnNodeServiceListener {

	public void onNewServiceResponse (String data);
	public void onDriverConfirmed (String data);
}
