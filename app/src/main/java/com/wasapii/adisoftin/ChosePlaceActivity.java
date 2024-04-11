package com.wasapii.adisoftin;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
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
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.wasapii.adisoftin.Service.ProximityService;
import com.wasapii.adisoftin.customview.RoundedCornersTransformation;
import com.wasapii.adisoftin.fragments.PeopleFragment;
import com.wasapii.adisoftin.model.NearUser;
import com.wasapii.adisoftin.model.Venue;
import com.wasapii.adisoftin.util.AppGlobalConstants;
import com.wasapii.adisoftin.util.AppSharedPreferences;
import com.wasapii.adisoftin.util.CommonUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.R.attr.fragment;

public class ChosePlaceActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {

    private ArrayList<Venue> list = new ArrayList<>();
    private Context ctx;
    private ImageAdapter adapter;
  //  ListView listView;
    private GridView gridView;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private TextView et_search;
    private Location mLastLocation=null;
    private GoogleApiClient mGoogleApiClient=null;
    private double lat,lng;
    private LatLng southwestLatLng=null,northeastLatLng=null;
    LocationRequest mLocationRequest;
    private static final long INTERVAL = 1000 * 60;
    private static final long FASTEST_INTERVAL = 1000 * 20;
    String  mLastUpdateTime="";
    TextView btn_skip;
    String name = "";
    String distance = "";
    private int fromWhere;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_chose_place);
       // listView = (ListView) findViewById(R.id.listview);
        gridView = (GridView) findViewById(R.id.gridview);
        btn_skip = (TextView) findViewById(R.id.btn_skip);
        et_search = (TextView) findViewById(R.id.et_search);

        ctx = ChosePlaceActivity.this;

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }



        btn_skip.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChosePlaceActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();

            }
        });

        et_search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                try {

                    AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                            .setCountry("IN")
                            .build();

                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .setFilter(typeFilter)
//                                     .setBoundsBias(new LatLngBounds(
//                                             southwestLatLng,
//                                             northeastLatLng))

                                    .build(ChosePlaceActivity.this);

                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });


