package com.wasapii.adisoftin;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.firebase.client.Firebase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.wasapii.adisoftin.Constants.Constants;
import com.wasapii.adisoftin.Service.GPSService;

import com.wasapii.adisoftin.db.DataHelper;
import com.wasapii.adisoftin.model.UserDetail;
import com.wasapii.adisoftin.util.AppGlobalConstants;
import com.wasapii.adisoftin.util.AppSharedPreferences;
import com.wasapii.adisoftin.util.CommonUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText et_email, et_password;
    private Button btn_signin, btn_signup;
    private LinearLayout btn_signup_with_fb;
    private Context ctx;
    private CallbackManager callbackManager;
    private String social_media_gender = "", social_media_userid_str = "", social_media_email_str = "", social_media_profile_pic_url = "", social_media_firstname = "", social_media_lastname = "";
    private TextView tv_forgot_password, tv_facebook;
    private Typeface regularFont, boldFont;
    private DataHelper dh;
    String refreshedToken = "";
    CheckBox remember_password;
    //private LoginPresenter mLoginPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);

        regularFont = Typeface.createFromAsset(getAssets(), "fonts/" + Constants.REGULAR_FONT);

        boldFont = Typeface.createFromAsset(getAssets(), "fonts/" + Constants.BOLD_FONT);

        Firebase.setAndroidContext(this);
        findViewById();

        ctx = LoginActivity.this;

        dh = DataHelper.getInstance(ctx);

        initFacebook();
        //mLoginPresenter = new LoginPresenter(ctx);
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("FireToken",refreshedToken);
        AppSharedPreferences.putFcmToken(ctx, refreshedToken);

        if(!AppSharedPreferences.getGlobalString(ctx,AppSharedPreferences.KEY_SHARED_PREFERENCE_REMEMBERED_USERNAME).trim().equalsIgnoreCase("")) {
            remember_password.setChecked(true);
        }
//
        et_email.setText(AppSharedPreferences.getGlobalString(ctx,AppSharedPreferences.KEY_SHARED_PREFERENCE_REMEMBERED_USERNAME));
        et_password.setText(AppSharedPreferences.getGlobalString(ctx,AppSharedPreferences.KEY_SHARED_PREFERENCE_REMEMBERED_USER_PASSWORD));


        btn_signin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                actionSignUp();

            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, SignUpEmailActivity.class);
                startActivity(intent);
                finish();

            }
        });

        btn_signup_with_fb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (isFacebookLogin()) {

                    LoginManager.getInstance().logOut();
                }

                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));

            }
        });

        tv_forgot_password.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showForgotPasswordDialog();
            }
        });

        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(et_password.length() > 0){
                        btn_signin.setBackgroundResource(R.drawable.rounded_black_background);
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(et_password.length() == 0){
                    btn_signin.setBackgroundResource(R.drawable.rounded_white_background);
                }
            }
        });

        if (!CommonUtilities.isGpsEnable(LoginActivity.this)) {
            showSettingsAlert();
        } else {
            Intent intent = new Intent(LoginActivity.this, GPSService.class);
            startService(intent);
        }

