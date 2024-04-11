package com.wasapii.adisoftin;

import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wasapii.adisoftin.Receiver.ProximityReceiver;
import com.wasapii.adisoftin.Service.DistanceAlert;
import com.wasapii.adisoftin.Service.GPSService;
import com.wasapii.adisoftin.Service.ProximityService;
import com.wasapii.adisoftin.adapter.DrawerAdapter;
import com.wasapii.adisoftin.customview.RoundedCornersTransformation;
import com.wasapii.adisoftin.db.DataHelper;
import com.wasapii.adisoftin.fragments.PeopleFragment;
import com.wasapii.adisoftin.fragments.TabFragment;
import com.wasapii.adisoftin.model.DrawerItem;
import com.wasapii.adisoftin.model.NearUser;
import com.wasapii.adisoftin.model.User;
import com.wasapii.adisoftin.util.AppGlobalConstants;
import com.wasapii.adisoftin.util.AppSharedPreferences;
import com.wasapii.adisoftin.util.CommonUtilities;

import net.steamcrafted.materialiconlib.MaterialIconView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity  {

    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    ListView lst_menu_items;
    ArrayList<DrawerItem> drawerList;
    DrawerAdapter adp;
    private ImageView btn_search;
    private DataHelper dh;
    private Context ctx;
    CircleImageView profile_pic;
    private TextView tv_username;


    //Proximity
    private static final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATE = 1000; // in Milliseconds
    private static final long POINT_RADIUS = 100; // in Meters
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
    //Context ctx;
    double lat;
    double lng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.home_activity);

        /**
         *Setup the DrawerLayout and NavigationView
         */

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff);
        profile_pic = (CircleImageView) findViewById(R.id.profile_pic);
        tv_username = (TextView) findViewById(R.id.tv_username);

        ctx = HomeActivity.this;

        dh = DataHelper.getInstance(ctx);



            /**
             * Lets inflate the very first fragment
             * Here , we are inflating the TabFragment as the first Fragment
             */

            mFragmentManager = getSupportFragmentManager();
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();

            /**
             * Setup click events on the Navigation View Items.
             */

//        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(MenuItem menuItem) {
//                mDrawerLayout.closeDrawers();
//
//
//
//                if (menuItem.getItemId() == R.id.nav_item_sent) {
////                     FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
////                     fragmentTransaction.replace(R.id.containerView,new SentFragment()).commit();
//
//                    startActivity(new Intent(HomeActivity.this,LoginActivity.class));
//
//                }
//
//                if (menuItem.getItemId() == R.id.nav_item_inbox) {
//                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
//                    xfragmentTransaction.replace(R.id.containerView,new TabFragment()).commit();
//                }
//
//                return false;
//            }
//
//        });


            drawerList = new ArrayList<DrawerItem>();

            drawerList.add(new DrawerItem("Me"));
            drawerList.add(new DrawerItem("Settings"));