//        createLocationRequest();

        exploreRequest(AppSharedPreferences.getLat(ctx),AppSharedPreferences.getLng(ctx));


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i("wasapi", "Place: " + place.getPlaceTypes());
                et_search.setText(place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("wasapi", "error message= "+status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }


    private void exploreRequest(double lat,double lng){

        // Tag used to cancel the request
        String  tag_string_req = "string_req";

        //food,coffee,event,education,shops,travel,nightlife,arts_entertainment,parks_outdoors
        //String url = "https://api.foursquare.com/v2/venues/search?ll=22.71788,75.887743&oauth_token=2O2KIV2ONNH3BSVHEYSG5GA54LP0FTULCPLIGMGQYMUW5MZH&v=20170224&query=restaurant%2C%coffee&intent=checkin&radius=500";
        //  String url="https://api.foursquare.com/v2/venues/search?v=20170223&ll=22.719036%2C%2075.886849&query=restaurant%2C%coffee&intent=checkin&radius=500&client_id=TRFKBZAA1MKN1IZUTIZ3TVPAOWVZF2MVBZEAVXQBIW2EFXGL&client_secret=1OMDMGIFU5TK0QREBIYKM4NLG2PNQ4FIMNA3TPHY30QLFSYN";
        // "https://api.foursquare.com/v2/venues/search?v=20170223&ll=22.719036%2C%2075.886849&query=coffee%2C%nightlife&intent=browse&radius=500&client_id=TRFKBZAA1MKN1IZUTIZ3TVPAOWVZF2MVBZEAVXQBIW2EFXGL&client_secret=1OMDMGIFU5TK0QREBIYKM4NLG2PNQ4FIMNA3TPHY30QLFSYN";//AppGlobalConstants.WEBSERVICE_BASE_URL+"login/Email/"+email+"/Password/"+password+"/DeviceType/aos/DeviceToken/"+AppSharedPreferences.getFcmToken(ctx)+"/AgentCode/"+AppSharedPreferences.getAgentCode(ctx);

        String today=new SimpleDateFormat("yyyyMMdd").format(new Date());


        String url="https://api.foursquare.com/v2/venues/explore?v="+today+"&ll="+lat+","+lng+"&venuePhotos=1&radius=100&client_id=TRFKBZAA1MKN1IZUTIZ3TVPAOWVZF2MVBZEAVXQBIW2EFXGL&client_secret=1OMDMGIFU5TK0QREBIYKM4NLG2PNQ4FIMNA3TPHY30QLFSYN";
        //String url="https://api.foursquare.com/v2/venues/explore?v="+today+"&ll="+37.9993743+","+ 23.8025605+"&venuePhotos=1&radius=100&client_id=TRFKBZAA1MKN1IZUTIZ3TVPAOWVZF2MVBZEAVXQBIW2EFXGL&client_secret=1OMDMGIFU5TK0QREBIYKM4NLG2PNQ4FIMNA3TPHY30QLFSYN";
        //String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+today+"&lat="+37.9993743+"&lng="+23.8025605+"&radius=5000&client_id=TRFKBZAA1MKN1IZUTIZ3TVPAOWVZF2MVBZEAVXQBIW2EFXGL&client_secret=1OMDMGIFU5TK0QREBIYKM4NLG2PNQ4FIMNA3TPHY30QLFSYN";

        Log.e("wasapii","venue url="+url);

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("com.adisoft.mls.temp", "login responce "+response.toString());
                pDialog.hide();
                parseJSONResponseExplore(response.toString());
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("com.adisoft.mls.temp", "Error: " + error.getMessage());
                pDialog.hide();

                // As of f605da3 the following should work
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        Log.e("com.adisoft.mls.temp","error response "+res);
                        parseJSONResponseExplore(res);

                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    }
                }
            }
        });

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }



    private void parseJSONResponseExplore(String response){

        try {

            JSONObject metaData = new JSONObject(response);

            JSONObject jsonObject = metaData.getJSONObject("response");//new JSONObject(response);

            JSONArray groups = jsonObject.getJSONArray("groups");

            JSONObject mainObject = groups.getJSONObject(0);//jsonObject.getJSONObject("response");

            JSONArray items = mainObject.getJSONArray("items");

            list.clear();

            for(int k=0;k<items.length();k++){

                JSONObject venueObject1 = items.getJSONObject(k);

                JSONObject venueObject = venueObject1.getJSONObject("venue");


                String address = "";
                String contact = "";

                String imageUrl="";

                String latitude = "";
                String longitude = "";


                if (venueObject.has("name")) {
                    name = venueObject.getString("name");
                    Log.e("Name",name);

                }

                JSONObject locationObject = venueObject.getJSONObject("location");

                if (locationObject.has("formattedAddress")) {
                    address = locationObject.getString("formattedAddress");//locationObject.getString("address")+","+locationObject.getString("city")+","+locationObject.getString("state")+", "+locationObject.getString("country");
                }

                if (locationObject.has("distance")) {
                    distance = locationObject.getString("distance") + "m";
                    Log.e("Distance",distance);
                }

                if (locationObject.has("lat")) {
                    latitude = locationObject.getString("lat");

                }

                if (locationObject.has("lng")) {
                    longitude = locationObject.getString("lng");

                }

                JSONObject contactObject = venueObject.getJSONObject("contact");

                if (contactObject.has("phone")) {
                    contact = contactObject.getString("phone");
                }

                JSONObject photoObject = venueObject.getJSONObject("photos");
                int count=photoObject.getInt("count");

                if(count>0){
                    JSONArray photos = photoObject.getJSONArray("groups");
                    JSONObject singlePhoto=photos.getJSONObject(0);
                    int itemCount=singlePhoto.getInt("count");

                    if(itemCount>0) {

                        JSONArray photosItems = singlePhoto.getJSONArray("items");
                        JSONObject photoItem=photosItems.getJSONObject(0);

                        imageUrl = photoItem.getString("prefix") + "200x200" + photoItem.getString("suffix");
                        Log.d("wasa","image url="+imageUrl);

                    }

                }

                Venue venue = new Venue(name, address, contact, distance,imageUrl, latitude, longitude);

                list.add(venue);


            }

            adapter = new ImageAdapter();

            gridView.setAdapter(adapter);


        }catch (JSONException js){
            js.printStackTrace();
        }


    }



    class ViewHolder{
        public ImageView photo;
        //  public SimpleDraweeView photo;;
        public TextView tv_name;

    }


