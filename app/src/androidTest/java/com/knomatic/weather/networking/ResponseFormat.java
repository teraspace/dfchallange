package com.knomatic.weather.networking;

public enum ResponseFormat {

	JSON("JSON"),
	XML("XML"),
	HTML("HTML"),
	;
      
    private final String type;  
      
    ResponseFormat(String type)
    {  
        this.type = type;  
    }
    
    public String value()
    {
        return this.type;  
    }
    
    public String toString()
    {
    	return type;
    }
}