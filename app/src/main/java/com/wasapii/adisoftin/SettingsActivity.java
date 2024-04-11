package com.wasapii.adisoftin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.wasapii.adisoftin.db.DataHelper;
import com.wasapii.adisoftin.util.AppGlobalConstants;
import com.wasapii.adisoftin.util.AppSharedPreferences;
import com.wasapii.adisoftin.util.CommonUtilities;

import net.steamcrafted.materialiconlib.MaterialIconView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class SettingsActivity extends AppCompatActivity {

    Toolbar toolbar;
    private Context ctx;
    EditText et_mobile,et_old_password,et_new_password,et_confirm_password;
    Button btn_delete,btn_save;
    private DataHelper dh;
    String et_old,new_pass,oldpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        ctx = SettingsActivity.this;
        dh = DataHelper.getInstance(ctx);

        initToolBar();

        et_mobile = (EditText) findViewById(R.id.et_mobile);
        et_old_password = (EditText) findViewById(R.id.et_old_password);
        et_new_password = (EditText) findViewById(R.id.et_new_password);
        et_confirm_password = (EditText) findViewById(R.id.et_confirm_password);

        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_save = (Button) findViewById(R.id.btn_save);

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestDeleteAccount();
            }
        });

        /*et_mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() > 0) {
                    et_mobile.setError(null);
                } else {
                    et_mobile.setError("Please fill Mobile Number");
                }

            }
        });*/

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(et_mobile.length()!= 10){
                    et_mobile.setError("Mobile Number Should be 10 digits");

                }
                else{
                    et_mobile.setError(null);


                }
                if(et_old_password.length() > 0){
                    et_old_password.setError(null);

                }else{

                    et_old_password.setError("Enter Your Old Password");
                }
                if(et_new_password.length() > 0){
                    et_new_password.setError(null);
                }else {

                    et_new_password.setError("Enter Your New Password");
                }
                if(et_confirm_password.length() > 0){
                    et_confirm_password.setError(null);
                }
                else{

                    et_confirm_password.setError("Enter Your Confirm Password");
                }
                if((et_mobile.length()== 10) && (et_old_password.length() > 0) && (et_new_password.length() > 0) && (et_confirm_password.length() > 0)){
                    requestGetPassword();
                    //Toast.makeText(ctx, "Data Updated Successfully", Toast.LENGTH_LONG).show();
                    Intent in = new Intent(SettingsActivity.this,HomeActivity.class);
                    startActivity(in);
                }