//    private void getCityNameViaLatLong(){
//
//        try {
//
//            Geocoder geocoder;
//            List<Address> addresses;
//            geocoder = new Geocoder(this, Locale.getDefault());
//
//            addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//
//            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//            String city = addresses.get(0).getLocality();
//            String state = addresses.get(0).getAdminArea();
//            String country = addresses.get(0).getCountryName();
//
//
//            Log.d("wasapii","city="+city+"\nstate="+state+"\n country="+country+"\n address="+address);
//
//
//            String url="http://maps.google.com/maps/api/geocode/json?address="+city.trim().replaceAll(" ","%20");
//
//            StringRequest strReq = new StringRequest(Request.Method.GET,
//                    url, new Response.Listener<String>() {
//
//                @Override
//                public void onResponse(String response) {
//                    Log.d("com.adisoft.mls.temp", "get city by latlng responce "+response.toString());
//
//                    parseSearchLatlng(response.toString());
//                }
//            }, new Response.ErrorListener() {
//
//                @Override
//                public void onErrorResponse(VolleyError error) {
//
//                    Log.d("com.adisoft.mls.temp", "Error: " + error.getMessage());
//
//                    // As of f605da3 the following should work
//                    NetworkResponse response = error.networkResponse;
//                    if (error instanceof ServerError && response != null) {
//                        try {
//                            String res = new String(response.data,
//                                    HttpHeaderParser.parseCharset(response.headers, "utf-8"));
//                            // Now you can use any deserializer to make sense of data
//                            Log.e("com.adisoft.mls.temp","error response "+res);
//                            parseSearchLatlng(res);
//
//                        } catch (UnsupportedEncodingException e1) {
//                            // Couldn't properly decode data to string
//                            e1.printStackTrace();
//                        }
//                    }
//                }
//            });
//
//            // Adding request to request queue
//            MyApplication.getInstance().addToRequestQueue(strReq, "get city");
//
//
//        }catch (IOException io){
//            io.printStackTrace();
//        }
//
//
//
//    }

//    private void parseSearchLatlng(String response){
//
//        try {
//
//            JSONObject jsonObject = new JSONObject(response);
//            JSONArray results= jsonObject.getJSONArray("results");
//            JSONObject items = results.getJSONObject(0);
//            JSONObject geometry=items.getJSONObject("geometry");
//            JSONObject bounds=geometry.getJSONObject("bounds");
//            JSONObject northeast=bounds.getJSONObject("northeast");
//            JSONObject southwest=bounds.getJSONObject("southwest");
//
//            northeastLatLng = new LatLng(northeast.getDouble("lat"),northeast.getDouble("lng"));
//
//            southwestLatLng = new LatLng(southwest.getDouble("lat"),southwest.getDouble("lng"));
//
//        }catch (JSONException je){
//            je.printStackTrace();
//        }
//    }


