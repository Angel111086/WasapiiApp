package com.wasapii.adisoftin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
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
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.firebase.client.Firebase;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import com.firebase.client.utilities.Base64;
import com.google.firebase.database.DatabaseReference;
import com.wasapii.adisoftin.adapters.ChatRecyclerAdapter;
import com.wasapii.adisoftin.db.DataHelper;
import com.wasapii.adisoftin.fragments.ChatFragment;
import com.wasapii.adisoftin.model.Chat;
import com.wasapii.adisoftin.model.User;
import com.wasapii.adisoftin.model.UserDetail;
import com.wasapii.adisoftin.util.AppGlobalConstants;
import com.wasapii.adisoftin.util.AppSharedPreferences;
import com.wasapii.adisoftin.util.CommonUtilities;

import net.steamcrafted.materialiconlib.MaterialIconView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by root on 2/6/17.
 */

public class ChatActivity extends AppCompatActivity {

    Toolbar toolbar;
    private Context ctx;
    ImageView btn_like,btn_send,btn_meet;
    EditText et_message;
    ChatFragment fr;
    String rece_id="";
    LinearLayout layout;
    ScrollView scrollView;
    Firebase reference1, reference2;
    String name="",profile="";
    TextView title;
    CircleImageView profile_pic;
    private int type=1;
    double currlat,currlng;
    //RecyclerView recycler_view_chat;
    Map<String, String> map = new HashMap<String, String>();



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        ctx = ChatActivity.this;
        btn_like = (ImageView) findViewById(R.id.btn_like);
        btn_meet = (ImageView) findViewById(R.id.btn_meet);
        btn_send = (ImageView) findViewById(R.id.btn_send);
        et_message = (EditText) findViewById(R.id.et_message);
        layout = (LinearLayout)findViewById(R.id.layout1);
        scrollView = (ScrollView)findViewById(R.id.scrollView);

        Firebase.setAndroidContext(this);
        initToolBar();

        Intent in = getIntent();
        if (in.hasExtra("ChatUserID")) {
            rece_id = in.getStringExtra("ChatUserID");
            UserDetail.chatWith=rece_id;
            UserDetail.username = AppSharedPreferences.loadUserIDFromPreference(ctx);
            Log.e("RECE", rece_id);
            reference1 = new Firebase("https://wasapii-faab8.firebaseio.com//messages/" + UserDetail.username + "_" + rece_id);
            Log.e("UserNameChat",UserDetail.username);
            Log.e("UserChatWith",UserDetail.chatWith);
            reference2 = new Firebase("https://wasapii-faab8.firebaseio.com//messages/" + rece_id + "_" + UserDetail.username);
        }
        if(in.hasExtra("SentMeetUserId")) {
            rece_id = in.getStringExtra("SentMeetUserId");
            UserDetail.chatWith=rece_id;
            UserDetail.username = AppSharedPreferences.loadUserIDFromPreference(ctx);
            Log.e("SentRECE", rece_id);
            reference1 = new Firebase("https://wasapii-faab8.firebaseio.com//messages/" + UserDetail.username + "_" + rece_id);
            reference2 = new Firebase("https://wasapii-faab8.firebaseio.com//messages/" + rece_id + "_" + UserDetail.username);

        }
        if(in.hasExtra("AcceptedMeetUserId")) {
            rece_id = in.getStringExtra("AcceptedMeetUserId");
            UserDetail.chatWith=rece_id;
            UserDetail.username = AppSharedPreferences.loadUserIDFromPreference(ctx);
            Log.e("AcceptedRECE", rece_id);
            reference1 = new Firebase("https://wasapii-faab8.firebaseio.com//messages/" + UserDetail.username + "_" + rece_id);
            reference2 = new Firebase("https://wasapii-faab8.firebaseio.com//messages/" + rece_id + "_" + UserDetail.username);

        }
        if(in.hasExtra("ViewUserId")) {
            rece_id = in.getStringExtra("ViewUserId");
            UserDetail.chatWith=rece_id;
            UserDetail.username = AppSharedPreferences.loadUserIDFromPreference(ctx);
            Log.e("ViewRECE", rece_id);
            reference1 = new Firebase("https://wasapii-faab8.firebaseio.com//messages/" + UserDetail.username + "_" + rece_id);
            reference2 = new Firebase("https://wasapii-faab8.firebaseio.com//messages/" + rece_id + "_" + UserDetail.username);

        }
        if(in.hasExtra("Contact") && in.hasExtra("AcceptedMeetUserId")){
            rece_id = in.getStringExtra("AcceptedMeetUserId");
            UserDetail.contact = in.getStringExtra("Contact");
            UserDetail.chatWith=rece_id;
            UserDetail.username = AppSharedPreferences.loadUserIDFromPreference(ctx);
            Log.e("AcceptedRECE", rece_id);
            reference1 = new Firebase("https://wasapii-faab8.firebaseio.com//messages/" + UserDetail.username + "_" + rece_id );
            reference2 = new Firebase("https://wasapii-faab8.firebaseio.com//messages/" + rece_id + "_" + UserDetail.username);

        }
        if (in.hasExtra("Location") && in.hasExtra("AcceptedMeetUserIdd")) {
            rece_id = in.getStringExtra("AcceptedMeetUserIdd");
            UserDetail.latlong = in.getStringExtra("Location");
            UserDetail.chatWith=rece_id;
            UserDetail.username = AppSharedPreferences.loadUserIDFromPreference(ctx);
            Log.e("latlong", UserDetail.latlong);
            Log.e("AcceptedRECE", rece_id);
            reference1 = new Firebase("https://wasapii-faab8.firebaseio.com//messages/" + UserDetail.username + "_" + rece_id );
            reference2 = new Firebase("https://wasapii-faab8.firebaseio.com//messages/" + rece_id + "_" + UserDetail.username);


        }