//                else {
//                    et_mobile.setError("Mobile Number Should be 10 digits");
//                    et_old_password.setError("Enter Your Old Password");
//                    et_new_password.setError("Enter Your New Password");
//                    et_confirm_password.setError("Enter Your Confirm Password");


                
            }
        });

        et_old_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){

                    et_old = et_old_password.getText().toString();


                }

            }
        });

        et_new_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    new_pass = et_new_password.getText().toString();
                    if(new_pass.isEmpty()){
                        et_new_password.setError("Please fill credentials");
                    }
                }
            }
        });


        /*et_confirm_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(!hasFocus) {
                    String confirm_pass = et_confirm_password.getText().toString();
                    Log.e("Confirm Pass",confirm_pass);
                    if (confirm_pass.isEmpty() || confirm_pass.equals(new_pass)) {
                        Toast.makeText(ctx, "Password match", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ctx, "Password do not match", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });*/

        et_confirm_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String confirm_pass = et_confirm_password.getText().toString();
                Log.e("Confirm Pass",confirm_pass);
                if (confirm_pass.isEmpty() || confirm_pass.equals(new_pass)) {
                    Toast.makeText(ctx, "Password match", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ctx, "Password do not match", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        MaterialIconView btn_back = (MaterialIconView) toolbar.findViewById(R.id.btn_back);

        title.setText("Settings");

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }

        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {


            Window window = getWindow();
            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // finally change the color
            window.setStatusBarColor(Color.BLACK);//(ctx.getResources().getColor(R.color.statusbar));
        }


    }




    public void requestGetPassword(){

        if(!CommonUtilities.isOnline(ctx)){
            Toast.makeText(ctx,R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
            return;
        }

        String url= AppGlobalConstants.WEBSERVICE_BASE_URL+"setting";


        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject parameters = new JSONObject();

        //Parameter :
        //{"user_id":"12","contact_no":"8888888888","old_pass":"1234567","new_pass":"123456"}


        try {
            parameters.put("user_id",AppSharedPreferences.loadUserIDFromPreference(ctx));
            parameters.put("contact_no",et_mobile.getText().toString());
            parameters.put("old_pass",et_old_password.getText().toString());
            parameters.put("new_pass",et_new_password.getText());

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

                    Log.e("Password","Jyoti error response "+response);

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

                            Log.e("com.adisoft.mls.temp","New error response "+res);

                            parseResponse(res);

                            JSONObject jsonObject = new JSONObject(res);
                            int flag  = jsonObject.getInt("Flag");
                            if(flag==0){
                                et_old_password.setError("Password InCorrect");
                            }

                        } catch (UnsupportedEncodingException e1) {
                            // Couldn't properly decode data to string
                            e1.printStackTrace();
                        }
                        catch (JSONException e){
                            e.printStackTrace();
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

        Log.e("wasapii", "response " + response);

        try {
            JSONObject jsonObject = new JSONObject(response);
            int flag  = jsonObject.getInt("Flag");
            String errorMessage = jsonObject.has("Message") ? jsonObject.getString("Message"):"Invalid Details";

            if(flag==1) {

                JSONObject userDetails = jsonObject.has("UserDetail") ? jsonObject.getJSONObject("UserDetail"):null;
                Log.e("JSONObject",userDetails+"");

                String id = userDetails.getString("user_id");
                Log.e("User ID",id);
                if(id != null) {
                    String contact_no = userDetails.has("contact_no")?userDetails.getString("contact_no"):"";
                    String old_pass = userDetails.has("old_pass")?userDetails.getString("old_pass"):"";
                    new_pass = userDetails.has("new_pass")?userDetails.getString("new_pass"):"";

                }

            }

            }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void requestDeleteAccount(){
        if(!CommonUtilities.isOnline(ctx)){
            Toast.makeText(ctx,R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
            return;
        }

        String url= AppGlobalConstants.WEBSERVICE_BASE_URL+"delete_account";


        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject parameters = new JSONObject();

        //Parameter :
        //{"user_id":"12","contact_no":"8888888888","old_pass":"1234567","new_pass":"123456"}


        try {
            parameters.put("user_id",AppSharedPreferences.loadUserIDFromPreference(ctx));


            JsonRequest jsonRequest =  new JsonObjectRequest(Request.Method.POST,url,parameters,new Response.Listener<JSONObject>(){

                @Override
                public void onResponse(JSONObject response) {
                    pDialog.dismiss();

                    parseDeleteResponse(response.toString());

                }

            },new Response.ErrorListener(){

                @Override
                public void onErrorResponse(VolleyError error) {

                    pDialog.dismiss();

                    NetworkResponse response = error.networkResponse;

                    Log.e("Password","Jyoti error response "+response);

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

                            Log.e("com.adisoft.mls.temp","New error response "+res);

                            parseDeleteResponse(res);


                        } catch (UnsupportedEncodingException e1) {
                            // Couldn't properly decode data to string
                            e1.printStackTrace();
                        }
                        catch (Exception e){
                            e.printStackTrace();
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
    public void parseDeleteResponse(String response){
        Log.e("wasapii", "response " + response);

        try {
            JSONObject jsonObject = new JSONObject(response);
            int flag  = jsonObject.getInt("Flag");
            Log.e("DeleteFlag",flag+"");
            if(flag==1) {
                Toast.makeText(ctx,"Your Account Has been deleted",Toast.LENGTH_LONG).show();
                Intent in = new Intent(ctx,LoginActivity.class);
                startActivity(in);
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