//   public class MyAdapter extends BaseAdapter {
//
//
//        int layoutResourceId;
//
//
//        public MyAdapter( int layoutResourceId) {
////            super(ctx, layoutResourceId);
//            this.layoutResourceId = layoutResourceId;
//
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//
//        @Override
//        public Object getItem(int position) {
//            return null;
//        }
//
//
//        @Override
//        public int getCount() {
//            return list.size();
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//
//            final ViewHolder holder ;
//
//            /*
//             * The convertView argument is essentially a "ScrapView" as described is Lucas post
//             * http://lucasr.org/2012/04/05/performance-tips-for-androids-listview/
//             * It will have a non-null value when ListView is asking you recycle the row layout.
//             * So, when convertView is not null, you should simply update its contents instead of inflating a new row layout.
//             */
//            if(convertView==null){
//                // inflate the layout
//                LayoutInflater inflater = ((Activity) ctx).getLayoutInflater();
//                convertView = inflater.inflate(layoutResourceId, parent, false);
//                holder = new ViewHolder();
//
//                holder.tv_address=(TextView)convertView.findViewById(R.id.tv_address);
//                holder.tv_name=(TextView)convertView.findViewById(R.id.tv_name);
//                holder.tv_contact=(TextView)convertView.findViewById(R.id.tv_contact);
//                holder.tv_distance=(TextView)convertView.findViewById(R.id.tv_distance);
//
//                holder.photo=(ImageView) convertView.findViewById(R.id.image);
//
//                convertView.setTag(holder);
//
//            }else {
//                // View recycled !
//                // no need to inflate
//                // no need to findViews by id
//                holder = (ViewHolder) convertView.getTag();
//
//                Log.e("mls","satish View recycled.....");
//            }
//
//            final Venue item = list.get(position);
//
//            holder.tv_name.setText(item.getName());
//            holder.tv_contact.setText(item.getContact());
//            holder.tv_address.setText(item.getAddress());
//            holder.tv_distance.setText(item.getDistance());
//
//            Glide.with(ctx).load(item.getPhotoUrl())
//                    .error(R.mipmap.ic_launcher)
//                    .placeholder(R.mipmap.ic_launcher)
//                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
//                    .into(holder.photo);
//
//            return convertView;
//
//        }
//
//    }


    public class ImageAdapter extends BaseAdapter {


        public ImageAdapter() {

        }

        public int getCount() {
            return list.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {

            final ViewHolder holder ;

            /*
             * The convertView argument is essentially a "ScrapView" as described is Lucas post
             * http://lucasr.org/2012/04/05/performance-tips-for-androids-listview/
             * It will have a non-null value when ListView is asking you recycle the row layout.
             * So, when convertView is not null, you should simply update its contents instead of inflating a new row layout.
             */
            if(convertView==null){
                // inflate the layout
                LayoutInflater inflater = ((Activity) ctx).getLayoutInflater();
                convertView = inflater.inflate(R.layout.place_grid_item, parent, false);

                holder = new ViewHolder();
                holder.tv_name = (TextView)convertView.findViewById(R.id.tv_venueName);
                holder.photo = (ImageView) convertView.findViewById(R.id.photo);



                convertView.setTag(holder);

            }else {
                // View recycled !
                // no need to inflate
                // no need to findViews by id
                holder = (ViewHolder) convertView.getTag();
            }

            final Venue item = list.get(position);

            holder.tv_name.setText(item.getName());


            Glide.with(ctx).load(item.getPhotoUrl())
                    .error(R.drawable.gradiant_background)
                    .placeholder(R.drawable.gradiant_background)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    //.bitmapTransform(new RoundedCornersTransformation( ctx,15,2, RoundedCornersTransformation.CornerType.TOP))
                    .into(holder.photo);


            holder.photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    requestSubmitUserVenueDetails(item.getName(), item.getLatitude(), item.getLongitude());

//                    Intent home = new Intent(ChosePlaceActivity.this,HomeActivity.class);
//                    startActivity(home);

                }
            });


