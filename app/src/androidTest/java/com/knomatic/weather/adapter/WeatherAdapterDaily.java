package com.knomatic.weather.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.knomatic.weather.R;
import com.knomatic.weather.WeatherActivity;
import com.knomatic.weather.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by geotech-user on 9/03/16.
 */
public class WeatherAdapterDaily extends BaseAdapter {

    public LayoutInflater inflater = null;
    private ArrayList<HashMap<String, String>> dataList;
    private FragmentActivity activity;
    private String strCurrentWeather;
    public WeatherAdapterDaily( WeatherActivity activity,String strCurrentWeather,String icon, ArrayList<HashMap<String, String>> dataList) {
        // TODO Auto-generated constructor stub
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
        this.dataList = dataList;
        this.strCurrentWeather = strCurrentWeather;
    }
    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DataHolder dataHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.weather_item, null);
            dataHolder = new DataHolder(convertView);
            convertView.setTag(dataHolder);
        } else {
            dataHolder = (DataHolder) convertView.getTag();
        }
        try {


            String lstWeather = dataList.get(position).get("hour"+position);

            String dateStr = getWeatherValue(lstWeather, "time");

            Log.d("WeatherAdpater", "lstWeather " + lstWeather);

            dataHolder.txtDateTime.setText("Day : " +Util.getDate(dateStr));
            dataHolder.txtNow.setText("");
            dataHolder.txtPrecipt.setText("Prep Intensity : " +  getWeatherValue(lstWeather, "precipIntensity"));
            dataHolder.txtProb.setText("Prep Probability : " +  getWeatherValue(lstWeather, "precipProbability"));
            dataHolder.txtReal.setText("Temperature Min : " +  getWeatherValue(lstWeather, "temperatureMin"));
            dataHolder.txtFeel.setText("Temperature Max : " +  getWeatherValue(lstWeather, "temperatureMax"));
            try {
                dataHolder.lblNow.setText(getWeatherValue(lstWeather, "summary"));
            } catch(Exception e){}
        } catch (NullPointerException ne) {
            Log.d("ListPersonaAdapter","errore "+ ne.getLocalizedMessage());
        }
        return convertView;
    }
    private class DataHolder {
        private TextView txtLongitud,txtLatitud,lblNow,txtDateTime,txtNow,txtPrecipt,txtProb,txtReal,txtFeel;

        private LinearLayout layoutItem = null;

        public DataHolder(View convertView) {
            txtDateTime =(TextView)convertView.findViewById(R.id.txtDateTime);
            txtNow=(TextView)convertView.findViewById(R.id.txtNow);
            lblNow=(TextView)convertView.findViewById(R.id.lblNow);
            txtPrecipt=(TextView)convertView.findViewById(R.id.txtPrecipt);
            txtProb=(TextView)convertView.findViewById(R.id.txtProb);
            txtReal=(TextView)convertView.findViewById(R.id.txtReal);
            txtFeel=(TextView)convertView.findViewById(R.id.txtFeel);
            layoutItem = (LinearLayout) convertView.findViewById(R.id.lyCurWeather);
        }
    }
    private String getWeatherValue (String key) {
        String value = null;
        if(strCurrentWeather==null)return key;
        try {
            JSONObject jsonCurrentWeather = new JSONObject(strCurrentWeather);
            value =(String) jsonCurrentWeather.get(key).toString();

        } catch (NullPointerException e){
            Log.e("WeatherActivityFragment", "Null Property not found");
        } catch (JSONException e) {
            Log.e("WeatherActivityFragment", "JSON Exception Property not found");
            e.printStackTrace();
        }
        return value;
    }
    private String getWeatherValue (String json, String key) {
        String value = null;
        // Log.d("WeateherActivity","subJson " + json);
        if(json==null)return key;
        try {
            JSONObject jsonCurrentWeather = new JSONObject(json);
            value =(String) jsonCurrentWeather.get(key).toString();

        } catch (NullPointerException e){
            Log.e("WeatherActivityFragment", "Null Property not found");
        } catch (JSONException e) {
            Log.e("WeatherActivityFragment", "JSON Exception Property not found");
            e.printStackTrace();
        }
        return value;
    }
}
