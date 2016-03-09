package com.knomatic.weather;

import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.knomatic.weather.adapter.WeatherAdapter;
import com.knomatic.weather.adapter.WeatherAdapterDaily;
import com.knomatic.weather.interfaces.OnLocationListener;
import com.knomatic.weather.model.Forecast;
import com.knomatic.weather.networking.BackgroundRequest;
import com.knomatic.weather.networking.RestClient;
import com.knomatic.weather.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class WeatherActivity extends AppCompatActivity implements OnLocationListener{
    static WeatherActivity mActivity;
    private GPSActivity gps;
    private String weatherResponse;
    private TextView txtLongitud,txtLatitud,txtTimeZone,txtDateTime,txtNow,txtPrecipt,txtProb,txtReal,txtFeel,txtSummary;
    private String strCurrentWeather;
    private WeatherAdapter weatherAdapter;
    private WeatherAdapterDaily weatherAdapterDaily;
    private ListView listWeather;
    private RadioButton radioDaily,radioHourly;

    public static WeatherActivity getInstance(){
        return mActivity;
    }
    public String getWeatherResponse (){
        return weatherResponse;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mActivity = this;

        gps = new GPSActivity(mActivity);
        gps.setLocationListener(this);
        txtLongitud=(TextView)this.findViewById(R.id.txtLongitud);
        txtLatitud=(TextView)this.findViewById(R.id.txtLatitud);
        txtTimeZone=(TextView)this.findViewById(R.id.txtTimeZone);
        txtDateTime =(TextView)this.findViewById(R.id.txtDateTime);
        txtNow=(TextView)this.findViewById(R.id.txtNow);
        txtPrecipt=(TextView)this.findViewById(R.id.txtPrecipt);
        txtProb=(TextView)this.findViewById(R.id.txtProb);
        txtReal=(TextView)this.findViewById(R.id.txtReal);
        txtFeel=(TextView)this.findViewById(R.id.txtFeel);
         txtSummary=(TextView)this.findViewById(R.id.txtSummary);
        listWeather=(ListView)this.findViewById(R.id.lstWeather);
        radioHourly=(RadioButton)this.findViewById(R.id.radioHourly);
        radioDaily=(RadioButton)this.findViewById(R.id.radioDaily);

        onRefresh();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_weather, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationUpdate(Location location) {
        Log.d("WeatherActivity", "Location update " + location.getLatitude());
        Forecast forecast = new Forecast();
        forecast.setLatitud(Double.toString(location.getLatitude()));
        forecast.setLongitud(Double.toString(location.getLongitude()));

        new BackgroundRequest(RestClient.METHOD_GET2){
            @Override
            protected void onPostExecute(Object result) {
                super.onPostExecute(result);
                strCurrentWeather = result.toString();
                onRefresh();
            }
        }.execute(forecast);
    }
    private void onRefresh() {
        txtLongitud.setText("Longitude : " + getWeatherValue(strCurrentWeather,"longitude"));
        txtLatitud.setText("Latitude : " + getWeatherValue(strCurrentWeather,"latitude"));
        txtTimeZone.setText("TimeZone : " + getWeatherValue(strCurrentWeather,"timezone") + " (" + getWeatherValue(strCurrentWeather,"offset") + ") ");
        String dateStr = getWeatherValue(getWeatherValue(strCurrentWeather,"currently"), "time");
        txtDateTime.setText(Util.getDate(dateStr) + " " + Util.getTime(dateStr) );
        txtNow.setText(getWeatherValue(getWeatherValue(strCurrentWeather,"currently"), "summary"));
        txtPrecipt.setText("Prep Intensity : " + getWeatherValue(getWeatherValue(strCurrentWeather,"currently"), "precipIntensity"));
        txtProb.setText("Prep Probability : " + getWeatherValue(getWeatherValue(strCurrentWeather,"currently"), "precipProbability"));
        txtReal.setText("Prep Probability : " + getWeatherValue(getWeatherValue(strCurrentWeather,"currently"), "temperature"));
        txtFeel.setText("Prep Probability : " + getWeatherValue(getWeatherValue(strCurrentWeather,"currently"), "apparentTemperature"));
        txtSummary.setText("Summary : " + getWeatherValue(getWeatherValue("hourly"), "summary"));
        mActivity = this;
        weatherAdapter = new WeatherAdapter(this,
                                getWeatherValue(getWeatherValue(strCurrentWeather,"hourly"), "summary"),
                                getWeatherValue(getWeatherValue(strCurrentWeather,"hourly"), "icon"),
                                listWeather("hourly"));
        listWeather.setAdapter(weatherAdapter);
        radioHourly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("WeatherActivity", "checked hourly " + isChecked);
                if(isChecked) {
                    txtSummary.setText("Summary : " + getWeatherValue(getWeatherValue(strCurrentWeather, "hourly"), "summary"));

                    weatherAdapter = new WeatherAdapter(mActivity,
                            getWeatherValue(getWeatherValue(strCurrentWeather, "hourly"), "summary"),
                            getWeatherValue(getWeatherValue(strCurrentWeather, "hourly"), "icon"),
                            listWeather("hourly"));
                    Log.d("WeatherActivity", "listWeatherhourly " + listWeather("daily").size());

                    listWeather.setAdapter(weatherAdapter);
                }else {
                    txtSummary.setText("Summary : " + getWeatherValue(getWeatherValue(strCurrentWeather, "daily"), "summary"));
                    Log.d("WeatherActivity", "checked hourly " + isChecked);
                    weatherAdapterDaily = new WeatherAdapterDaily(mActivity,
                            getWeatherValue(getWeatherValue(strCurrentWeather, "daily"), "summary"),
                            getWeatherValue(getWeatherValue(strCurrentWeather, "daily"), "icon"),
                            listWeather("daily"));
                    Log.d("WeatherActivity","listWeatherdaily " + listWeather("daily").size());

                    listWeather.setAdapter(weatherAdapterDaily);
                }
            }
        });

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
    public ArrayList<HashMap<String, String>> listWeather(String freq){
        JSONArray jsonArray= null;
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        HashMap<String,String> itemMap;
        Log.d("WeatherActivity","hour " + getWeatherValue(getWeatherValue(strCurrentWeather,freq), "data"));
        String weatherArray = getWeatherValue(getWeatherValue(strCurrentWeather,freq), "data");
        if(weatherArray==null)return list;
        try {
            jsonArray = new JSONArray(getWeatherValue(getWeatherValue(strCurrentWeather,freq), "data"));
        } catch (JSONException e) {
            //e.printStackTrace();
            Log.e("WeatherActivity","Error "  + e.getLocalizedMessage());
        }
        if(jsonArray==null) return list;

        for(int i=0;i<jsonArray.length();i++){
            itemMap = new HashMap<String,String>();
            try {
                itemMap.put("hour"+i,jsonArray.get(i).toString());
                list.add(itemMap);
            } catch (JSONException e) {
                Log.e("WeatherActivity","Error jsonArray item");
            }
        }

        Log.d("WeatherActivity", "hourxx " + list.get(0).get("hour1"));
        return list;
    }
}
