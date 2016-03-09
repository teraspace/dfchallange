// Decompiled by:       Fernflower v0.8.6
// Date:                28.07.2015 11:55:18
// Copyright:           2008-2012, Stiver
// Home page:           http://www.neshkov.com/ac_decompiler.html

package com.knomatic.weather;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy.Builder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.knomatic.weather.WeatherActivity;
import com.knomatic.weather.interfaces.OnLocationListener;
import com.knomatic.weather.util.App;


import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



@SuppressLint({"NewApi"})
public class GPSActivity  {

    private static final long TWO_MINUTES =  1 * 60 * 1000;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0L;
    private static final long MIN_TIME_BW_UPDATES = 120000L;
    private double altitude;
    private int batteryLevel;
    private float batteryPct;
    private int batteryScale;
    private boolean canGetLocation = false;
    private final Handler handler = new Handler();
    private String imei;
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private final double kph = 3.6D;
    private double latitude;
    private Location bestLocation;
    private Location locationGps;
    private LocationManager locationManager;
    private Location locationNet;
    private double longitude;
    private float speed;

    private String url;
    private Activity mActivity;
    OnLocationListener onLocationListener;
    public GPSActivity(Activity mActivity) {

        Log.d("Oncreate", "onCreate del GPS");
        this.mActivity = mActivity;
        if(VERSION.SDK_INT > 9) {
            StrictMode.setThreadPolicy((new Builder()).permitAll().build());
        }

        Log.d("Oncreate", "onCreate del GPS");
    }
    public void setLocationListener (OnLocationListener onLocationListener){
        this.onLocationListener = onLocationListener;
        initLocation();
        initLocationTimer();
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }


    public double getAltitude() {
        return this.altitude;
    }

