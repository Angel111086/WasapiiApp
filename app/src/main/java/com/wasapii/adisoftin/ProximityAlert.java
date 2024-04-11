package com.wasapii.adisoftin;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

import com.wasapii.adisoftin.Receiver.ProximityReceiver;
import com.wasapii.adisoftin.util.AppSharedPreferences;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

/**
 * Created by root on 26/6/17.
 */

public class ProximityAlert extends Activity {

    private static final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATE = 1000; // in Milliseconds
    private static final long POINT_RADIUS = 1; // in Meters
    private static final long PROX_ALERT_EXPIRATION = -1;
    private static final String POINT_LATITUDE_KEY = "POINT_LATITUDE_KEY";
    private static final String POINT_LONGITUDE_KEY = "POINT_LONGITUDE_KEY";
    private static final String PROX_ALERT_INTENT =
    "com.javacodegeeks.android.lbs.ProximityAlert";
    private static final NumberFormat nf = new DecimalFormat("##.########");
    private LocationManager locationManager;
    private String latitudeEditText;
    private String longitudeEditText;
    private String findCoordinatesButton;
    private String savePointButton;
    Context ctx;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = ProximityAlert.this;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MINIMUM_TIME_BETWEEN_UPDATE,MINIMUM_DISTANCECHANGE_FOR_UPDATE, new MyLocationListener());

        populateCoordinatesFromLastKnownLocation();
        saveProximityAlertPoint();
    }


    private void saveProximityAlertPoint() {
        Location location = getLastKnownLocation();

        if (location==null) {
            Toast.makeText(this, "No last known location. Aborting...", Toast.LENGTH_LONG).show();
            return;
        }

        double lat = AppSharedPreferences.getLat(ctx);
        double lng = AppSharedPreferences.getLng(ctx);
        //saveCoordinatesInPreferences((float)location.getLatitude(),(float)location.getLongitude());
        saveCoordinatesInPreferences((float)lat,(float)lng);

        addProximityAlert(location.getLatitude(), location.getLongitude());

    }

    private void addProximityAlert(double latitude, double longitude) {
        Intent intent = new Intent(PROX_ALERT_INTENT);
        PendingIntent proximityIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        locationManager.addProximityAlert(latitude, longitude,POINT_RADIUS, PROX_ALERT_EXPIRATION, proximityIntent );
        IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT);
        registerReceiver(new ProximityReceiver(), filter);
    }

    private void populateCoordinatesFromLastKnownLocation() {
        Location location = getLastKnownLocation();

        if (location!=null) {
            //latitudeEditText.setText(nf.format(location.getLatitude()));
            //longitudeEditText.setText(nf.format(location.getLongitude()));
            //latitudeEditText.concat(nf.format(location.getLatitude()));
            //longitudeEditText.concat(nf.format(location.getLongitude()));
            latitudeEditText.valueOf(AppSharedPreferences.getLat(ctx));
            longitudeEditText.valueOf(AppSharedPreferences.getLng(ctx));
            Log.e(latitudeEditText +":"+ longitudeEditText,"");

        }

    }
    private void saveCoordinatesInPreferences(float latitude, float longitude) {
        SharedPreferences prefs = this.getSharedPreferences(getClass().getSimpleName(),Context.MODE_PRIVATE);

        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putFloat(POINT_LATITUDE_KEY, latitude);
        prefsEditor.putFloat(POINT_LONGITUDE_KEY, longitude);
        Log.e("SaveProc",latitude+"");
        Log.e("SaveProc",longitude+"");

        prefsEditor.commit();

    }

    private Location retrievelocationFromPreferences() {
      SharedPreferences prefs = this.getSharedPreferences(getClass().getSimpleName(),Context.MODE_PRIVATE);

        Location location = new Location("POINT_LOCATION");
        location.setLatitude(prefs.getFloat(POINT_LATITUDE_KEY, 0));
        location.setLongitude(prefs.getFloat(POINT_LONGITUDE_KEY, 0));
        return location;

    }

    private Location getLastKnownLocation() {
        locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    public class MyLocationListener implements LocationListener {
            public void onLocationChanged(Location location) {

            Location pointLocation = retrievelocationFromPreferences();
            float distance = location.distanceTo(pointLocation);
                Log.e("MyNewDist",distance+"");
            Toast.makeText(ctx,"Distance from Point:"+distance, Toast.LENGTH_LONG).show();
            }

        public void onStatusChanged(String s, int i, Bundle b) {

        }

        public void onProviderDisabled(String s) {

        }

        public void onProviderEnabled(String s) {

        }

    }
}


//    private final String PROX_ALERT = "app.test.PROXIMITY_ALERT";
//    private ProximityReceiver proxReceiver = null;
//    private LocationManager locMgr = null;
//    PendingIntent pIntent1 = null;
//    PendingIntent pIntent2 = null;
//    Context ctx;
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        ctx = ProximityAlert.this;
//        double lat = AppSharedPreferences.getLat(ctx);
//        double lon = AppSharedPreferences.getLng(ctx);
//        //float radius = 5.0f * 1609.0f;
//        float radius = 50.0f;
//        String geo = "geo:"+lat+","+lon;
//        Intent intent = new Intent(PROX_ALERT, Uri.parse(geo));
//        intent.putExtra("message", "Current Location");
//        pIntent1 = PendingIntent.getBroadcast(getApplicationContext(), 0, intent,PendingIntent.FLAG_CANCEL_CURRENT);
//
//        locMgr = (LocationManager) this.getSystemService(LOCATION_SERVICE);
//        locMgr.addProximityAlert(lat, lon, radius, 2000L, pIntent1);
//
////        lat = 38.54;
////        lon = -80.38;
////        lat =
////        lon =
////        geo = "geo:"+lat+","+lon;
////
////        intent = new Intent(PROX_ALERT, Uri.parse(geo));
////        intent.putExtra("message", "Orlando, FL");
////
////        pIntent2 = PendingIntent.getBroadcast(getApplicationContext(), 0, intent,
////                PendingIntent.FLAG_CANCEL_CURRENT);
////
////        locMgr.addProximityAlert(lat, lon, radius, 60000L, pIntent2);
////
//        proxReceiver = new ProximityReceiver();
//
//        IntentFilter iFilter = new IntentFilter(PROX_ALERT);
//        iFilter.addDataScheme("geo");
//
//        registerReceiver(proxReceiver, iFilter);
//    }
//
//    protected void onDestroy() {
//        super.onDestroy();
//        unregisterReceiver(proxReceiver);
//        locMgr.removeProximityAlert(pIntent1);
//        //locMgr.removeProximityAlert(pIntent2);
//    }