        requestNameAndPic(rece_id);
        getContactAndLatlng();
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageText = et_message.getText().toString();

                if(!messageText.equals("")) {
                    //Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("user", UserDetail.username);
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                    requestUser_Chat();
                    requestChatNotification(5,messageText);
                }
                et_message.setText("");
            }
        });

    try {
        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    Map map = dataSnapshot.getValue(Map.class);
                    if (map.get("message") != null) {
                        String message = map.get("message").toString();
                        String userName = map.get("user").toString();

                        if (userName.equals(rece_id)) {
                            addMessageBox("" + message, 1);
                           } else {
                            addMessageBox("" + message, 2);

                        }
                    }try{
                    if (UserDetail.contact == null) {
                            String contact = null;
                            Log.e("Con", contact);
                            addMessageBox(2, contact);
                    } else if (UserDetail.contact != null) {
                            String contact = map.get("contact").toString();
                            Log.e("Con", contact);
                            addMessageBox(2, contact);
                        }
                    } catch(Exception eg){}
                    if (UserDetail.latlong == null) {
                            String latlong = null;
                            Log.e("latlong", latlong);
                            addMessageBox(latlong,2,null);
                    } else if (UserDetail.latlong != null) {
                                String latlong = map.get("latlong").toString();
                               // Log.e("latlong", latlong);
                                addMessageBox(latlong,2,null);
                            }
                    }
                    catch (Exception e){e.printStackTrace();}



                }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }catch (Exception e){
        e.printStackTrace();
    }
        btn_meet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_meet.setImageResource(R.drawable.meetbtn_2);
                actionLikeMeet(2);
            }
        });

        btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_like.setImageResource(R.drawable.like_2);
                actionLikeMeet(1);
            }
        });
}

    public void getContactAndLatlng(){
        Intent phint = getIntent();
        if (phint.hasExtra("Contact") && phint.hasExtra("AcceptedMeetUserId")) {
            Map<String, String> map = new HashMap<String, String>();
            UserDetail.contact = getIntent().getStringExtra("Contact");
            map.put("contact", UserDetail.contact);
            Log.e("Contact", UserDetail.contact);
            reference1.push().setValue(map);
            reference2.push().setValue(map);
            requestUser_Chat();
        }

//                    } else {
//                        UserDetail.contact = "";
//                        map.put("contact", UserDetail.contact);
//                    }
        if (phint.hasExtra("Location") && phint.hasExtra("AcceptedMeetUserIdd")) {
            Map<String, String> map = new HashMap<String, String>();
            UserDetail.latlong = getIntent().getStringExtra("Location");
            map.put("latlong", UserDetail.latlong);
            Log.e("latlong", UserDetail.latlong);
            reference1.push().setValue(map);
            reference2.push().setValue(map);
            requestUser_Chat();
        }


//                    } else {
//                        UserDetail.latlong = "";
//                        map.put("latlong", UserDetail.latlong);
//
//                    }
    }

    public void addMessageBox(String message, int type){
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime().toString());

        SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
        String formattedDate = df.format(c.getTime());
        TextView textView = new TextView(ChatActivity.this);
        SpannableString ss1=  new SpannableString(message);
        ss1.setSpan(new RelativeSizeSpan(2f), 0, ss1.length(), 0);
        textView.append(message +"\n");
        textView.append("\t\t"+formattedDate);
        textView.setTextSize(15);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        textView.setLayoutParams(lp);
        //Log.e("Chat Type",type+"");
        if(type == 1) {
            Log.e("Chat Type",type+"");
            textView.setBackgroundResource(R.drawable.bubble_in);
            lp.gravity = Gravity.LEFT;
            lp.setMargins(8, 8, 8, 8);

        }
        else if(type==2){
            Log.e("Chat Type",type+"");
            textView.setBackgroundResource(R.drawable.bubble_out);
            textView.setGravity(LinearLayout.FOCUS_RIGHT);
            lp.gravity = Gravity.RIGHT;
            lp.setMargins(10, 10, 10, 10);


        }
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_UP);
    }

    public void addMessageBox(int type, final String number){
        Log.e("AddMessage",number);
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime().toString());
        SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
        String formattedDate = df.format(c.getTime());
        TextView textView = new TextView(ChatActivity.this);
        SpannableString ss1=  new SpannableString(number);
        ss1.setSpan(new RelativeSizeSpan(2f), 0, ss1.length(), 0);
        textView.setTextSize(15);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        textView.setLayoutParams(lp);


        if(number==null){
            layout.removeView(textView);
        }
        if(type==2){
            Log.e("Chat Type",type+"");
            textView.setBackgroundResource(R.drawable.bubble_out);
            textView.setGravity(LinearLayout.FOCUS_RIGHT);
            textView.append(number+"\n");
            textView.append("\t\t\t\t\t\t"+formattedDate);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String num = "tel:"+number;
                    Intent in = new Intent(Intent.ACTION_DIAL);
                    in.setData(Uri.parse(num));
                    startActivity(in);
                }
            });


            lp.gravity = Gravity.RIGHT;
            lp.setMargins(10, 10, 10, 10);
        }
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_UP);
    }
    public void addMessageBox(String latlong,int type,String s){
        Log.e("Latlong",latlong);
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime().toString());
        SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
        String formattedDate = df.format(c.getTime());
