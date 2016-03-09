package com.knomatic.weather.util;

import android.text.format.DateFormat;
import android.util.Log;

import java.util.Calendar;
import java.util.Locale;

public class Util {

	//================================================================================
	//Constants
	//================================================================================
	
	//================================================================================
	//Tags
	//================================================================================
		
	public static String TAG_NETWORKING_ERROR = "NetworkingError";
	public static String TAG_INPUT_STREAM_READ_ERROR = "NetworkingError_EntityCreation";
	
	
	//================================================================================
	//Utility Functions
	//================================================================================
	
	
	/**
	 * 
	 * This function verify if a string is null or empty
	 * 
	 * @param str The string to be verify 
	 * @return true if string is empty or null, false elsewhere 
	 * 
	 */
	public static boolean isEmpty(String str)
	{
		return !(str!=null && str.length()>0);
	}
	
	/**
	 * 
	 * This function verify if a string is null or empty
	 * 
	 * @param str The string to be verify 
	 * @return true if string is empty or null, false elsewhere 
	 * 
	 */
	public static boolean isEmpty(CharSequence str)
	{
		return !(str!=null && str.length()>0);
	}
	
	/**
	 * 
	 * This function verify if a string is null or empty
	 * 
	 * @param str The string to be verify 
	 * @return true if string is empty or null, false elsewhere 
	 * 
	 */
	public static boolean isEmpty(Object obj)
	{
		return !(obj!=null && !isEmpty(obj.toString()));
	}


	public static String getDate(String time) {
		Calendar cal = Calendar.getInstance(Locale.ENGLISH);
		if(time==null)return "0";
		//Log.d("Util", "time " + time);
		cal.setTimeInMillis(Long.parseLong(time) * 1000L);
		String date = DateFormat.format("dd/MM/yyyy", cal).toString();
		return date;
	}
	public static String getTime(String time) {
		Calendar cal = Calendar.getInstance(Locale.ENGLISH);
		if(time==null)return "0";
		cal.setTimeInMillis(Long.parseLong(time) * 1000L);
		String date = DateFormat.format("hh:mm:ss", cal).toString();
		return date;
	}
	public static String convertDate(String dateInMilliseconds,String dateFormat) {
		if (dateInMilliseconds==null) dateInMilliseconds="0";
		return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds) * 1000L).toString();
	}

}
