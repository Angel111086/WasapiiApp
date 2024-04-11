package com.wasapii.adisoftin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.wasapii.adisoftin.customview.HashTagEditText;
import com.wasapii.adisoftin.db.DataHelper;
import com.wasapii.adisoftin.util.AppGlobalConstants;
import com.wasapii.adisoftin.util.AppSharedPreferences;
import com.wasapii.adisoftin.util.CommonUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SignUpHashTagActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mButtonNext;
    private Context ctx;
    private HashTagEditText hashTagInput;
    private TextView music_tag,lifestyle_tag,animals_tag,sports_tag,nonprofit_tag,literature_tag,cinema_tag,
            arts_tag,technology_tag,gaming_tag,entertainment_tag,politics_tag,photography_tag,traveling_tag;

    private DataHelper dh;
    private String refreshedToken="";
    private TextView tv_redirectToLogin;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_sign_up_hash_tag);

        findViewByIds();

        ctx = SignUpHashTagActivity.this;

        setOnclickListener();

        dh = DataHelper.getInstance(ctx);

        refreshedToken = FirebaseInstanceId.getInstance().getToken();

        AppSharedPreferences.putFcmToken(ctx,refreshedToken);

        tv_redirectToLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                actionAlreadyAccount();
            }
        });

    }


    private void actionAlreadyAccount(){

        Intent i = new Intent(ctx, LoginActivity.class);
// set the new task and clear flags
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);

    }


    private void setOnclickListener(){

        mButtonNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String intenerests = hashTagInput.getValues().toString().replace("[","").replace("]","");//hashTagInput.getText().toString().trim();

                if(intenerests.equalsIgnoreCase("")){
                    Toast.makeText(ctx,"Please select at least one #tag",Toast.LENGTH_SHORT).show();
                }else {
                    requestRegistration(intenerests);
                }
            }
        });

        music_tag.setOnClickListener(this);
        lifestyle_tag.setOnClickListener(this);
        animals_tag.setOnClickListener(this);
        sports_tag.setOnClickListener(this);
        nonprofit_tag.setOnClickListener(this);
        literature_tag.setOnClickListener(this);
        cinema_tag.setOnClickListener(this);
        arts_tag.setOnClickListener(this);
        technology_tag.setOnClickListener(this);
        gaming_tag.setOnClickListener(this);
        entertainment_tag.setOnClickListener(this);
        politics_tag.setOnClickListener(this);
        photography_tag.setOnClickListener(this);
        traveling_tag.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.music_tag:
                setHashValue("Music");
                music_tag.setVisibility(View.GONE);
//                music_tag.setBackgroundResource(R.drawable.hash_tag_bg);
                break;
            case R.id.lifestyle_tag:
                setHashValue("Lifestyle");
                lifestyle_tag.setVisibility(View.GONE);
//                lifestyle_tag.setBackgroundResource(R.drawable.hash_tag_bg);
                break;
            case R.id.animals_tag:
                setHashValue("Animals");
                animals_tag.setVisibility(View.GONE);