//            Glide.with(ctx).load(item.getPhotoUrl()).asBitmap().error(R.drawable.gradiant_background).placeholder(R.drawable.gradiant_background).into(new BitmapImageViewTarget(holder.photo) {
//                @Override
//                protected void setResource(Bitmap mbitmap) {
//                   /* RoundedBitmapDrawable circularBitmapDrawable =
//                            RoundedBitmapDrawableFactory.create(ctx.getResources(), resource);
//                    circularBitmapDrawable.setCircular(true);
//                    holder.photo.setImageDrawable(circularBitmapDrawable);*/
//
//                    Bitmap imageRounded = Bitmap.createBitmap(mbitmap.getWidth(), mbitmap.getHeight(), mbitmap.getConfig());
//                    Canvas canvas = new Canvas(imageRounded);
//                    Paint mpaint = new Paint();
//                    mpaint.setAntiAlias(true);
//                    mpaint.setShader(new BitmapShader(mbitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
//                    canvas.drawRoundRect((new RectF(0, 0, mbitmap.getWidth(), mbitmap.getHeight())), 20, 20, mpaint);// Round Image Corner 100 100 100 100
//                    holder.photo.setImageBitmap(imageRounded);
//                }
//            });

            return convertView;
        }


    }

    protected void startLocationUpdates() {

        try {
            PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
            Log.d("", "Location update started ..............: ");
        }catch (Exception ne){
            ne.printStackTrace();
        }
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.d("wasapii","****onConnected***");

        startLocationUpdates();

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {

            Log.d("wasapii","my current lat="+String.valueOf(mLastLocation.getLatitude())+" lng="+String.valueOf(mLastLocation.getLongitude()));

            lat = mLastLocation.getLatitude();
            lng = mLastLocation.getLongitude();
//            getCityNameViaLatLong();
            exploreRequest(mLastLocation.getLatitude(),mLastLocation.getLongitude());

        }else{
            Log.d("wasapii","****onConnected null***");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.d("wasapii","///// onConnectionSuspended ////");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.d("wasapii","........ onConnectionSuspended ....");
    }

//    protected void createLocationRequest() {
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(INTERVAL);
//        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
//        mLocationRequest.setSmallestDisplacement(100f);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
//    }

    @Override
    public void onLocationChanged(Location location) {

        Log.d("", "Firing onLocationChanged..............................................");
        mLastLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

        if (null != mLastLocation) {
            String lat = String.valueOf(mLastLocation.getLatitude());
            String lng = String.valueOf(mLastLocation.getLongitude());
            Log.e("wasapi","At Time: " + mLastUpdateTime + "\n" +
                    "Latitude: " + lat + "\n" +
                    "Longitude: " + lng + "\n" +
                    "Accuracy: " + mLastLocation.getAccuracy() + "\n" +
                    "Provider: " + mLastLocation.getProvider());
        } else {
            Log.d("wasapii", "location is null ...............");
        }

    }









    private void requestSubmitUserVenueDetails(String venue_name, String lat, String lng){

        if(!CommonUtilities.isOnline(ctx)){
            Toast.makeText(ctx,R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
            return;
        }

        String url= AppGlobalConstants.WEBSERVICE_BASE_URL+"user_venue_detail";

        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject parameters = new JSONObject();

        try {
//            {"user_id":"24","venue_name":"56dukan","venue_lat":"43.34234","venue_long":"879.45747"}
            parameters.put("user_id",AppSharedPreferences.loadUserIDFromPreference(ctx));
            parameters.put("venue_name",venue_name);
            parameters.put("venue_lat",lat);
            parameters.put("venue_long",lng);


            JsonRequest jsonRequest =  new JsonObjectRequest(Request.Method.POST,url,parameters,new Response.Listener<JSONObject>(){

                @Override
                public void onResponse(JSONObject response) {
                    pDialog.dismiss();
                    parseResponse(response.toString());

                }

            },new Response.ErrorListener(){

                @Override
                public void onErrorResponse(VolleyError error) {

                    pDialog.dismiss();

                    NetworkResponse response = error.networkResponse;

                    Log.e("com.adisoft.mls.temp","satish error response "+response);

                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                        Log.e("mls","VolleyError TimeoutError error or NoConnectionError");

                    } else if (error instanceof AuthFailureError) {
                        //TODO
                        Log.e("mls","VolleyError AuthFailureError");
                    } else if (error instanceof ServerError) {
                        Log.e("mls","VolleyError ServerError");
                        //TODO
                    } else if (error instanceof NetworkError) {
                        Log.e("mls","VolleyError NetworkError");
                        //TODO
                    } else if (error instanceof ParseError) {
                        Log.e("mls","VolleyError TParseError");
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


    private void parseResponse(String response){

        Log.e("wasapii","response "+response);

        try {

            JSONObject jsonObject = new JSONObject(response);

            int flag  = jsonObject.getInt("Flag");
            String errorMessage = jsonObject.has("Message") ? jsonObject.getString("Message"):"Invalid email id";

            if(flag==1){

                JSONObject userObject = jsonObject.getJSONObject("UserDetail");
                Log.e("JSONObject",userObject+"");

                    String id = userObject.getString("user_id");

                    Intent home = new Intent(ChosePlaceActivity.this,HomeActivity.class);
                    startActivity(home);

            }else{
                Toast.makeText(ctx,errorMessage,Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }




}