//        if (remember_password.isChecked()){
//            String mail = et_email.getText().toString().trim();
//            String passw = et_password.getText().toString().trim();
//            AppSharedPreferences.putEmail(ctx,mail);
//            AppSharedPreferences.putPassword(ctx,passw);
//            AppSharedPreferences.putBoolean(ctx,"true");
//            remember_password.setChecked(true);
//        }
//
//        if(AppSharedPreferences.getBoolean(ctx) == "true") {
//            String em = AppSharedPreferences.getEmail(ctx);
//            String pas = AppSharedPreferences.getPassword(ctx);
//            et_email.setText(em);
//            et_password.setText(pas);
//        }

    }

    private void actionSignUp() {

        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        if (email.equalsIgnoreCase("")) {
            Toast.makeText(ctx, "Please enter email id", Toast.LENGTH_SHORT).show();
        } else if (!AppSharedPreferences.isValidEmail(email)) {
            Toast.makeText(ctx, "Invalid email id", Toast.LENGTH_SHORT).show();
        } else if (password.equalsIgnoreCase("")) {
            Toast.makeText(ctx, "Please enter password", Toast.LENGTH_SHORT).show();
        } else {
            requestLogin(email, password);
            //requestRegisterbyFireBase(email,password);
            requestLoginbyFireBase(email,password);
        }
    }

    private void requestLoginbyFireBase(final String email, final String password){
        String url = "https://wasapii-faab8.firebaseio.com//users.json";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                if(s.equals("null")){
                    Toast.makeText(LoginActivity.this, "user not found", Toast.LENGTH_LONG).show();
                }
                else{
                    try {
                        JSONObject obj = new JSONObject(s);

                        if(!obj.has(email)){
                            Toast.makeText(LoginActivity.this, "user not found", Toast.LENGTH_LONG).show();
                        }
                        else if(obj.getJSONObject(email).getString("password").equals(password)){
                            UserDetail.username = email;
                            UserDetail.password = password;

                            Log.e("FireBase",email);
                            Log.e("FireBase",password);
                            //startActivity(new Intent(LoginActivity.this, Users.class));
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "incorrect password", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);

            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(LoginActivity.this);
        rQueue.add(request);

    }
    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    public static String DecodeString(String string) {
        return string.replace(",", ".");
    }

    /*private void requestRegisterbyFireBase(final String email, final String password){

        final String evalue = EncodeString(email);
        String url = "https://wasapii-9d381.firebaseio.com/users.json";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                Firebase reference = new Firebase("https://wasapii-9d381.firebaseio.com/users");

                if(s.equals("null")) {
                    reference.child(evalue).child("password").setValue(password);
                }
                else {
                    try {
                        JSONObject obj = new JSONObject(s);

                        if (!obj.has(evalue)) {
                            reference.child(evalue).child("password").setValue(password);
                            Log.e("Registration Successful","FirebaseChat");
                        } else {
                            Log.e("Reg. Not Successful","FirebaseChat");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }

        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError );

            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(LoginActivity.this);
        rQueue.add(request);

}*/




    private void requestLogin(final String email, String password) {

        if (!CommonUtilities.isOnline(ctx)) {
            Toast.makeText(ctx, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            return;
        }

        String url = AppGlobalConstants.WEBSERVICE_BASE_URL + "login";

        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject parameters = new JSONObject();

        try {

            String datetime = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").format(new Date());

            parameters.put("email", email);
            parameters.put("password", password);
            parameters.put("user_lat", AppSharedPreferences.getLat(ctx));
            parameters.put("user_long", AppSharedPreferences.getLng(ctx));
            parameters.put("device_token", AppSharedPreferences.getFcmToken(ctx));
            parameters.put("user_timestamp", datetime);

            Log.d("wasapii", " params : " + parameters.toString());

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

                            Log.e("com.adisoft.mls.temp", "satish error response " + res);

                            parseResponse(res);

                        } catch (UnsupportedEncodingException e1) {
                            // Couldn't properly decode data to string
                            e1.printStackTrace();
                        }
                    }

                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("User-agent", "My useragent");

                    return headers;
                }

            };

            jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                    AppGlobalConstants.WEBSERVICE_TIMEOUT_VALUE_IN_MILLIS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            jsonRequest.setShouldCache(false);

            MyApplication.getInstance().addToRequestQueue(jsonRequest);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    public void showSettingsAlert() {

        final android.app.AlertDialog.Builder alertDialog;//= new AlertDialog.Builder(mContext);
        if (isLocationEnabled(LoginActivity.this) == false) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alertDialog = new android.app.AlertDialog.Builder(ctx, android.R.style.Theme_Material_Light_Dialog_Alert);
            } else {
                alertDialog = new android.app.AlertDialog.Builder(ctx);
            }


            // Setting Dialog Title
            alertDialog.setTitle("GPS Settings");

            // Setting Dialog Message
            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

            // On pressing Settings button
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
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

    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    private void parseResponse(String response){

        Log.e("wasapii","response "+response);

        try {

            JSONObject jsonObject = new JSONObject(response);

            int flag = jsonObject.getInt("Flag");

            Log.e("wasapii","response flag "+flag);

            if(flag==1) {

                JSONObject userDetails = jsonObject.has("UserDetail") ? jsonObject.getJSONObject("UserDetail") : null;

                if (userDetails != null) {
                    String userId = userDetails.has("user_id") ? userDetails.getString("user_id") : "";
                    String email = userDetails.has("email") ? userDetails.getString("email") : "";
                    String name = userDetails.has("name") ? userDetails.getString("name") : "";
                    String password = userDetails.has("password") ? userDetails.getString("password") : "";
                    String contactNo = userDetails.has("contact_no") ? userDetails.getString("contact_no") : "";
                    String gender = userDetails.has("gender") ? userDetails.getString("gender") : "";
                    String dob = userDetails.has("dob") ? userDetails.getString("dob") : "";
                    String profileImage = userDetails.has("profile_img") ? userDetails.getString("profile_img") : "";
                    String userInterests = userDetails.has("user_interests") ? userDetails.getString("user_interests") : "";
                    String image1 = userDetails.has("image1") ? userDetails.getString("image1") : "";
                    String image2 = userDetails.has("image2") ? userDetails.getString("image2") : "";
                    String image3 = userDetails.has("image3") ? userDetails.getString("image3") : "";

                    dh.insertUserInfo(userId, name, password, email, contactNo, gender, dob, profileImage, image1, image2, image3, userInterests);

                    AppSharedPreferences.saveUserIDToPreferences(ctx, userId);

                    AppSharedPreferences.setGlobalString(ctx,AppSharedPreferences.KEY_SHARED_PREFERENCE_REMEMBERED_USERNAME, remember_password.isChecked()?et_email.getText().toString().trim():"");
                    AppSharedPreferences.setGlobalString(ctx,AppSharedPreferences.KEY_SHARED_PREFERENCE_REMEMBERED_USER_PASSWORD,  remember_password.isChecked()?et_password.getText().toString().trim():"");


                    Intent ta = new Intent(ctx, ChosePlaceActivity.class);
                    startActivity(ta);
                    finish();
                }

            }else{
                Toast.makeText(ctx,"Invalid email id or password",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void showForgotPasswordDialog() {

        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.setContentView(R.layout.dialog_forgot_password);

        //    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); // In this Case keyboard not appear automatically

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button btn_done = (Button) dialog.findViewById(R.id.btn_submit);
        final EditText email = (EditText) dialog.findViewById(R.id.forgot_password);

        btn_done.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String regemail = email.getText().toString();
                requestForgetPassword(regemail);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private boolean isFacebookLogin(){

        AccessToken token = AccessToken.getCurrentAccessToken();
        return  token!=null;

    }


    private void findViewById(){

        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_signin = (Button) findViewById(R.id.btn_signin);
        btn_signup = (Button) findViewById(R.id.btn_signup);
        btn_signup_with_fb = (LinearLayout) findViewById(R.id.btn_signup_with_fb);
        tv_forgot_password = (TextView) findViewById(R.id.tv_forgot_password);
        tv_facebook = (TextView) findViewById(R.id.facebook);
        remember_password = (CheckBox) findViewById(R.id.forgot_password);
        et_email.setTypeface(regularFont);
        et_password.setTypeface(regularFont);
        btn_signin.setTypeface(boldFont);
        tv_facebook.setTypeface(boldFont);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void initFacebook(){

        FacebookSdk.sdkInitialize(this.getApplicationContext());

//        AppEventsLogger.activateApp(this);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,

                new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code

                        // Log.e("mls","fb login onSucess access token="+loginResult.getAccessToken());

                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),

                                new GraphRequest.GraphJSONObjectCallback() {

                                    @Override
                                    public void onCompleted(
                                            JSONObject user,
                                            GraphResponse response) {
                                        // Application code

                                        Log.v("LoginActivity", "graph ="+response.toString());

                                        social_media_userid_str = user.optString("id");

                                        social_media_gender = user.has("gender")?user.optString("gender"):"";

                                        if (user.has("email")) {
                                            social_media_email_str = user.optString("email").trim();
                                        }

                                        social_media_profile_pic_url = "http://graph.facebook.com/" + social_media_userid_str + "/picture?type=large";

                                        Log.d("com.adisoft.mls.temp","fb profile pic "+social_media_profile_pic_url);

                                        if (user.has("first_name")) {
                                            social_media_firstname = user.optString("first_name");
                                        }

                                        if (user.has("last_name")) {
                                            social_media_lastname = user.optString("last_name");
                                        }


                                        if(social_media_lastname.equalsIgnoreCase("")){
                                            social_media_lastname=" ";
                                        }

                                        if(social_media_email_str.equalsIgnoreCase("")){
                                            showWarning();
                                        }else {


                                             requestRegistration();
                                            //singUpRequest(social_media_firstname, social_media_lastname, social_media_email_str, 2);
                                        }

                                    }
                                });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender, first_name, last_name ");
                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        // App code

                        Log.d("com.adisoft.mls.temp","********onCancel*****");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code

                        Log.d("com.adisoft.mls.temp","********onError*****");
                    }
                });


    }


    private void requestRegistration(){

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

            parameters.put("email",social_media_email_str);
            parameters.put("password","");
            parameters.put("name",social_media_firstname +" "+social_media_lastname);
            parameters.put("contact_no","");
            parameters.put("user_interests","");
            parameters.put("venu_name","");
            parameters.put("user_lat",""+AppSharedPreferences.getLat(ctx));
            parameters.put("user_long",""+AppSharedPreferences.getLng(ctx));
            parameters.put("venu_lat","");
            parameters.put("venu_long","");
            parameters.put("device_token",AppSharedPreferences.getFcmToken(ctx));
            parameters.put("user_timestamp",new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa").format(new Date()));
            parameters.put("signup_type","2");
            parameters.put("profile_img",social_media_profile_pic_url);
            parameters.put("gender",social_media_gender);
            parameters.put("dob","");
            parameters.put("device_type","aos");


            Log.e("wasapiii","signup params= "+parameters.toString());


           /* JsonRequest jsonRequest =  new JsonObjectRequest(Request.Method.POST,url,parameters,new Response.Listener<JSONObject>(){

                @Override
                public void onResponse(JSONObject response) {
                    pDialog.dismiss();

                    parseSignupResponse(response.toString());


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
                            parseSignupResponse(res);

                        } catch (UnsupportedEncodingException e1) {
                            // Couldn't properly decode data to string
                            e1.printStackTrace();
                        }
                    }

                }
            });*/



                RequestQueue requestQueue = Volley.newRequestQueue(this);

                final String mRequestBody = parameters.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("LOG_VOLLEY", response);
                        parseSignupResponse(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("LOG_VOLLEY", error.toString());
                    }
                }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                            return null;
                        }
                    }

                };

                requestQueue.add(stringRequest);



            /*jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                    AppGlobalConstants.WEBSERVICE_TIMEOUT_VALUE_IN_MILLIS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


            MyApplication.getInstance().addToRequestQueue(jsonRequest);*/


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void parseSignupResponse(String response){

        try{

            Log.d("wasapii","response "+response);

            JSONObject jsonObject = new JSONObject(response);

            int flag = jsonObject.has("Flag") ? jsonObject.getInt("Flag"):0;

            String errorMessage = jsonObject.has("Message")? jsonObject.getString("Message"):"Failed try again";

            if(flag==1) {
                JSONObject userDetails = jsonObject.has("UserDetail") ? jsonObject.getJSONObject("UserDetail") : null;
                String userId = "";


                if (userDetails != null) {

                        userId = userDetails.has("user_id") ? userDetails.getString("user_id") : "";
                        String email = userDetails.has("email") ? userDetails.getString("email") : "";
                        String name = userDetails.has("name") ? userDetails.getString("name") : "";
                        String password = userDetails.has("password") ? userDetails.getString("password") : "";
                        String contactNo = userDetails.has("contact_no") ? userDetails.getString("contact_no") : "";
                        String gender = userDetails.has("gender") ? userDetails.getString("gender") : "";
                        String dob = userDetails.has("dob") ? userDetails.getString("dob") : "";
                        String profileImage = userDetails.has("profile_img") ? userDetails.getString("profile_img") : "";
                        String userInterests = userDetails.has("user_interests") ? userDetails.getString("user_interests") : "";
                        String image1 = userDetails.has("image1") ? userDetails.getString("image1") : "";
                        String image2 = userDetails.has("image2") ? userDetails.getString("image2") : "";
                        String image3 = userDetails.has("image3") ? userDetails.getString("image3") : "";
//                    String venueName = userDetails.has("venu_name")?userDetails.getString("venu_name"):"";
//                    String venueLat = userDetails.has("venu_lat")?userDetails.getString("venu_lat"):"";
//                    String venueLong = userDetails.has("venu_long")?userDetails.getString("venu_long"):"";
//                    String userLat = userDetails.has("user_lat")?userDetails.getString("user_lat"):"";
//                    String userLong = userDetails.has("user_long")?userDetails.getString("user_long"):"";
//                    String userTimestamp = userDetails.has("user_timestamp")?userDetails.getString("user_timestamp"):"";
//                    String deviceToken = userDetails.has("device_token")?userDetails.getString("device_token"):"";
//                    String loginStatus = userDetails.has("login_status")?userDetails.getString("login_status"):"";
//                    String signupType = userDetails.has("signup_type")? userDetails.getString("signup_type"):"";

                        dh.insertUserInfo(userId, name, password, email, contactNo, gender, dob, profileImage, image1, image2, image3, userInterests);

                        AppSharedPreferences.saveUserIDToPreferences(ctx, userId);
                        if(userInterests.equals("")) {
                            Intent intent = new Intent(ctx, EditMyProfileActivity.class);
                            intent.putExtra("from where", 1);
                            startActivity(intent);
                            finish();
                        }
                        else if (userInterests.length()>0) {
                            Intent inten = new Intent(ctx, ChosePlaceActivity.class);
                            startActivity(inten);
                            finish();
                        }


                    }
                } else {
                    Toast.makeText(ctx, errorMessage, Toast.LENGTH_SHORT).show();
                }


        }catch (JSONException je){
            je.printStackTrace();
        }

    }

    private void showWarning(){

        new AlertDialog.Builder(this)
                .setMessage("Oops! Looks like something went wrong. Please try to sign in with email id.")
                .setPositiveButton("Ok", null)
                .show();

    }

    public void requestForgetPassword(String s){
        if(!CommonUtilities.isOnline(ctx)){
            Toast.makeText(ctx,R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
            return;
        }

        String url= AppGlobalConstants.WEBSERVICE_BASE_URL + "forgotPassword";

        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject parameters = new JSONObject();

        try {
            parameters.put("email",s);
            Log.e("Email Forget Password",s);


            JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    pDialog.dismiss();

                    parseForgetPasswordResponse(response.toString());

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
                            Log.e("com.adisoft.mls.temp", "satish error response " + res);

                            parseForgetPasswordResponse(res);

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

    public void parseForgetPasswordResponse(String response){
        Log.e("wasapii", "response " + response);

        try {

            JSONObject jsonObject = new JSONObject(response);

            int flag = jsonObject.getInt("Flag");
            String errorMessage = jsonObject.has("Message") ? jsonObject.getString("Message") : "Failed try again";
            String successMessage = jsonObject.has("Message") ? jsonObject.getString("Message") : "Reset password link has been sent in mail";

            if (flag == 1) {
                Toast.makeText(ctx,successMessage , Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ctx, errorMessage, Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
