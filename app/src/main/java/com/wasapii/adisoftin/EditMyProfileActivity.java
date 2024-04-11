package com.wasapii.adisoftin;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wasapii.adisoftin.Constants.Constants;
import com.wasapii.adisoftin.customview.HashTagEditText;
import com.wasapii.adisoftin.customview.RoundedCornersTransformation;
import com.wasapii.adisoftin.db.DataHelper;
import com.wasapii.adisoftin.fragments.TabFragment;
import com.wasapii.adisoftin.model.User;
import com.wasapii.adisoftin.util.AppGlobalConstants;
import com.wasapii.adisoftin.util.AppSharedPreferences;
import com.wasapii.adisoftin.util.CommonUtilities;

import net.steamcrafted.materialiconlib.MaterialIconView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class EditMyProfileActivity extends AppCompatActivity implements View.OnClickListener{

    Toolbar toolbar;
    private Context ctx;
    private ImageView thumbnail_first,thumbnail_second,thumbnail_third,thumbnail_fourth;
    private Button btn_add_image,btn_next;
    private HashTagEditText hashTagInput;
    private TextView music_tag,lifestyle_tag,animals_tag,sports_tag,nonprofit_tag,literature_tag,cinema_tag,
            arts_tag,technology_tag,gaming_tag,entertainment_tag,politics_tag,photography_tag,traveling_tag;
    private EditText et_dob,et_name;
    private RadioGroup rgGender;
    private Calendar myCalendar = Calendar.getInstance();
    private DataHelper dh;
    private RadioButton rb_male,rb_female;
    private static final int CHOICE_AVATAR_FROM_GALLERY1=102;
    private static final int CHOICE_AVATAR_FROM_CAMERA1=202;
    private static final int CHOICE_AVATAR_FROM_GALLERY2 =103;
    private static final int CHOICE_AVATAR_FROM_CAMERA2 =203;
    private static final int CHOICE_AVATAR_FROM_GALLERY3 = 104;
    private static final int CHOICE_AVATAR_FROM_CAMERA3 = 204;
    private Bitmap finalBitmap=null;
    private ImageView profile_image;
    private String base64Image1=null,base64Image2=null,base64Image3=null;
    private   Uri imageUri;
    private ArrayList<Bitmap> bitmapList = new ArrayList<>();
    private String gender="";
    private int fromWhere;

    String age="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_my_profile);

        findViewById();

        ctx = EditMyProfileActivity.this;

        dh = DataHelper.getInstance(ctx);

        initToolBar();

        fromWhere = getIntent().getIntExtra("from where",0);

        setOnclickListener();

        showDatePicker();

        showUserInfo();
        getAgeRequestUpdate();

    }

    private void showUserInfo(){

        User user = dh.getUserInfo();

        if(user!= null) {

            et_name.setText(user.getName());

            //et_dob.setText(""+user.getDob());



            gender = user.getGender();

            if(user.getGender().equalsIgnoreCase("male")){
                rb_male.setChecked(true);
            }else{
                rb_female.setChecked(true);
            }

            String [] list = user.getInterests().split(",");

            for(String value : list) {

                if(hashTagInput.getText().toString().isEmpty()){
                       hashTagInput.setText(""+value+" ");
                }else{
                    String str = hashTagInput.getText().toString()+""+value+" ";
                    hashTagInput.setText(str);
                }
            }

            hashTagInput.setText(user.getInterests().replaceAll(",",""));

            Glide.with(ctx)
                    .load(user.getProfileImgUrl())
                    .placeholder(R.drawable.no_photo_available_icon)
                    .error(R.drawable.no_photo_available_icon)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .thumbnail(0.3f)
                    .bitmapTransform(new RoundedCornersTransformation( ctx,15,2, RoundedCornersTransformation.CornerType.TOP))
                    .into(profile_image);


            Glide.with(ctx)
                    .load(user.getProfileImgUrl())
                    .placeholder(R.drawable.no_photo_available_icon)
                    .error(R.drawable.no_photo_available_icon)
                    .thumbnail(0.3f)
                    .bitmapTransform(new RoundedCornersTransformation( ctx,15,2, RoundedCornersTransformation.CornerType.TOP))
                    .into(thumbnail_first);

            if(!user.getImg1().equalsIgnoreCase("")){

                base64Image1 = user.getImg1();
                Glide.with(ctx)
                        .load(user.getImg1())
                        .placeholder(R.drawable.no_photo_available_icon)
                        .error(R.drawable.no_photo_available_icon)
                        .thumbnail(0.3f)
                        .bitmapTransform(new RoundedCornersTransformation( ctx,15,2, RoundedCornersTransformation.CornerType.TOP))
                        .into(thumbnail_second);
                thumbnail_second.setVisibility(View.VISIBLE);
            }

            if(!user.getImg2().equalsIgnoreCase("")){

                base64Image2 = user.getImg2();

                Glide.with(ctx)
                        .load(user.getImg2())
                        .placeholder(R.drawable.gradiant_background)
                        .error(R.drawable.gradiant_background)
                        .thumbnail(0.3f)
                        .bitmapTransform(new RoundedCornersTransformation( ctx,15,2, RoundedCornersTransformation.CornerType.TOP))
                        .into(thumbnail_third);

                thumbnail_third.setVisibility(View.VISIBLE);
            }

            if(!user.getImg3().equalsIgnoreCase("")){

                base64Image3 = user.getImg3();

                Glide.with(ctx)
                        .load(user.getImg3())
                        .placeholder(R.drawable.gradiant_background)
                        .error(R.drawable.gradiant_background)
                        .thumbnail(0.3f)
                        .bitmapTransform(new RoundedCornersTransformation( ctx,15,2, RoundedCornersTransformation.CornerType.TOP))
                        .into(thumbnail_fourth);

                thumbnail_fourth.setVisibility(View.VISIBLE);
            }



        }
    }

    private void setOnclickListener(){

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
        btn_add_image.setOnClickListener(this);
        btn_next.setOnClickListener(this);

        thumbnail_first.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


            }
        });

        thumbnail_second.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(bitmapList.size()>0) {
                    Bitmap bm = bitmapList.get(0);
                    profile_image.setImageBitmap(bm);

                }else if(base64Image2!=null){

                    Glide.with(ctx)
                            .load(base64Image2)
                            .placeholder(R.drawable.gradiant_background)
                            .error(R.drawable.gradiant_background)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .bitmapTransform(new RoundedCornersTransformation( ctx,15,2, RoundedCornersTransformation.CornerType.TOP))
                            .into(profile_image);
                }
            }
        });

        thumbnail_third.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(bitmapList.size()>1) {
                    Bitmap bm = bitmapList.get(1);
                    profile_image.setImageBitmap(bm);
                }else if(base64Image1!=null){

                    Glide.with(ctx)
                            .load(base64Image1)
                            .placeholder(R.drawable.gradiant_background)
                            .error(R.drawable.gradiant_background)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .bitmapTransform(new RoundedCornersTransformation( ctx,15,2, RoundedCornersTransformation.CornerType.TOP))
                            .into(profile_image);
                }
            }
        });

        thumbnail_fourth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(bitmapList.size()>2) {

                    Bitmap bm = bitmapList.get(2);
                    profile_image.setImageBitmap(bm);

                }else if(base64Image3!=null){

                    Glide.with(ctx)
                            .load(base64Image3)
                            .placeholder(R.drawable.gradiant_background)
                            .error(R.drawable.gradiant_background)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .bitmapTransform(new RoundedCornersTransformation( ctx,15,2, RoundedCornersTransformation.CornerType.TOP))
                            .into(profile_image);
                }
            }
        });


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

            case R.id.btn_add_image:
                actionAddImage();
                break;

            case R.id.btn_next:
                requestUpdate();
                break;

        }
    }


    private void actionAddImage(){

        if(thumbnail_second.getVisibility()==View.GONE){

            if(isAllPermissionsGranted(ctx)){
                createImagePickerOption(1);
            }

        }else if(thumbnail_third.getVisibility()==View.GONE){

            thumbnail_third.setVisibility(View.VISIBLE);
            if(isAllPermissionsGranted(ctx)){
                createImagePickerOption(2);
            }

        }else if(thumbnail_fourth.getVisibility()==View.GONE){

          thumbnail_fourth.setVisibility(View.VISIBLE);
            if(isAllPermissionsGranted(ctx)){
                createImagePickerOption(3);
            }
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

    private void findViewById(){

        profile_image = (ImageView) findViewById(R.id.profile_image);
        et_dob = (EditText) findViewById(R.id.et_dob);
        et_name = (EditText) findViewById(R.id.et_name);
        rgGender = (RadioGroup) findViewById(R.id.rg_gender);
        rb_female = (RadioButton) findViewById(R.id.rb_female);
        rb_male = (RadioButton) findViewById(R.id.rb_male);
        hashTagInput = (HashTagEditText) findViewById(R.id.tag_input);
        thumbnail_first = (ImageView) findViewById(R.id.thumbnail_first);
        thumbnail_second = (ImageView) findViewById(R.id.thumbnail_second);
        thumbnail_third = (ImageView) findViewById(R.id.thumbnail_third);
        thumbnail_fourth = (ImageView) findViewById(R.id.thumbnail_fourth);
        btn_add_image = (Button) findViewById(R.id.btn_add_image);
        btn_next = (Button) findViewById(R.id.btn_next);
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

    }

    public void initToolBar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);

        MaterialIconView btn_back = (MaterialIconView) toolbar.findViewById(R.id.btn_back);

        title.setText("Edit Profile");

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

    public void getAgeRequestUpdate(){
        if(!CommonUtilities.isOnline(ctx)){
            Toast.makeText(ctx,R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
            return;
        }

        String url= AppGlobalConstants.WEBSERVICE_BASE_URL+"single_user_detail";

        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject parameters = new JSONObject();

        try {

            //parameters.put("user_id",userId);
            parameters.put("user_id",AppSharedPreferences.loadUserIDFromPreference(ctx));

            JsonRequest jsonRequest =  new JsonObjectRequest(Request.Method.POST,url,parameters,new Response.Listener<JSONObject>(){

                @Override
                public void onResponse(JSONObject response) {
                    pDialog.dismiss();

                    parseAgeResponse(response.toString());

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

                            Log.e("com.adisoft.mls.temp","satish error response "+res);

                            parseAgeResponse(response.toString());

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

    private void parseAgeResponse(String response){

        Log.e("wasapii","response "+response);

        try {

            JSONObject jsonObject = new JSONObject(response);

            int flag  = jsonObject.getInt("Flag");
            String errorMessage = jsonObject.has("Message") ? jsonObject.getString("Message"):"Invalid email id";

            if(flag==1){

                JSONObject userDetail = jsonObject.getJSONObject("UserDetail");
                age = userDetail.getString("age");
                Log.e("Age",age);
                et_dob.setText(age);


            }else{
                Toast.makeText(ctx,errorMessage,Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    private void requestUpdate(){

        if(!CommonUtilities.isOnline(ctx)){
            Toast.makeText(ctx,R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
            return;
        }

        String url= AppGlobalConstants.WEBSERVICE_BASE_URL+"update_profile";

        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject parameters = new JSONObject();

        try {

            if(rb_male.isChecked()){
                gender = "male";
            }else {
                gender = "female";
            }

//            {"user_id":"1","user_interests":"cooking","user_lat":"22.456789",
// "user_long":"75.789456","user_timestamp": "2017-03-28 06:59:22",
// "age":"26","name":"Rinu","gender":"Male","image1":"123456",
// "image2":"","image3":"","image4":""}


            parameters.put("user_id",AppSharedPreferences.loadUserIDFromPreference(ctx));
            parameters.put("name",et_name.getText());
            parameters.put("user_interests",hashTagInput.getValues().toString().replace("[","").replace("]",""));
            parameters.put("user_lat",AppSharedPreferences.getLat(ctx));
            parameters.put("user_long",AppSharedPreferences.getLng(ctx));
            parameters.put("user_timestamp",new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa").format(new Date()));
            parameters.put("image1",AppSharedPreferences.getProfilePic(ctx));
            parameters.put("gender",gender);
            parameters.put("age",et_dob.getText().toString().trim());
            parameters.put("image2",base64Image1);
            parameters.put("image3",base64Image2);
            parameters.put("image4",base64Image3);

            Log.e("wasapii","signup params "+parameters.toString());


            RequestQueue requestQueue = Volley.newRequestQueue(this);

            final String mRequestBody = parameters.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("LOG_VOLLEY", response);
                    parseResponse(response);
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

                if(userDetails!=null) {

                    String name = userDetails.has("name")?userDetails.getString("name"):"";
                    String gender = userDetails.has("gender")?userDetails.getString("gender"):"";
                    String dob = userDetails.has("dob")?userDetails.getString("dob"):"";
                    String profileImage = userDetails.has("profile_img")?userDetails.getString("profile_img"):"";
                    String userInterests = userDetails.has("user_interests")?userDetails.getString("user_interests"):"";
                    String image1 = userDetails.has("image1")?userDetails.getString("image1"):"";
                    String image2 = userDetails.has("image2")?userDetails.getString("image2"):"";
                    String image3 = userDetails.has("image3")?userDetails.getString("image3"):"";


                    dh.updateUserInfo(name,gender,dob,profileImage,image1,image2,image3,userInterests);

                    Toast.makeText(ctx,"Profile updated successfully",Toast.LENGTH_SHORT).show();


                    if(fromWhere==1) {

                        Intent i = new Intent(EditMyProfileActivity.this, ChosePlaceActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();

                    }
                    else{
                        Intent i = new Intent(EditMyProfileActivity.this, HomeActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                    }
                }

            }else {
                Toast.makeText(ctx,errorMessage,Toast.LENGTH_SHORT).show();
            }

        }catch (JSONException je){
            je.printStackTrace();
        }

    }


    private void showDatePicker(){


        final DatePickerDialog.OnDateSetListener startDate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat sdf = new SimpleDateFormat(Constants.MDY);

                et_dob.setText(sdf.format(myCalendar.getTime()));

//                if(myCalendar.getTime().before(new Date())) {
//                    et_dob.setText(sdf.format(new Date()));
//                }else{
//                    et_dob.setText(sdf.format(myCalendar.getTime()));
//                }

            }

        };


       /* et_dob.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction()==MotionEvent.ACTION_UP) {

                    DatePickerDialog datePickerDialog =  new DatePickerDialog(ctx, startDate, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH));//.show();

                    datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);

                    datePickerDialog.show();
                }

                return false;
            }
        });*/

    }


    public boolean isAllPermissionsGranted(Context ctx) {

        if (Build.VERSION.SDK_INT >= 23) {

            if (ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;

            } else {

                ArrayList<String> unGrantedPermissionsList = new ArrayList<>();

                if(ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    unGrantedPermissionsList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }


                if (ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    unGrantedPermissionsList.add(android.Manifest.permission.CAMERA);
                }

                String[] permissionsArray = new String[unGrantedPermissionsList.size()];
                permissionsArray = unGrantedPermissionsList.toArray(permissionsArray);

                ActivityCompat.requestPermissions(EditMyProfileActivity.this, permissionsArray, 108);

                return false;
            }

        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("com.adisoft.mls.temp", "Permission is granted");
            return true;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Map<String, Integer> perms = new HashMap<String, Integer>();

        // perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
        perms.put(android.Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
        perms.put(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

        // Fill with results
        for (int i = 0; i < permissions.length; i++)
            perms.put(permissions[i], grantResults[i]);

        if (perms.get(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && perms.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            createImagePickerOption(1);
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void createImagePickerOption(final  int imageNumber) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

        builder.setTitle("Choose Image Source");
        try {

            builder.setItems(new CharSequence[]{"Gallery", "Camera"},

                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            switch (which) {

                                case 0:
                                    // GET IMAGE FROM THE GALLERY
                                    //Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,
                                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    photoPickerIntent.setType("image/*");

                                    if (imageNumber == 1) {
                                        startActivityForResult(photoPickerIntent, CHOICE_AVATAR_FROM_GALLERY1);
                                    } else if (imageNumber == 2) {
                                        startActivityForResult(photoPickerIntent, CHOICE_AVATAR_FROM_GALLERY2);
                                    } else if (imageNumber == 3) {
                                        startActivityForResult(photoPickerIntent, CHOICE_AVATAR_FROM_GALLERY3);
                                    }

                                    break;

                                case 1:

                               /*Intent getCameraImage = new Intent("android.media.action.IMAGE_CAPTURE");

                                File cameraFolder;

                                if (Environment.getExternalStorageState().equals
                                        (Environment.MEDIA_MOUNTED))
                                    cameraFolder = new File(Environment.getExternalStorageDirectory(),
                                            "wasapii/");
                                else
                                    cameraFolder= ctx.getCacheDir();
                                if(!cameraFolder.exists())
                                    cameraFolder.mkdirs();

                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
                                String timeStamp = dateFormat.format(new Date());
                                String imageFileName = "picture_" + timeStamp + ".jpg";

                                File photo = new File(Environment.getExternalStorageDirectory(),
                                        "wasapii/" + imageFileName);
                                getCameraImage.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                                imageUri = Uri.fromFile(photo);

                                if(imageNumber==1) {
                                    startActivityForResult(getCameraImage, CHOICE_AVATAR_FROM_CAMERA1);
                                }else if(imageNumber==2) {
                                    startActivityForResult(getCameraImage, CHOICE_AVATAR_FROM_CAMERA2);
                                }else if(imageNumber==3) {
                                    startActivityForResult(getCameraImage, CHOICE_AVATAR_FROM_CAMERA3);
                                }*/


                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    imageUri = Uri.fromFile(getOutputMediaFile());
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                                    //startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

//                                    if (getCameraImage.resolveActivity(getPackageManager()) != null) {
                                        if (imageNumber == 1) {
                                            startActivityForResult(intent, CHOICE_AVATAR_FROM_CAMERA1);
                                        } else if (imageNumber == 2) {
                                            startActivityForResult(intent, CHOICE_AVATAR_FROM_CAMERA2);
                                        } else if (imageNumber == 3) {
                                            startActivityForResult(intent, CHOICE_AVATAR_FROM_CAMERA3);
                                        }

                                    break;

                                default:
                                    break;
                            }
                        }
                    });

            builder.show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Wasapii");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }


        return new File(mediaStorageDir.getPath() + File.separator +
                "wasapii_"+ (System.currentTimeMillis()/1000) + "_img.jpg");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {

            switch(requestCode) {

                case CHOICE_AVATAR_FROM_GALLERY1:

                    try {

                        final Uri imageUri = data.getData();
                        //Log.i("calm","image uri orientation "+getOrientation(getRealPathFromURI(imageUri)));
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = rotateBitmap(BitmapFactory.decodeStream(imageStream), getOrientation(getFilePath(ctx,imageUri)));
                        finalBitmap = getResizedBitmap(selectedImage, 200);//Bitmap.createScaledBitmap(selectedImage, 150, 150, true);
                        profile_image.setImageBitmap(finalBitmap);
                        thumbnail_second.setImageBitmap(finalBitmap);
                        bitmapList.add(0,finalBitmap);

                        base64Image1 = new String(convertBitmapToBase64(finalBitmap));

                        thumbnail_second.setVisibility(View.VISIBLE);


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    break;

                case CHOICE_AVATAR_FROM_CAMERA1:

                    try {

                        Uri selectedImage = imageUri;

                        Log.e("wasapii","selected image uri="+selectedImage);
                        String filePath = getFilePath(ctx,selectedImage);

                        //getContentResolver().notifyChange(selectedImage, null);

                        //ContentResolver cr = getContentResolver();

                        Bitmap bitmap = BitmapFactory.decodeFile(filePath);

                        bitmap = getCorrectBitmap(bitmap, filePath);


                        //Bitmap bitmap = BitmapFactory.decodeFile(getFilePath(ctx,selectedImage));

//                        bitmap = rotateBitmap(MediaStore.Images.Media.getBitmap(cr, selectedImage), 0);

                        finalBitmap = getResizedBitmap(bitmap, 200);//Bitmap.createScaledBitmap(bitmap, 150, 150, true);

                        profile_image.setImageBitmap(finalBitmap);

                        thumbnail_second.setImageBitmap(finalBitmap);

                        bitmapList.add(0,finalBitmap);

                        base64Image1 = new String(convertBitmapToBase64(finalBitmap));

                        thumbnail_second.setVisibility(View.VISIBLE);


                      /*  Bundle extras = data.getExtras();
                        finalBitmap = (Bitmap) extras.get("data");
                        profile_image.setImageBitmap(finalBitmap);
                        thumbnail_second.setImageBitmap(finalBitmap);
                        bitmapList.add(0,finalBitmap);
                        base64Image1 = new String(convertBitmapToBase64(finalBitmap));
                        thumbnail_second.setVisibility(View.VISIBLE);*/


                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }


                case CHOICE_AVATAR_FROM_GALLERY2:

                    try {

                        final Uri imageUri = data.getData();
                        //                        Log.i("calm","image uri orientation "+getOrientation(getRealPathFromURI(imageUri)));
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = rotateBitmap(BitmapFactory.decodeStream(imageStream), getOrientation(getFilePath(ctx,imageUri)));
                        finalBitmap = getResizedBitmap(selectedImage, 200);//Bitmap.createScaledBitmap(selectedImage, 150, 150, true);
                        profile_image.setImageBitmap(finalBitmap);

                        thumbnail_third.setImageBitmap(finalBitmap);

                        bitmapList.add(1,finalBitmap);

                        base64Image2 = new String(convertBitmapToBase64(finalBitmap));

                        thumbnail_third.setVisibility(View.VISIBLE);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    break;

                case CHOICE_AVATAR_FROM_CAMERA2:

                    try {

                        Uri selectedImage = imageUri;

                        Log.e("wasapii","selected image uri="+selectedImage);
                        String filePath = getFilePath(ctx,selectedImage);
                        Bitmap bitmap;
                        bitmap = BitmapFactory.decodeFile(filePath);
                        bitmap = getCorrectBitmap(bitmap, filePath);
                        //bitmap = rotateBitmap(MediaStore.Images.Media.getBitmap(cr, selectedImage), 0);

                        finalBitmap = getResizedBitmap(bitmap, 200);//Bitmap.createScaledBitmap(bitmap, 150, 150, true);

                        profile_image.setImageBitmap(finalBitmap);

                        thumbnail_third.setImageBitmap(finalBitmap);

                        bitmapList.add(1,finalBitmap);

                        base64Image2= new String(convertBitmapToBase64(finalBitmap));

                        thumbnail_third.setVisibility(View.VISIBLE);

                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                break;
                case CHOICE_AVATAR_FROM_GALLERY3:

                    try {

                        final Uri imageUri = data.getData();
                        //                        Log.i("calm","image uri orientation "+getOrientation(getRealPathFromURI(imageUri)));
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = rotateBitmap(BitmapFactory.decodeStream(imageStream), getOrientation(getFilePath(ctx,imageUri)));
                        finalBitmap = getResizedBitmap(selectedImage, 200);//Bitmap.createScaledBitmap(selectedImage, 150, 150, true);
                        profile_image.setImageBitmap(finalBitmap);

                        thumbnail_fourth.setImageBitmap(finalBitmap);
                        if(bitmapList.size() >=3) {
                            bitmapList.add(2, finalBitmap);
                        } else {
                            bitmapList.add(finalBitmap);
                        }
                        base64Image3 = new String(convertBitmapToBase64(finalBitmap));

                        thumbnail_fourth.setVisibility(View.VISIBLE);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

                case CHOICE_AVATAR_FROM_CAMERA3:

                    try {

                        Uri selectedImage = imageUri;

                        Log.e("wasapii","selected image uri="+selectedImage);
                        String filePath = getFilePath(ctx,selectedImage);
                       // ContentResolver cr = getContentResolver();

                        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                        bitmap = getCorrectBitmap(bitmap, filePath);
                        //bitmap = rotateBitmap(MediaStore.Images.Media.getBitmap(cr, selectedImage), 0);

                        finalBitmap = getResizedBitmap(bitmap, 200);//Bitmap.createScaledBitmap(bitmap, 150, 150, true);

                        profile_image.setImageBitmap(finalBitmap);

                        thumbnail_fourth.setImageBitmap(finalBitmap);

                        bitmapList.add(2,finalBitmap);

                        base64Image3 = new String(convertBitmapToBase64(finalBitmap));

                        thumbnail_fourth.setVisibility(View.VISIBLE);

                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

            }
        }
    }

//    public String getRealPathFromURI(Uri uri) {
//        try {
//            String[] projection = {MediaStore.Images.Media.DATA};
//            @SuppressWarnings("deprecation")
//            Cursor cursor = ctx.getContentResolver().query(uri, projection, null, null, null);
//            int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
//            return cursor.getString(column_index);
//        }catch (Exception ne){
//            ne.printStackTrace();
//        }
//        return "";
//    }

    public Bitmap getCorrectBitmap(Bitmap bitmap, String filePath) {
        ExifInterface ei;
        Bitmap rotatedBitmap = bitmap;
        try {
            ei = new ExifInterface(filePath);

            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
            }

            rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return rotatedBitmap;
    }



//For Marshmallow version

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getFilePath(final Context context, final Uri uri)
    {
        final boolean isKitKatOrAbove = Build.VERSION.SDK_INT >=  Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKatOrAbove && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];


//                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                return getDirectory("SECONDARY_STORAGE", "/sdcard") + "/" + split[1];

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    public static File getDirectory(String variableName, String defaultPath) {
        String path = System.getenv(variableName);
        return path == null ? new File(defaultPath) : new File(path);
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }







    public int getOrientation(String filepath) {

        Log.d("wasapii","getOrientation "+filepath);

        try{
            ExifInterface exif = new ExifInterface(filepath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
//            Log.d("calm","orentation "+orientation);
            return  orientation;

        }catch (IOException e){
            e.printStackTrace();
        }

        return 0;

    }

    public  Bitmap rotateBitmap(Bitmap source, int orientation) {

        int angle = 0;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                angle = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                angle = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                angle = 270;
                break;
            default:
                angle = 0;
                break;
        }

        Log.e("wasapii","orientation="+orientation);

        Matrix mat = new Matrix();

        /*if (angle == 0 && source.getWidth() > source.getHeight())
            mat.postRotate(0);//(90);
        else*/
            mat.postRotate(angle);

        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), mat, true);
    }

    /**
     * reduces the size of the image
     * @param image
     * @param maxSize
     * @return
     */
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public String convertBitmapToBase64(Bitmap bitmap){

        String convertedString="";

        if(bitmap==null){
            return convertedString;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        convertedString = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return convertedString;
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (finalBitmap != null) {
            outState.putString("cameraImageUri", "");
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey("cameraImageUri")) {

//            imageUri = Uri.parse(savedInstanceState.getString("cameraImageUri"));

            if (finalBitmap != null) {

                profile_image.setImageBitmap(finalBitmap);

                base64Image1 = convertBitmapToBase64(finalBitmap);

            }
        }
    }
}
