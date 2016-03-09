package com.knomatic.weather.model;

/**
 * Created by geotech-user on 8/03/16.
 */
public class Forecast {



    @MyAnnotation(FieldName = "longitud")
    public String longitud;
    @MyAnnotation(FieldName = "latitud")
    public String latitud;
    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

}