//                animals_tag.setBackgroundResource(R.drawable.hash_tag_bg);
                break;
            case R.id.sports_tag:
                setHashValue("Sports");
                sports_tag.setVisibility(View.GONE);
                break;
            case R.id.nonprofit_tag:
                setHashValue("NonProfilt");
                nonprofit_tag.setVisibility(View.GONE);
                break;
            case R.id.literature_tag:
                setHashValue("Literature");
                literature_tag.setVisibility(View.GONE);
                break;
            case R.id.cinema_tag:
                setHashValue("Cinema");
                cinema_tag.setVisibility(View.GONE);
                break;
            case R.id.arts_tag:
                setHashValue("Arts");
                arts_tag.setVisibility(View.GONE);
                break;
            case R.id.technology_tag:
                setHashValue("Technology");
                technology_tag.setVisibility(View.GONE);
                break;
            case R.id.gaming_tag:
                setHashValue("Gaming");
                gaming_tag.setVisibility(View.GONE);
                break;
            case R.id.entertainment_tag:
                setHashValue("Entertainment");
                entertainment_tag.setVisibility(View.GONE);
                break;
            case R.id.politics_tag:
                setHashValue("Politics");
                politics_tag.setVisibility(View.GONE);
                break;
            case R.id.photography_tag:
                setHashValue("Photography");
                photography_tag.setVisibility(View.GONE);
                break;
            case R.id.traveling_tag:
                setHashValue("Traveling");
                traveling_tag.setVisibility(View.GONE);
                break;

        }
    }

    private void setHashValue(String str){

        String hashValues = hashTagInput.getText().toString().trim();

        if(hashValues.contains(str)){
            return;
        }

        hashValues = hashValues+""+str+" ";

        hashTagInput.setText(hashValues);
    }

    private void findViewByIds(){

        hashTagInput = (HashTagEditText) findViewById(R.id.tag_input);
        mButtonNext = (Button) findViewById(R.id.btn_next);
        music_tag = (TextView) findViewById(R.id.music_tag);
        lifestyle_tag = (TextView) findViewById(R.id.lifestyle_tag);
        animals_tag = (TextView) findViewById(R.id.animals_tag);
        sports_tag =  (TextView) findViewById(R.id.sports_tag);
        nonprofit_tag = (TextView) findViewById(R.id.nonprofit_tag);
        literature_tag = (TextView) findViewById(R.id.literature_tag);
        cinema_tag = (TextView) findViewById(R.id.cinema_tag);
        arts_tag = (TextView) findViewById(R.id.arts_tag);
        technology_tag = (TextView) findViewById(R.id.technology_tag);
        gaming_tag = (TextView) findViewById(R.id.gaming_tag);
        entertainment_tag = (TextView) findViewById(R.id.entertainment_tag);
        politics_tag = (TextView) findViewById(R.id.politics_tag);
        photography_tag = (TextView) findViewById(R.id.photography_tag);
        traveling_tag = (TextView) findViewById(R.id.traveling_tag);
        tv_redirectToLogin = (TextView) findViewById(R.id.tv_redirectToLogin);
    }


    private void requestRegistration(String interests){

        if(!CommonUtilities.isOnline(ctx)){
            Toast.makeText(ctx,R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
            return;
        }

        String url= AppGlobalConstants.WEBSERVICE_BASE_URL+"signup";

        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject parameters = new JSONObject();

        try {

            parameters.put("email",AppSharedPreferences.getEmail(ctx));
            parameters.put("password",AppSharedPreferences.getPassword(ctx));
            parameters.put("name",AppSharedPreferences.getFullName(ctx));
            parameters.put("contact_no",AppSharedPreferences.getMobile(ctx));
            parameters.put("user_interests",interests);
            parameters.put("user_lat",AppSharedPreferences.getLat(ctx));
            parameters.put("user_long",AppSharedPreferences.getLng(ctx));
            parameters.put("venu_name","");
            parameters.put("venu_lat","");
            parameters.put("venu_long","");
            parameters.put("device_token",AppSharedPreferences.getFcmToken(ctx));
            parameters.put("user_timestamp",new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa").format(new Date()));
            parameters.put("signup_type","1");
            parameters.put("profile_img",AppSharedPreferences.getProfilePic(ctx));
            parameters.put("gender",AppSharedPreferences.getGender(ctx));
            parameters.put("dob",AppSharedPreferences.getDOB(ctx));
            parameters.put("device_type","aos");


            Log.e("wasapii","signup params "+parameters.toString());


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
                            String res = new String(response.data,
                                    HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                            // Now you can use any deserializer to make sense of data
                            Log.e("com.adisoft.mls.temp","satish error response "+res);

                            parseResponse(response.toString());

                        } catch (UnsupportedEncodingException e1) {
                            // Couldn't properly decode data to string
                            e1.printStackTrace();
                        }
                    }

                }
            });

            jsonRequest.setShouldCache(false);

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

        try{

            JSONObject jsonObject = new JSONObject(response);
            int flag = jsonObject.has("Flag") ? jsonObject.getInt("Flag"):0;
            String errorMessage = jsonObject.has("Message") ? jsonObject.getString("Message"):"Failed try again";

            if(flag==1){

                JSONObject userDetails = jsonObject.has("UserDetail") ? jsonObject.getJSONObject("UserDetail"):null;

                if(userDetails!=null){

                    String userId = userDetails.has("user_id")?userDetails.getString("user_id"):"";
                    String email = userDetails.has("email")?userDetails.getString("email"):"";
                    String name = userDetails.has("name")?userDetails.getString("name"):"";
                    String password = userDetails.has("password")?userDetails.getString("password"):"";
                    String contactNo = userDetails.has("contact_no")?userDetails.getString("contact_no"):"";
                    String gender = userDetails.has("gender")?userDetails.getString("gender"):"";
                    String dob = userDetails.has("dob")?userDetails.getString("dob"):"";
                    String profileImage = userDetails.has("profile_img")?userDetails.getString("profile_img"):"";
                    String userInterests = userDetails.has("user_interests")?userDetails.getString("user_interests"):"";
//                    String venueName = userDetails.has("venu_name")?userDetails.getString("venu_name"):"";
//                    String venueLat = userDetails.has("venu_lat")?userDetails.getString("venu_lat"):"";
//                    String venueLong = userDetails.has("venu_long")?userDetails.getString("venu_long"):"";
//                    String userLat = userDetails.has("user_lat")?userDetails.getString("user_lat"):"";
//                    String userLong = userDetails.has("user_long")?userDetails.getString("user_long"):"";
//                    String userTimestamp = userDetails.has("user_timestamp")?userDetails.getString("user_timestamp"):"";
//                    String deviceToken = userDetails.has("device_token")?userDetails.getString("device_token"):"";
//                    String loginStatus = userDetails.has("login_status")?userDetails.getString("login_status"):"";
//                    String signupType = userDetails.has("signup_type")? userDetails.getString("signup_type"):"";

                    dh.insertUserInfo(userId,name,password,email,contactNo,gender,dob,profileImage,"","","",userInterests);

                    AppSharedPreferences.saveUserIDToPreferences(ctx,userId);

                    Intent intent = new Intent(ctx, ChosePlaceActivity.class);
                    startActivity(intent);
                    finish();

                    Toast.makeText(ctx,"You have signed up successfully",Toast.LENGTH_SHORT).show();

                }

            }else {
                Toast.makeText(ctx,errorMessage,Toast.LENGTH_SHORT).show();
            }

        }catch (JSONException je){
            je.printStackTrace();
        }

    }

}