//        TextView textView = new TextView(ChatActivity.this);
//        SpannableString ss1=  new SpannableString(latlong);
//        ss1.setSpan(new RelativeSizeSpan(2f), 0, ss1.length(), 0);
//        textView.setTextSize(15);

        //LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(150, 150);

       // textView.setLayoutParams(lp);

        ImageView im = new ImageView(ChatActivity.this);
        im.setLayoutParams(lp);

        currlat = AppSharedPreferences.getLat(ctx);
        currlng = AppSharedPreferences.getLng(ctx);
        if(latlong==null){
            //layout.removeView(textView);
            layout.removeView(im);
        }
        if(type==2) {
            Log.e("Chat Type", type + "");
            //textView.setBackgroundResource(R.drawable.bubble_out);
            //textView.setGravity(LinearLayout.FOCUS_RIGHT);
            //textView.append(latlong+"\n");
            //textView.append("https://maps.googleapis.com/maps/api/staticmap?center=currlat,%20currlng&zoom=12&size=600x300&maptype=normal"+"\n");
            //textView.append("\t\t\t\t\t"+formattedDate);

            //im.setForegroundGravity(LinearLayout.FOCUS_RIGHT);
            try {
//                InputStream in = new URL("https://maps.googleapis.com/maps/api/staticmap?center="+currlat+","+currlng+"&zoom=12&size=200x200&maptype=normal").openStream();
//                Bitmap bm = BitmapFactory.decodeStream(in);
//                im.setImageBitmap(bm);

                new MapTask(im).execute("https://maps.googleapis.com/maps/api/staticmap?center="+currlat+","+currlng+"&zoom=15&size=150x150&maptype=normal");

                im.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", currlat, currlng);
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(intent);
                    }
                });
                lp.gravity = Gravity.RIGHT;
                lp.setMargins(10, 10, 10, 10);

            }catch(Exception me){
                me.printStackTrace();
            }
            //layout.addView(textView);
            layout.addView(im);
            scrollView.fullScroll(View.FOCUS_UP);
        }
    }


    public void initToolBar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        profile_pic = (CircleImageView) toolbar.findViewById(R.id.profile_pic);
        title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        MaterialIconView btn_back = (MaterialIconView) toolbar.findViewById(R.id.btn_back);




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

    public void requestNameAndPic(String id){
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
            parameters.put("user_id", id);
            Log.e("ChatActivityreq",rece_id);
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

                            Log.e("com.adisoft.mls.temp","satish error response "+res);

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

    public void parseResponse(String response){
        Log.e("ChatActivity","response "+response);

        try {

            JSONObject jsonObject = new JSONObject(response);

            int flag  = jsonObject.getInt("Flag");
            String errorMessage = jsonObject.has("Message") ? jsonObject.getString("Message"):"";

            if(flag==1){

                JSONObject userDetail = jsonObject.getJSONObject("UserDetail");
                name = userDetail.getString("name");
                Log.e("NameChat",name);
                title.setText(name);
                profile = userDetail.getString("profile_img");
                Log.e("ProfileChat",profile);
                Glide.with(ctx)
                        .load(userDetail.getString("profile_img"))
                        .placeholder(R.drawable.gradiant_background)
                        .error(R.drawable.gradiant_background)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        //.bitmapTransform(new RoundedCornersTransformation( ctx,15,2, RoundedCornersTransformation.CornerType.TOP))
                        .into(profile_pic);
            }else{
                //Toast.makeText(ctx,errorMessage,Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void requestUser_Chat(){
        if(!CommonUtilities.isOnline(ctx)){
            Toast.makeText(ctx,R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
            return;
        }

        String url= AppGlobalConstants.WEBSERVICE_BASE_URL+"user_chat";

        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id",AppSharedPreferences.loadUserIDFromPreference(ctx));
            parameters.put("receiver_id",rece_id);
            JsonRequest jsonRequest =  new JsonObjectRequest(Request.Method.POST,url,parameters,new Response.Listener<JSONObject>(){

                @Override
                public void onResponse(JSONObject response) {
                    pDialog.dismiss();

                    parseChatUserResponse(response.toString());

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

                            parseChatUserResponse(res);

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

    public void parseChatUserResponse(String response){
        Log.e("ChatActUserResponse","response "+response);

        try {

            JSONObject jsonObject = new JSONObject(response);

            int flag  = jsonObject.getInt("Flag");
            String errorMessage = jsonObject.has("Message") ? jsonObject.getString("Message"):"";

            if(flag==1){

            }else{
                //Toast.makeText(ctx,errorMessage,Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void actionLikeMeet(final int type){

        if(!CommonUtilities.isOnline(ctx)){
            Toast.makeText(ctx,R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
            return;
        }

        String url= AppGlobalConstants.WEBSERVICE_BASE_URL+"user_like_request";

        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject parameters = new JSONObject();

        try {

            String timestamp = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").format(new Date());
            parameters.put("userid_from",AppSharedPreferences.loadUserIDFromPreference(ctx));
            parameters.put("userid_to",rece_id);
            parameters.put("type",type);
            parameters.put("user_timestamp",timestamp);

            JsonRequest jsonRequest =  new JsonObjectRequest(Request.Method.POST,url,parameters,new Response.Listener<JSONObject>(){

                @Override
                public void onResponse(JSONObject response) {
                    pDialog.dismiss();

                    parseResponseRequests(response.toString(),type);

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

                            parseResponseRequests(res,type);

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


    private void parseResponseRequests(String response,int type){

        Log.e("wasapii","response "+response);

        try {

            JSONObject jsonObject = new JSONObject(response);

            int flag  = jsonObject.getInt("Flag");
            String errorMessage = jsonObject.has("Message") ? jsonObject.getString("Message"):"Failed try again";

            if(flag==1){

                if(type==1){
                    Toast.makeText(ctx,"Like",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ctx,"Sent request",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(ctx,errorMessage,Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }




    }

    public void requestChatNotification(final int type, final String notimessage){

        if(!CommonUtilities.isOnline(ctx)){
            Toast.makeText(ctx,R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
            return;
        }

        String url= AppGlobalConstants.WEBSERVICE_BASE_URL+"user_like_request";

        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();
        JSONObject parameters = new JSONObject();

        try {
            String timestamp = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").format(new Date());
            parameters.put("userid_from", AppSharedPreferences.loadUserIDFromPreference(ctx));
            parameters.put("userid_to", rece_id);
            parameters.put("type",type);
            parameters.put("user_timestamp",timestamp);
            parameters.put("message",notimessage);
            Log.e("ChatActivityreq",rece_id);
            JsonRequest jsonRequest =  new JsonObjectRequest(Request.Method.POST,url,parameters,new Response.Listener<JSONObject>(){

                @Override
                public void onResponse(JSONObject response) {
                    pDialog.dismiss();

                    parseChatNotificationResponse(response.toString(),type);

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

                            parseChatNotificationResponse(res,type);

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

    public void parseChatNotificationResponse(String response,int type){
        Log.e("ChatActivity","response "+response);

        try {

            JSONObject jsonObject = new JSONObject(response);

            int flag  = jsonObject.getInt("Flag");
            //String errorMessage = jsonObject.has("Message") ? jsonObject.getString("Message"):"Invalid email id";
            String name_cons = "";
            if(flag==1){
                JSONArray jsonArray = jsonObject.getJSONArray("Detail");

                for (int i=0;i<jsonArray.length();i++){
                    JSONObject userObject = jsonArray.getJSONObject(i);

                Log.e("Testing","Notification");
                if(type==5){

                    name_cons = userObject.getString("userid_fromname").concat(name_cons);
                    Log.e("type5check",name_cons);
                }}
            }else{
               // Toast.makeText(ctx,errorMessage,Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    class MapTask extends AsyncTask<String, Void, Bitmap>{

        ImageView bmImage;

        public MapTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }

    }

}






