package com.wasapii.adisoftin.fragments;


import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.wasapii.adisoftin.MapDemoActivity;
import com.wasapii.adisoftin.MyApplication;
import com.wasapii.adisoftin.R;
import com.wasapii.adisoftin.model.MapClusterItem;
import com.wasapii.adisoftin.util.AppGlobalConstants;
import com.wasapii.adisoftin.util.AppSharedPreferences;
import com.wasapii.adisoftin.util.CommonUtilities;
import com.android.volley.VolleyError;

import net.steamcrafted.materialiconlib.MaterialIconView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;

import permissions.dispatcher.NeedsPermission;

public class MapFragment extends Fragment implements OnMapReadyCallback ,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ClusterManager.OnClusterClickListener<MapClusterItem>,
        ClusterManager.OnClusterInfoWindowClickListener<MapClusterItem>,
        ClusterManager.OnClusterItemClickListener<MapClusterItem>,
        ClusterManager.OnClusterItemInfoWindowClickListener<MapClusterItem> {

    private Context ctx;
    private static View view;
    private boolean isViewShown = false;
    LinearLayout my_map;
    Fragment frag;
    GoogleMap mMap;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final int REQUEST_CODE_MAP = 1;
    private LatLng location;
    private GoogleApiClient mGoogleApiClient;
    SupportMapFragment fragment;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */
    private ClusterManager<MapClusterItem> mClusterManager;
    private Marker selectedMarker;
    private MapClusterItem mapClusterItem;
    LocationRenderer locationRenderer;
    private ArrayList<MapClusterItem> mapClusterItemsList = new ArrayList<>();
    private ImageView btn_filter;
    String min = "", max = "";
    String male, female,both,superhot,veryhot,hot;
    int m, f, b,s,vh,h;
    String gender = "male",login_status,users="super hot";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            ctx = getActivity();
            view = inflater.inflate(R.layout.fragment_map, container, false);
            btn_filter = (ImageView) view.findViewById(R.id.btn_filter);
            SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            location = new LatLng(0.0,0.0);

            btn_filter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showFilterDialog();
                }
            });

        } catch (InflateException e) {}
        return view;
    }

    private void showFilterDialog() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.setContentView(R.layout.dialog_map_filter);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final Button btn_superhot = (Button) dialog.findViewById(R.id.btn_superhot);
        final Button btn_veryhot = (Button) dialog.findViewById(R.id.btn_veryhot);
        final Button btn_hot = (Button) dialog.findViewById(R.id.btn_hot);
        Button btn_done = (Button) dialog.findViewById(R.id.btn_ok);
        final TextView tv_start = (TextView) dialog.findViewById(R.id.tv_start);
        final TextView tv_end = (TextView) dialog.findViewById(R.id.tv_end);
        MaterialIconView btnClose = (MaterialIconView) dialog.findViewById(R.id.btn_close);
        final Button btn_male = (Button) dialog.findViewById(R.id.btn_male);
        final Button btn_female = (Button) dialog.findViewById(R.id.btn_female);
        final Button btn_both = (Button) dialog.findViewById(R.id.btn_both);

        CrystalRangeSeekbar rangeSeekbar = (CrystalRangeSeekbar) dialog.findViewById(R.id.rangeSeekbar1);

        rangeSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {

            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                tv_start.setText(String.valueOf(minValue));
                tv_end.setText(String.valueOf(maxValue));
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        btn_male.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                m = v.getId();
                btn_male.setBackgroundResource(R.drawable.rounded_blue_btn);
                btn_male.setTextColor(Color.WHITE);
                btn_female.setBackgroundColor(Color.TRANSPARENT);
                btn_female.setTextColor(Color.BLACK);
                btn_both.setBackgroundColor(Color.TRANSPARENT);
                btn_both.setTextColor(Color.BLACK);
                male = btn_male.getText().toString();
                gender=male;
            }
        });

        btn_female.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                f = v.getId();
                btn_female.setBackgroundResource(R.drawable.rounded_blue_btn);
                btn_female.setTextColor(Color.WHITE);
                btn_male.setBackgroundColor(Color.TRANSPARENT);
                btn_male.setTextColor(Color.BLACK);
                btn_both.setBackgroundColor(Color.TRANSPARENT);
                btn_both.setTextColor(Color.BLACK);
                female = btn_female.getText().toString();
                gender=female;
            }
        });

        btn_both.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                b = v.getId();
                btn_both.setBackgroundResource(R.drawable.rounded_blue_btn);
                btn_both.setTextColor(Color.WHITE);
                btn_male.setBackgroundColor(Color.TRANSPARENT);
                btn_male.setTextColor(Color.BLACK);
                btn_female.setBackgroundColor(Color.TRANSPARENT);
                btn_female.setTextColor(Color.BLACK);
                both = btn_both.getText().toString();
                gender=both;
            }
        });


        btn_superhot.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                s = v.getId();
                btn_superhot.setBackgroundResource(R.drawable.rounded_blue_btn);
                btn_superhot.setTextColor(Color.WHITE);
                btn_veryhot.setBackgroundColor(Color.TRANSPARENT);
                btn_veryhot.setTextColor(Color.BLACK);
                btn_hot.setBackgroundColor(Color.TRANSPARENT);
                btn_hot.setTextColor(Color.BLACK);
                superhot = btn_superhot.getText().toString();
                users=superhot;
            }
        });

        btn_veryhot.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                vh = v.getId();
                btn_veryhot.setBackgroundResource(R.drawable.rounded_blue_btn);
                btn_veryhot.setTextColor(Color.WHITE);
                btn_superhot.setBackgroundColor(Color.TRANSPARENT);
                btn_superhot.setTextColor(Color.BLACK);
                btn_hot.setBackgroundColor(Color.TRANSPARENT);
                btn_hot.setTextColor(Color.BLACK);
                veryhot = btn_veryhot.getText().toString();
                users=veryhot;
            }
        });

        btn_hot.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                h = v.getId();
                btn_hot.setBackgroundResource(R.drawable.rounded_blue_btn);
                btn_hot.setTextColor(Color.WHITE);
                btn_superhot.setBackgroundColor(Color.TRANSPARENT);
                btn_superhot.setTextColor(Color.BLACK);
                btn_veryhot.setBackgroundColor(Color.TRANSPARENT);
                btn_veryhot.setTextColor(Color.BLACK);
                hot = btn_hot.getText().toString();
                users=hot;
            }
        });