//        drawerList.add(new DrawerItem("History"));
            drawerList.add(new DrawerItem("About"));
            drawerList.add(new DrawerItem("Privacy Policy"));
            drawerList.add(new DrawerItem("Terms & Conditions"));
            drawerList.add(new DrawerItem("Report a problem"));
            drawerList.add(new DrawerItem("Contact"));
            drawerList.add(new DrawerItem("Log out"));

            lst_menu_items = (ListView) findViewById(R.id.lst_menu_items);

            adp = new DrawerAdapter(HomeActivity.this, drawerList);

            lst_menu_items.setAdapter(adp);

            lst_menu_items.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    switch (position) {
                        case 0:
                            TabFragment.setCurrentPage();
                            break;
                        case 1:
                            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                            startActivity(intent);
                            break;
                        case 2:
                            Intent about = new Intent(HomeActivity.this, AboutActivity.class);
                            startActivity(about);
                            break;
                        case 3:
                            Intent privacy = new Intent(HomeActivity.this, PrivacyPolicyActivity.class);
                            startActivity(privacy);
                            break;
                        case 4:
                            Intent tc = new Intent(HomeActivity.this, TermsandConditionsActivity.class);
                            startActivity(tc);
                            break;

                        case 5:
                            Intent report = new Intent(HomeActivity.this, ReportActivity.class);
                            startActivity(report);
                            break;

                        case 6:
                            Intent contact = new Intent(HomeActivity.this, ContactActivity.class);
                            startActivity(contact);
                            break;
                        case 7:
                            logout();
                            break;
                    }

                    mDrawerLayout.closeDrawer(Gravity.LEFT);

                }
            });


            /**
             * Setup Drawer Toggle of the Toolbar
             */

            android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);

            ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name,
                    R.string.app_name);

            mDrawerLayout.setDrawerListener(mDrawerToggle);

            mDrawerToggle.syncState();

            btn_search = (ImageView) findViewById(R.id.btn_search);

            btn_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                    startActivity(intent);
                }
            });


            if (!CommonUtilities.isGpsEnable(HomeActivity.this)) {
                showSettingsAlert();
            } else {

                Intent intentt = new Intent(HomeActivity.this, GPSService.class);
                startService(intentt);
            }



            User user = dh.getUserInfo();

            if (user != null) {
                tv_username.setText(user.getName());
                Glide.with(ctx)
                        .load(user.getProfileImgUrl())
                        .placeholder(R.drawable.gradiant_background)
                        .error(R.drawable.gradiant_background)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        //.bitmapTransform(new RoundedCornersTransformation( ctx,15,2, RoundedCornersTransformation.CornerType.TOP))
                        .into(profile_pic);
            }
            //Proximity
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATE, MINIMUM_DISTANCECHANGE_FOR_UPDATE, new MyLocationListener());
            lat = AppSharedPreferences.getLat(ctx);
            lng = AppSharedPreferences.getLng(ctx);
            latitudeEditText = Double.toString(lat);
            longitudeEditText = Double.toString(lng);
            populateCoordinatesFromLastKnownLocation();
            saveProximityAlertPoint();

        try {
            Intent t = getIntent();
            if(t.hasExtra("Noti")){
                int x = t.getIntExtra("Noti",0);
                if(x==0){
                    show_venue_dialog();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }



    }





    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(){

        final android.app.AlertDialog.Builder alertDialog ;//= new AlertDialog.Builder(mContext);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertDialog = new android.app.AlertDialog.Builder(ctx, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            alertDialog = new android.app.AlertDialog.Builder(ctx);
        }


        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                ctx.startActivity(intent);
                dialog.cancel();
            }
        });
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();


    }

      /*Function to call when user is alert with proximity*/

    public void show_venue_dialog(){
        try{
            final Dialog dialog = new Dialog(ctx);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
            dialog.setContentView(R.layout.dialog_ask_venue);

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            MaterialIconView btnClose = (MaterialIconView) dialog.findViewById(R.id.btn_cancel);
            final TextView now_at = (TextView) dialog.findViewById(R.id.tv_now_at);
            final TextView yes_ven = (TextView) dialog.findViewById(R.id.txtdialog_yes);
            final TextView no_ven = (TextView) dialog.findViewById(R.id.txtdialog_no);
            requestGetNearByUsers(now_at);
            yes_ven.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(ctx,"OK",Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            });

            no_ven.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent in = new Intent(ctx,ChosePlaceActivity.class);
                    startActivity(in);
                    dialog.dismiss();
                }
            });
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();}catch (Exception e){
            e.printStackTrace();
        }
    }


    private void logout(){

        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to logout ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dh.deleteUserInfo();
                        AppSharedPreferences.putEmail(ctx,"");
                        AppSharedPreferences.putFullName(ctx,"");
                        AppSharedPreferences.putGender(ctx,"");
                        AppSharedPreferences.putDOB(ctx,"");
                        AppSharedPreferences.putEmail(ctx,"");
                        AppSharedPreferences.putPassword(ctx,"");
                        AppSharedPreferences.putMobile(ctx,"");
                        AppSharedPreferences.putProfilePic(ctx,"");
                        AppSharedPreferences.putInterests(ctx,"");
                        requestLogout();
                    }

                })
                .setNegativeButton("No", null)
                .show();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_menu, menu);

        for(int i = 0; i < menu.size(); i++) {

            Drawable drawable = menu.getItem(i).getIcon();

            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
            }
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    private void requestLogout() {

        if (!CommonUtilities.isOnline(ctx)) {
            Toast.makeText(ctx, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            return;
        }

        String url = AppGlobalConstants.WEBSERVICE_BASE_URL + "logout";

        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject parameters = new JSONObject();

        try {
            parameters.put("user_id", AppSharedPreferences.loadUserIDFromPreference(ctx));

            JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    pDialog.dismiss();
                    parseResponse(response.toString());

                }

            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    pDialog.dismiss();

                    NetworkResponse response = error.networkResponse;

                    Log.e("com.adisoft.mls.temp", "satish error response " + response);

                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                        Log.e("mls", "VolleyError TimeoutError error or NoConnectionError");

                    } else if (error instanceof AuthFailureError) {
                        //TODO
                        Log.e("mls", "VolleyError AuthFailureError");
                    } else if (error instanceof ServerError) {
                        Log.e("mls", "VolleyError ServerError");
                        //TODO
                    } else if (error instanceof NetworkError) {
                        Log.e("mls", "VolleyError NetworkError");
                        //TODO
                    } else if (error instanceof ParseError) {
                        Log.e("mls", "VolleyError TParseError");
                        //TODO
                    }

                    if (error instanceof ServerError && response != null) {

                        try {
                            String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                            // Now you can use any deserializer to make sense of data

                            //Log.e("com.adisoft.mls.temp","satish error response "+res);

                            parseResponse(res);

                        } catch (UnsupportedEncodingException e1) {
                            // Couldn't properly decode data to string
                            e1.printStackTrace();
                        }
                    }

                }
            });


            jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                    AppGlobalConstants.WEBSERVICE_TIMEOUT_VALUE_IN_MILLIS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            MyApplication.getInstance().addToRequestQueue(jsonRequest);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void parseResponse(String response) {

        Log.e("Logout", "response " + response);

        try {

            JSONObject jsonObject = new JSONObject(response);

            int flag = jsonObject.getInt("Flag");
            String errorMessage = jsonObject.has("Message") ? jsonObject.getString("Message") : "";

            if (flag == 1) {

                Intent i = new Intent(HomeActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();

            } else {
               // Toast.makeText(ctx, errorMessage, Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    private void saveProximityAlertPoint() {
        Location location = getLastKnownLocation();

        if (location==null) {
            Toast.makeText(this, "No last known location. Aborting...", Toast.LENGTH_LONG).show();
            return;
        }


        //saveCoordinatesInPreferences((float)location.getLatitude(),(float)location.getLongitude());
        saveCoordinatesInPreferences((float)lat,(float)lng);
        if(POINT_RADIUS>=100) {
            addProximityAlert(location.getLatitude(), location.getLongitude());
        }
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
            latitudeEditText.concat(nf.format(location.getLatitude()));
            longitudeEditText.concat(nf.format(location.getLongitude()));
            //latitudeEditText.valueOf(lat);
            //longitudeEditText.valueOf(lng);
            Log.e("Testing Service:  ",latitudeEditText +":"+ longitudeEditText);

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
            //Toast.makeText(ctx,"Distance from Point:"+distance, Toast.LENGTH_LONG).show();
        }

        public void onStatusChanged(String s, int i, Bundle b) {

        }

        public void onProviderDisabled(String s) {

        }

        public void onProviderEnabled(String s) {

        }

    }

    private void requestGetNearByUsers(final TextView textView) {

        if (!CommonUtilities.isOnline(ctx)) {
            Toast.makeText(ctx, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            return;
        }

        String url = AppGlobalConstants.WEBSERVICE_BASE_URL + "single_user_detail";

        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject parameters = new JSONObject();

        try {
            parameters.put("user_id", AppSharedPreferences.loadUserIDFromPreference(ctx));

            JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    pDialog.dismiss();
                    parseVenueResponse(response.toString(),textView);

                }

            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    pDialog.dismiss();

                    NetworkResponse response = error.networkResponse;

                    Log.e("com.adisoft.mls.temp", "satish error response " + response);

                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                        Log.e("mls", "VolleyError TimeoutError error or NoConnectionError");

                    } else if (error instanceof AuthFailureError) {
                        //TODO
                        Log.e("mls", "VolleyError AuthFailureError");
                    } else if (error instanceof ServerError) {
                        Log.e("mls", "VolleyError ServerError");
                        //TODO
                    } else if (error instanceof NetworkError) {
                        Log.e("mls", "VolleyError NetworkError");
                        //TODO
                    } else if (error instanceof ParseError) {
                        Log.e("mls", "VolleyError TParseError");
                        //TODO
                    }

                    if (error instanceof ServerError && response != null) {

                        try {
                            String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                            // Now you can use any deserializer to make sense of data

                            //Log.e("com.adisoft.mls.temp","satish error response "+res);

                            parseVenueResponse(res,textView);

                        } catch (UnsupportedEncodingException e1) {
                            // Couldn't properly decode data to string
                            e1.printStackTrace();
                        }
                    }

                }
            });


            jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                    AppGlobalConstants.WEBSERVICE_TIMEOUT_VALUE_IN_MILLIS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            MyApplication.getInstance().addToRequestQueue(jsonRequest);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void parseVenueResponse(String response,TextView t) {

        Log.e("wasapii", "response " + response);

        try {

            JSONObject jsonObject = new JSONObject(response);

            int flag = jsonObject.getInt("Flag");
            String errorMessage = jsonObject.has("Message") ? jsonObject.getString("Message") : "Invalid email id";

            if (flag == 1) {

                JSONObject userDetail = jsonObject.getJSONObject("UserDetail");
                String v_name = userDetail.getString("venu_name");
                t.setText(v_name);

            } else {
                Toast.makeText(ctx, errorMessage, Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();

    }


}
