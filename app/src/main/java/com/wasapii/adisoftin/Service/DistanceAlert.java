package com.wasapii.adisoftin.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;



/**
 * Created by root on 26/6/17.
 */

public class DistanceAlert extends Service {

    LocationManager locMan;
    Location curLocation;
    Boolean locationChanged;

    Handler handler = new Handler();

    LocationListener gpsListener = new LocationListener() {
        public void onLocationChanged(Location location) {
             Log.w("GPS", "Started");
            if (curLocation == null) {
                curLocation = location;
                locationChanged = true;
            }

            if (curLocation.getLatitude() == location.getLatitude() && curLocation.getLongitude() == location.getLongitude())
                locationChanged = false;
            else
                locationChanged = true;

            curLocation = location;

            if (locationChanged)
                locMan.removeUpdates(gpsListener);

        }

        public void onProviderDisabled(String provider) {

        }

        public void onProviderEnabled(String provider) {
            // Log.w("GPS", "Location changed", null);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (status == 0)// UnAvailable
            {

            } else if (status == 1)// Trying to Connect
            {

            } else if (status == 2) {// Available

            }
        }

    };

    @Override
    public void onCreate() {
        Toast.makeText(getBaseContext(),"Inside onCreate of Service", Toast.LENGTH_LONG).show();

        Log.e(TAG, "Inside onCreate of Service");
        super.onCreate();

        locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0, gpsListener);
           /*if (locMan.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                   locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER,20000, 1, gpsListener);
           } else {
                   this.startActivity(new Intent("android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS"));
           }
 */
        if (curLocation != null) {
            double lat = curLocation.getLatitude();
            double lng = curLocation.getLongitude();
            Toast.makeText(getBaseContext(),"Lat : " + String.valueOf(lat) + "\n Long : "+ String.valueOf(lng), Toast.LENGTH_LONG).show();

        }
        else
        {
            Toast.makeText(getBaseContext(),"Didn Get any location", Toast.LENGTH_LONG).show();
        }
    }

    final String TAG="LocationService";
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getBaseContext(),"Inside onStartCommand of Service", Toast.LENGTH_LONG).show();
        Log.e(TAG, "Inside onStartCommand of Service");



        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onLowMemory() {
        // TODO Auto-generated method stub
        super.onLowMemory();
    }


    @Override
    public void onStart(Intent i, int startId)
    {
        Toast.makeText(getBaseContext(),"Inside onStart of Service", Toast.LENGTH_LONG).show();
        Log.e(TAG, "Inside onStart of Service");

        handler.postDelayed(GpsFinder,5000);// will start after 5 seconds
    }

    public IBinder onBind(Intent arg0) {
        Log.e(TAG, "Inside onBind of Service");
        return null;
    }
    public Runnable GpsFinder = new Runnable()
    {

        public void run()
        {
            // TODO Auto-generated method stub

            if (locMan.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {

                locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0, gpsListener);
            }
            else
            {
                getApplicationContext().startActivity(new Intent("android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS"));
            }

            if (curLocation != null) {
                double lat = curLocation.getLatitude();
                double lng = curLocation.getLongitude();
                Toast.makeText(getBaseContext(),"Lat : " + String.valueOf(lat) + "\n Long : "+ String.valueOf(lng), Toast.LENGTH_LONG).show();

            }

            handler.postDelayed(GpsFinder,5000);// register again to start after 5 seconds...


        }
    };
}

