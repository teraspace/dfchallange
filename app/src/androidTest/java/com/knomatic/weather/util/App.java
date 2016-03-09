package com.knomatic.weather.util;

import android.app.Application;

import android.content.Context;


/**
 * Created by geotech-user on 8/03/16.
 */





public class App extends Application {

    private static Context context;
    private static App instance;
    private String weatherResponse;

    @Override
    public void onCreate() {
        super.onCreate();
        App.context = getApplicationContext();
        instance = this;

    }

    public Context getContext() {
        return context;
    }
    public static App getInstance() {
        return instance;
    }

    public void setWeatherResponse(String weatherResponse){
        this.weatherResponse=weatherResponse;
    }
    public String getWeatherResponse(){
        return this.weatherResponse;
    }
}