    /**
     * Método que obtiene el nivel de batería del Smartphone
     */
    public void getBatteryLevel() {
        Intent intent = mActivity.registerReceiver((BroadcastReceiver) null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        this.batteryLevel = intent.getIntExtra("level", -1);
        this.batteryScale = intent.getIntExtra("scale", -1);
        this.batteryPct = (float)this.batteryLevel / (float)this.batteryScale * 100.0F;
    }



    /**
     * Obtiene la  Latitud actual.
     * @return double
     */
    public double getLatitude() {
        return this.latitude;
    }



    /**
     * Obtiene la Longitud actual
     * @return double
     */
    public double getLongitude() {
        return this.longitude;
    }

    /**
     * Obtiene la velocidad actual reportada por el GPS
     * @return float
     */
    public float getSpeed() {
        return this.speed;
    }

    /**
     * Obtiene el estado de la red
     * @return
     */
    public boolean isOnline() {
        // $FF: Couldn't be decompiled
        return false;
    }

    public IBinder onBind(Intent var1) {
        return null;
    }

    public void onCreate() {

    }

    public void onLocationChanged(Location location) {
    }

    public void onProviderDisabled(String provider) {
    }

    public void onProviderEnabled(String provider) {
    }

    public int onStartCommand(Intent var1, int var2, int var3) {
        Log.d("onStartCommandx", "entro al onStartCommand del service");
        this.getBatteryLevel();

        //this.getLocation();

        initLocationTimer();

        runAsForeground();
        return Service.START_STICKY;
    }
    Timer updateTimer = null;
    public void initLocationTimer() {
        if(updateTimer==null) {
            updateTimer = new Timer();
        }
        updateTimer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                if (onLocationListener!=null && bestLocation != null && bestLocation.getLongitude()!=0D)
                    WeatherActivity.getInstance().runOnUiThread(new Runnable() {
                        public void run() {
                            //If there are stories, add them to the table
                            Toast.makeText(App.getInstance(),
                                    "Posicion" + bestLocation.getLongitude() +
                                            " - " + bestLocation.getLatitude() +
                                            ". Accu " + bestLocation.getAccuracy(), Toast.LENGTH_SHORT).show();
                            updateTimer.purge();
                            updateTimer.cancel();
                            updateTimer = null;
                           onLocationListener.onLocationUpdate(bestLocation);
                        }
                    });

            }
        },0,1000);

    }


    /**
     * Almacena la altitud reportada por el GPS
     * @param altitude
     */
    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    /**
     * Almacena la latitud reportada por el GPS
     * @param latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Almacena la longitud reportada por el GPS
     * @param longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Almacena la velocidad reportada por el GPS
     * @param speed
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    private void runAsForeground(){
        Intent notificationIntent = new Intent(mActivity, WeatherActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(mActivity, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification=new NotificationCompat.Builder(mActivity)
                .setContentText("Geotech Tracking Movil")
                .setContentIntent(pendingIntent).build();


    }
    // Criteria locCriteria;

    protected boolean isBetterLocation(Location location,
                                       Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > 1000;
        boolean isSignificantlyOlder = timeDelta < -1000;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use
        // the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be
            // worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
                .getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and
        // accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate
                && isFromSameProvider) {
            return true;
        }
        return false;
    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null)
            return provider2 == null;
        return provider1.equals(provider2);
    }

    /**
     * Returns the most accurate and timely previously detected location. Where
     * the last result is beyond the specified maximum distance or latency a
     * one-off location update is returned via the {@link LocationListener}
     * specified in {@link }.
     *
     * @param minDistance
     *            Minimum distance before we require a location update.
     * @param minTime
     *            Minimum time required between location updates.
     * @return The most accurate and / or timely previously detected location.
     */
    public Location getLastBestLocation(int minDistance, long minTime) {
        Location bestResult = null;
        float bestAccuracy = Float.MAX_VALUE;
        long bestTime = Long.MIN_VALUE;
        Location location = null;
        // Iterate through all the providers on the system, keeping
        // note of the most accurate result within the acceptable time limit.
        // If no result is found within maxTime, return the newest Location.
        List<String> matchingProviders = locationManager.getProviders(true);
        for (String provider : matchingProviders) {
            try {
                location = locationManager.getLastKnownLocation(provider);
            }catch (SecurityException e){
                Log.e("GPSActivity","Must to allow Location");
            }
            try {
                Log.d("GPSActivity", "wait provider->" + location.getLongitude());
            }catch(Exception e){
                Log.e("GPSActivity","Error " + e.getLocalizedMessage());
            }
            if (location != null) {
                float accuracy = location.getAccuracy();
                long time = location.getTime();
                if ((time > minTime && accuracy < bestAccuracy)) {
                    bestResult = location;
                    bestAccuracy = accuracy;
                    bestTime = time;
                } else if (time < minTime && bestAccuracy == Float.MAX_VALUE
                        && time > bestTime) {
                    bestResult = location;
                    bestTime = time;
                }
            } else {
                location = new Location(LocationManager.GPS_PROVIDER);
                location.setAccuracy(0);
                location.setAltitude(0);
                location.setBearing(0);
                location.setLatitude(0);
                location.setLongitude(0);
                location.setSpeed(0);
                location.setTime(0);
                location.setProvider(LocationManager.GPS_PROVIDER);
                bestResult = location;
                Log.d("GPS","wait provider->" + provider);
                Log.d ("GPS","wait Location is null "+provider);
            }

        }
        Log.d("GPS","wait bestResult->" + bestResult);
        return bestResult;
    }

    private synchronized void updateBestLocation(Location location) {
        if (isBetterLocation(location, bestLocation)) {

            bestLocation = location;
        }
    }
    private Location location = null;
    LocationListener locationListener;
    private boolean networkEnabled = false;
    private boolean gpsEnabled = false;
    protected void initLocation() {

        locationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        String providerName = null;


        locationListener = new LocationListener() {
            public void onLocationChanged(Location loc) {
                updateBestLocation(loc);
            }

            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                if (isSameProvider(provider, LocationManager.GPS_PROVIDER))
                    gpsEnabled = status == LocationProvider.AVAILABLE
                            || status == LocationProvider.TEMPORARILY_UNAVAILABLE;
            }

            public void onProviderEnabled(String provider) {

                if (isSameProvider(provider, LocationManager.GPS_PROVIDER))
                    gpsEnabled = true;
                if (provider != null) {
                    try {
                        locationManager.requestLocationUpdates(provider, 0, 0,
                                locationListener);
                    }catch (SecurityException e){

                    }
                }

            }

            public void onProviderDisabled(String provider) {

                if (isSameProvider(provider, LocationManager.GPS_PROVIDER))
                    gpsEnabled = false;
            }
        };
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showEnableLocationsDialog();
        }

        location = this.getLastBestLocation(0,0);
        updateBestLocation(location);
        if (location != null) {
            try {
                locationManager.requestLocationUpdates(location.getProvider(), 0, 0,
                        locationListener);
            }catch (SecurityException e ){

            }
        }else {
            Log.d("GPS","wait location is null");
        }
    }
    protected void showEnableLocationsDialog() {

        AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
        alert.setTitle("Servicios de Localización deshabilitados");
        alert.setMessage("Activa los Servicios de localización.");
        alert.setPositiveButton("Configuración",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent settingsIntent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mActivity.startActivity(settingsIntent);
                    }
                });
        alert.setNegativeButton("No configurar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
        alert.show();
    }
}