// set final value listener
        rangeSeekbar.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                Log.d("CRS=>", String.valueOf(minValue) + " : " + String.valueOf(maxValue));
                min = String.valueOf(minValue);
                max = String.valueOf(maxValue);

            }
        });

        btn_done.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.e("Gender",gender);
                Log.e("Users",users);
                //requestGetNearByUsersByFilter(min, max, gender);
                //gender=null;
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        //mMap.addMarker(new MarkerOptions().position(location));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        mMap.setMinZoomPreference(6.0f);
        mMap.setMaxZoomPreference(13.0f);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(AppSharedPreferences.getLat(getActivity()), AppSharedPreferences.getLng(getActivity())), 16.0f));
        //Clustering
         mClusterManager = new ClusterManager<MapClusterItem>(ctx, mMap);
//        mMap.setOnCameraChangeListener((GoogleMap.OnCameraChangeListener) mClusterManager);
         mMap.setOnCameraIdleListener(mClusterManager);
         mMap.setOnMarkerClickListener(mClusterManager);
         mMap.setOnInfoWindowClickListener(mClusterManager);
         requestGetNearByUsers();
        mClusterManager.cluster();

        locationRenderer = new LocationRenderer(ctx,mMap,mClusterManager);
        mClusterManager.setRenderer(locationRenderer);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);
        //showMarkerSelectedOnMap(mapClusterItem);

        //settings
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
        //mMap.setTrafficEnabled(true);
       // mMap.getUiSettings().setZoomGesturesEnabled(false);

    }

    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            Toast.makeText(getActivity(), "GPS location was found!", Toast.LENGTH_SHORT).show();
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            mMap.animateCamera(cameraUpdate);
        } else {
            Toast.makeText(getActivity(), "Current location was null, enable GPS on emulator!", Toast.LENGTH_SHORT).show();
        }
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }
    /*
        * Called by Location Services if the connection to the location client
        * drops because of an error.
        */
    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(getActivity(), "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(getActivity(), "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * Called by Location Services if the attempt to Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(),
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(),
                    "Sorry. Location services not available to you", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
// Report to the UI that the location was updated
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        MapDemoActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @SuppressWarnings("all")
    @NeedsPermission({android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION})
    void getMyLocation() {
        if (mMap != null) {
            // Now that map has loaded, let's get our location!
            mMap.setMyLocationEnabled(true);
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            connectClient();
        }
    }
    protected void connectClient() {
        // Connect the client.
        if (isGooglePlayServicesAvailable() && mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    private void requestGetNearByUsers() {

        if (!CommonUtilities.isOnline(ctx)) {
            Toast.makeText(ctx, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            return;
        }

        String url = AppGlobalConstants.WEBSERVICE_BASE_URL + "user_serach_map";

        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject parameters = new JSONObject();

        try {

            parameters.put("user_id", AppSharedPreferences.loadUserIDFromPreference(ctx));
            parameters.put("user_lat", AppSharedPreferences.getLat(ctx));
            parameters.put("user_long", AppSharedPreferences.getLng(ctx));

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
//  Receiving parameter "user_id":"39","name":"Lila","password":"Alikkos2017!#",
// "contact_no":"306942475051","gender":"female","dob":"04\/26\/1981",//
// "user_interests":"Music, frenchelection, greekislands, tennis, golf, kardashians, politics, Traveling, Photography, Gaming, Arts, Cinema, rts CinemaLiterature",
// "login_status":"1","email":"m.Hadjiandreou@theodorougroup.com","signup_type":"1",
// "profile_img":"http:\/\/111.118.254.188\/ci\/wasapii\/Wasapii\/UserImg\/Img39_1.png",
// "device_type":"aos","user_lat":"22.7189665","user_long":"75.8821283","user_timestamp":"06\/11\/2017 04:13:16",
// "venu_name":"","venu_lat":"","venu_long":"","distance":"0"

    private void parseResponse(String response) {

        Log.e("wasapii", "response " + response);


        try {

            JSONObject jsonObject = new JSONObject(response);

            int flag = jsonObject.getInt("Flag");
            String errorMessage = jsonObject.has("Message") ? jsonObject.getString("Message") : "";

            if (flag == 1) {

                JSONArray jsonArray = jsonObject.getJSONArray("Userdetail_map");
                Log.e("JSONArrayUserList", jsonArray + "");


                for (int i = 0; i < jsonArray.length(); i++) {
                    Log.e("ArraryLength",jsonArray.length()+"");
                    JSONObject userObject = jsonArray.getJSONObject(i);

                    Log.e("UserObject", userObject + "");
                    String user_id = userObject.getString("user_id");
                    String name = userObject.getString("name");
                    String password = userObject.getString("password");
                    String contact = userObject.getString("contact_no");
                    String gender = userObject.getString("gender");
                    String dob = userObject.getString("dob");
                    String user_interests = userObject.getString("user_interests");
                    String login_status = userObject.getString("login_status");
                    String email = userObject.getString("email");
                    String signup_type = userObject.getString("signup_type");
                    String profile_img = userObject.getString("profile_img");
                    String device_type = userObject.getString("device_type");
                    String user_lat = userObject.getString("user_lat");
                    String user_long = userObject.getString("user_long");
                    String user_timestamp = userObject.getString("user_timestamp");
                    String venu_name = userObject.getString("venu_name");
                    String venu_lat = userObject.getString("venu_lat");
                    String venu_long = userObject.getString("venu_long");
                    String distance = userObject.getString("distance");

                    double user_lt = Double.parseDouble(user_lat);
                    double user_lng = Double.parseDouble(user_long);
                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.heart_on);

//                    mapClusterItem = new MapClusterItem(icon, user_id, name, password,
//                            contact, gender, dob, user_interests, login_status, email, signup_type,
//                            profile_img, device_type, user_lt, user_lng, user_timestamp, venu_name,
//                            venu_lat, venu_long, distance);


//                    for (int c = 0; c < mapClusterItemsList.size(); c++) {
                    if (!AppSharedPreferences.loadUserIDFromPreference(ctx).equalsIgnoreCase(user_id)) {
                        mapClusterItemsList.add(new MapClusterItem(icon, user_id, name, password,
                                contact, gender, dob, user_interests, login_status, email, signup_type,
                                profile_img, device_type, user_lt, user_lng, user_timestamp, venu_name,
                                venu_lat, venu_long, distance));
                        if (mClusterManager != null) {
                            mClusterManager.addItems(mapClusterItemsList);
                            //mClusterManager.cluster();
                        }
                    }

//                } else {
//                //Toast.makeText(ctx, errorMessage, Toast.LENGTH_SHORT).show();
//                Log.e("Error", "ParseMapCluster");
            }

        }} catch (JSONException ee) {
            ee.printStackTrace();
        }
    }




    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                MapDemoActivity.ErrorDialogFragment errorFragment = new MapDemoActivity.ErrorDialogFragment();
                errorFragment.setDialog(errorDialog);
                errorFragment.show(getActivity().getSupportFragmentManager(), "Location Updates");
            }

            return false;
        }
    }

    @Override
    public boolean onClusterClick(Cluster<MapClusterItem> cluster) {

        // Show a toast with some info when the cluster is clicked.
        //String firstName = cluster.getItems().iterator().next().getTitle();
        // Toast.makeText(ctx, cluster.getSize() + " (including " + firstName + ")", Toast.LENGTH_SHORT).show();

        // Zoom in the cluster. Need to create LatLngBounds and including all the cluster items
        // inside of bounds, then animate to center of the bounds.

        // Create the builder to collect all essential cluster items for the bounds.
        LatLngBounds.Builder builder = LatLngBounds.builder();

        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        // Get the LatLngBounds
        LatLngBounds bounds = builder.build();

        // Animate camera to the bounds
        try {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<MapClusterItem> cluster) {

    }

    @Override
    public boolean onClusterItemClick(MapClusterItem mapClusterItem) {

        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(MapClusterItem mapClusterItem) {

    }


    private class LocationRenderer extends DefaultClusterRenderer<MapClusterItem> {
        private final IconGenerator mClusterIconGenerator = new IconGenerator(ctx);

        LocationRenderer(Context context, GoogleMap map, ClusterManager<MapClusterItem> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onClusterRendered(Cluster<MapClusterItem> cluster, Marker marker) {
            super.onClusterRendered(cluster, marker);
        }

        @Override
        protected void onBeforeClusterItemRendered(MapClusterItem item, MarkerOptions markerOptions) {
            //markerOptions.title(item.getName());
            //markerOptions.icon(item.getMarker_icon());
            markerOptions.visible(false);
            super.onBeforeClusterItemRendered(item, markerOptions);

        }

        @Override
        protected void onBeforeClusterRendered(Cluster<MapClusterItem> cluster, MarkerOptions markerOptions) {
            super.onBeforeClusterRendered(cluster, markerOptions);
            Drawable clusterIcon;
            //clusterIcon.setColorFilter(getResources().getColor(android.R.color.holo_orange_light), PorterDuff.Mode.SRC_ATOP);



            //modify padding for one or two digit numbers
            if (cluster.getSize() >= 10) {
                Log.e("clustersize",cluster.getSize()+"");
                clusterIcon = getResources().getDrawable(R.drawable.super_hot2);
                mClusterIconGenerator.setBackground(clusterIcon);
                mClusterIconGenerator.setTextAppearance(R.style.iconGenText);
                mClusterIconGenerator.setContentPadding(40, -15, 0, 0);
                //clusterIcon = getResources().getDrawable(R.drawable.super_hot);
            } else if(cluster.getSize() >= 8 ){
                Log.e("clustersize<=8",cluster.getSize()+"");
                mClusterIconGenerator.setContentPadding(50, -15, 0, 0);
                clusterIcon = getResources().getDrawable(R.drawable.very_hot2);
                mClusterIconGenerator.setBackground(clusterIcon);
                mClusterIconGenerator.setTextAppearance(R.style.iconGenText);
            }
            else {
                mClusterIconGenerator.setContentPadding(50, -15, 0, 0);
                clusterIcon = getResources().getDrawable(R.drawable.hot2);
                mClusterIconGenerator.setBackground(clusterIcon);
                mClusterIconGenerator.setTextAppearance(R.style.iconGenText);
            }

            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }
    }



}

