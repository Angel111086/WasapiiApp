package com.wasapii.adisoftin.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.wasapii.adisoftin.ChosePlaceActivity;
import com.wasapii.adisoftin.HomeActivity;
import com.wasapii.adisoftin.LoginActivity;
import com.wasapii.adisoftin.MyApplication;
import com.wasapii.adisoftin.R;
import com.wasapii.adisoftin.Receiver.ProximityReceiver;
import com.wasapii.adisoftin.SignUpNameActivity;
import com.wasapii.adisoftin.ViewUserProfileActivity;
import com.wasapii.adisoftin.customview.RoundedCornersTransformation;
import com.wasapii.adisoftin.model.NearUser;
import com.wasapii.adisoftin.util.AppGlobalConstants;
import com.wasapii.adisoftin.util.AppSharedPreferences;
import com.wasapii.adisoftin.util.CommonUtilities;

import net.steamcrafted.materialiconlib.MaterialIconView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.LOCATION_SERVICE;


public class PeopleFragment extends Fragment {

    private Context ctx;
    private Button btn;
    private GridView gridView;
    private ArrayList<NearUser> list = new ArrayList<>();
    private GridAdapter gridAdapter;
    private ImageView btn_filter;
    private TextView tv_load_more;
    private int from = 0, to = 10;
    private boolean isViewShown;
    String myStr = "";
    String dist = "";
    String min = "", max = "";
    String male, female,both;
    int m, f, b;
    String gender = "male",login_status;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_people, container, false);

        gridView = (GridView) v.findViewById(R.id.gridview);

        btn_filter = (ImageView) v.findViewById(R.id.btn_filter);

        tv_load_more = (TextView) v.findViewById(R.id.tv_load_more);

        ctx = getActivity();


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                NearUser nearUser = list.get(position);
                Intent intent = new Intent(ctx, ViewUserProfileActivity.class);
                intent.putExtra("user id", nearUser.getId());
                intent.putExtra("now_at",nearUser.getVenueName());
                startActivity(intent);
            }
        });
        tv_load_more.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(ctx, "No more", Toast.LENGTH_SHORT).show();
            }
        });
        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });

        if (!isViewShown) {
            requestGetNearByUsers();
        }
        return v;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getView() != null) {
            isViewShown = true;
            // fetchdata() contains logic to show data when page is selected mostly asynctask to fill the data
            requestGetNearByUsers();
            Log.d("wasapii ", "setUserVisibleHint  requestGetNearByUsers()");

        } else {
            isViewShown = false;
        }


    }

    private void requestGetNearByUsers() {

        if (!CommonUtilities.isOnline(ctx)) {
            Toast.makeText(ctx, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            return;
        }

        String url = AppGlobalConstants.WEBSERVICE_BASE_URL + "user_serach";

        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();
        JSONObject parameters = new JSONObject();

        try {

            String timestamp = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").format(new Date());
            parameters.put("user_id", AppSharedPreferences.loadUserIDFromPreference(ctx));
            parameters.put("user_lat", AppSharedPreferences.getLat(ctx));
            parameters.put("user_long", AppSharedPreferences.getLng(ctx));
            parameters.put("user_timestamp", timestamp);
            parameters.put("from", from);
            parameters.put("to", to);

            Log.e("Lat",AppSharedPreferences.getLat(ctx)+"");
            Log.e("Long",AppSharedPreferences.getLng(ctx)+"");
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
        Log.e("People Fragment wasapii", "response " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            int flag = jsonObject.getInt("Flag");
            String errorMessage = jsonObject.has("Message") ? jsonObject.getString("Message") : "Invalid email id";
            if (flag == 1) {

                JSONArray jsonArray = jsonObject.getJSONArray("UserDetail");
                Log.e("JSONArray", jsonArray + "");
                list.clear();

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject userObject = jsonArray.getJSONObject(i);

                    String id = userObject.getString("user_id");
                    login_status = userObject.getString("login_status");
                    Log.e("Login_status",login_status);
                    if(login_status == "0"){
                        Intent in = new Intent(ctx, LoginActivity.class);
                        startActivity(in);
                    }
                    if (!AppSharedPreferences.loadUserIDFromPreference(ctx).equalsIgnoreCase(id)) {

                        double distance_in_meters = Double.parseDouble(userObject.getString("distance")) * 1609.34;
                        list.add(new NearUser(userObject.getString("name"), userObject.getString("venu_name"), String.valueOf(distance_in_meters), userObject.getString("profile_img"), userObject.getString("user_id")));

                    }
                }

                gridAdapter = new GridAdapter();

                gridView.setAdapter(gridAdapter);

            } else {
                Toast.makeText(ctx, errorMessage, Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    class ViewHolder {
        public ImageView photo;
        //  public SimpleDraweeView photo;;
        public TextView tv_name, tv_distance, tv_venueName;

    }
    public class GridAdapter extends BaseAdapter {


        public GridAdapter() {

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

            final ViewHolder holder;

            /*
             * The convertView argument is essentially a "ScrapView" as described is Lucas post
             * http://lucasr.org/2012/04/05/performance-tips-for-androids-listview/
             * It will have a non-null value when ListView is asking you recycle the row layout.
             * So, when convertView is not null, you should simply update its contents instead of inflating a new row layout.
             */

            if (convertView == null) {
                // inflate the layout
                LayoutInflater inflater = ((Activity) ctx).getLayoutInflater();
                convertView = inflater.inflate(R.layout.home_user_grid_item, parent, false);

                holder = new ViewHolder();
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_userName);
                holder.photo = (ImageView) convertView.findViewById(R.id.photo);
                holder.tv_distance = (TextView) convertView.findViewById(R.id.tv_distance);
                holder.tv_venueName = (TextView) convertView.findViewById(R.id.tv_venueName);
                convertView.setTag(holder);

            } else {
                // View recycled !
                // no need to inflate
                // no need to findViews by id
                holder = (ViewHolder) convertView.getTag();
            }


            final NearUser item = list.get(position);

            holder.tv_name.setText(item.getName());
            holder.tv_venueName.setText(item.getVenueName());


            if (!item.getDistance().equalsIgnoreCase("")) {
                double value = Double.parseDouble(item.getDistance());
                //double value = Double.parseDouble(dist);
                holder.tv_distance.setText("" + Math.round(value) + " m");
            }


            Glide.with(ctx).load(item.getPhotoUrl())
                    .placeholder(R.drawable.no_photo_available_icon)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .bitmapTransform(new RoundedCornersTransformation(ctx, 15, 2, RoundedCornersTransformation.CornerType.TOP))
                    .into(holder.photo);

            return convertView;
        }


    }


    private void showFilterDialog() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.setContentView(R.layout.dialog_home_filter);

        //    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); // In this Case keyboard not appear automatically

        //  dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

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
//                gender="male";
//                if (m == R.id.btn_male) {
//                    gender="";
//                    gender += male;
//                    Log.e("If", gender);
//
//                }
//                if (f == R.id.btn_female) {
//                    gender="";
//                    gender += female;
//                    Log.e("Else If", gender);
//                }
//                if (b == R.id.btn_both){
//                    gender="";
//                    gender += both;
//                    Log.e("Ifboth", gender);
//                }
//
                Log.e("Gender",gender);

                requestGetNearByUsersByFilter(min, max, gender);
                //gender=null;
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void requestGetNearByUsersByFilter(String minV, String maxV, String gen) {

        if (!CommonUtilities.isOnline(ctx)) {
            Toast.makeText(ctx, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            return;
        }

        String url = AppGlobalConstants.WEBSERVICE_BASE_URL + "user_serach_by_filter";

        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject parameters = new JSONObject();

        try {
            String timestamp = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").format(new Date());
            parameters.put("user_id", AppSharedPreferences.loadUserIDFromPreference(ctx));
            parameters.put("user_lat", AppSharedPreferences.getLat(ctx));
            parameters.put("user_long", AppSharedPreferences.getLng(ctx));
            parameters.put("user_timestamp", timestamp);
            parameters.put("gender", gen);
            parameters.put("agefrom", minV);
            parameters.put("ageto", maxV);

            JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    pDialog.dismiss();
                    parseResponseByFilter(response.toString());

                }

            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    pDialog.dismiss();

                    NetworkResponse response = error.networkResponse;

                    Log.e("com.adisoft.mls.temp", "Dialog error response " + response);

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

                            parseResponseByFilter(res);

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

    private void parseResponseByFilter(String response) {

        Log.e("Auto Filter", "response " + response);

        try {

            JSONObject jsonObject = new JSONObject(response);

            int flag = jsonObject.getInt("Flag");
            String errorMessage = jsonObject.has("Message") ? jsonObject.getString("Message") : "Invalid email id";

            if (flag == 1) {

                JSONArray jsonArray = jsonObject.getJSONArray("UserDetail");
                Log.e("JSONArray", jsonArray + "");
                list.clear();

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject userObject = jsonArray.getJSONObject(i);

                    String id = userObject.getString("user_id");

                    if (!AppSharedPreferences.loadUserIDFromPreference(ctx).equalsIgnoreCase(id)) {

                        double distance_in_meters = Double.parseDouble(userObject.getString("distance")) * 1609.344;
                        list.add(new NearUser(userObject.getString("name"), userObject.getString("venu_name"), String.valueOf(distance_in_meters), userObject.getString("profile_img"), userObject.getString("user_id")));

                    }
                }

                gridAdapter = new GridAdapter();

                gridView.setAdapter(gridAdapter);

            } else {
                Toast.makeText(ctx, errorMessage, Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }





}
