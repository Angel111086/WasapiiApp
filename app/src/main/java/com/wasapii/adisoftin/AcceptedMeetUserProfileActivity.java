package com.wasapii.adisoftin;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wasapii.adisoftin.customview.RoundedCornersTransformation;
import com.wasapii.adisoftin.util.AppGlobalConstants;
import com.wasapii.adisoftin.util.AppSharedPreferences;
import com.wasapii.adisoftin.util.CommonUtilities;

import net.steamcrafted.materialiconlib.MaterialIconView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Locale;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by root on 17/5/17.
 */

public class AcceptedMeetUserProfileActivity extends AppCompatActivity {

    private Context ctx;
    private ImageAdapter mPageAdapter;
    Toolbar toolbar;
    private ImageView btn_chat, btn_number, btn_mapicon;
    TextView tv_interests, tv_username;
    private ArrayList<String> imageList = new ArrayList<>();
    CircleIndicator indicator;
    ViewPager viewpager;
    private String userId = "";
    private int fromWhere = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meet_user_profile);


        viewpager = (ViewPager) findViewById(R.id.viewpager);
        indicator = (CircleIndicator) findViewById(R.id.indicator);
        tv_interests = (TextView) findViewById(R.id.tv_interests);
        btn_chat = (ImageView) findViewById(R.id.btn_chat);
        btn_number = (ImageView) findViewById(R.id.btn_number);
        btn_mapicon = (ImageView) findViewById(R.id.btn_mapicon);
        tv_username = (TextView) findViewById(R.id.tv_username);
        ctx = AcceptedMeetUserProfileActivity.this;

        userId = getIntent().getStringExtra("user id");
        Log.e("Accepted Id",userId);
        fromWhere = getIntent().getIntExtra("from where", 0);
        requestGetDetail();
        initToolBar();

        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(ctx, ChatActivity.class);
                in.putExtra("AcceptedMeetUserId",userId);
                startActivity(in);
            }
        });

        btn_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                requestGetContact();
            }
        });
        btn_mapicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                double lat = AppSharedPreferences.getLat(ctx);
                double lng = AppSharedPreferences.getLng(ctx);
                String latt = String.valueOf(lat);
                String lngg = String.valueOf(lng);
                String location = latt.concat(lngg);
                Intent in = new Intent(ctx, ChatActivity.class);
                in.putExtra("Location",location);
                Log.e("AcceptedActivity",location);
                in.putExtra("AcceptedMeetUserIdd",userId);
                startActivity(in);
            }
        });
    }

    public void initToolBar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        //title = (TextView) toolbar.findViewById(R.id.toolbar_title);

        MaterialIconView btn_back = (MaterialIconView) toolbar.findViewById(R.id.btn_back);

        //title.setText("Username");

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
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        getMenuInflater().inflate(R.menu.user_profile_menu, menu);
//
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//
//        if (id == R.id.block) {
//
//            showBlockDialog();
//
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    private void showBlockDialog() {
//
//        final Dialog dialog = new Dialog(ctx);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
//        dialog.setContentView(R.layout.dialog_block_user);
//
//        //    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); // In this Case keyboard not appear automatically
//
//        //  dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//        Button btn_no = (Button) dialog.findViewById(R.id.btn_no);
//        Button btn_yes = (Button) dialog.findViewById(R.id.btn_yes);
//        MaterialIconView btn_cancel = (MaterialIconView)dialog.findViewById(R.id.btn_cancel);
//
//
//        btn_cancel.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//
//        btn_no.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                dialog.dismiss();
//            }
//        });
//
//        btn_yes.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                dialog.dismiss();
//            }
//        });
//
//        dialog.show();
//    }


    private void updateInterests(String spaceSeparatedInterestsStr) {
        String definition = spaceSeparatedInterestsStr.trim();

        tv_interests.setMovementMethod(LinkMovementMethod.getInstance());
        tv_interests.setText(definition, TextView.BufferType.SPANNABLE);
        Spannable spans = (Spannable) tv_interests.getText();
        BreakIterator iterator = BreakIterator.getWordInstance(Locale.US);
        iterator.setText(definition);
        int start = iterator.first();
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator
                .next()) {
            String possibleWord = definition.substring(start, end);
            if ((possibleWord.charAt(0) !=  ' ') && possibleWord.charAt(0) !=  '#') {
                ClickableSpan clickSpan = getClickableSpan(possibleWord);
//                ss.setSpan(new ForegroundColorSpan(Color.RED),1,2,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spans.setSpan(clickSpan, start, end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private ClickableSpan getClickableSpan(final String word) {
        return new ClickableSpan() {
            final String mWord;
            {
                mWord = word;
            }

            @Override
            public void onClick(View widget) {
//                Log.d("tapped on:", mWord);
//                Toast.makeText(widget.getContext(), mWord, Toast.LENGTH_SHORT)
//                        .show();
                Log.i("DEBUGTAG", "Span clicked - " + mWord);
                Intent in = new Intent(ctx, SearchActivity.class);
                in.putExtra("User_Interest_Profile",mWord);
                startActivity(in);
            }

            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false); // set to false to remove underline
            }
        };
    }



    class ImageAdapter extends PagerAdapter {

        // private static int[] images = { R.drawable.nature_1, R.drawable.nature_2, R.drawable.nature_3, R.drawable.nature_4, R.drawable.nature_5 };

        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            ImageView img = new ImageView(container.getContext());
            // img.setImageResource(images[position]);
            Glide.with(ctx)
                    .load(imageList.get(position))
                    .placeholder(R.drawable.no_photo_available_icon)
                    .error(R.drawable.no_photo_available_icon)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .bitmapTransform(new RoundedCornersTransformation( ctx,15,2, RoundedCornersTransformation.CornerType.TOP))
                    .into(img);
            container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            return img;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }


    }

    private void requestGetDetail(){

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

            parameters.put("user_id",userId);
            Log.e("Accepted",userId);
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


    private void parseResponse(String response){

        Log.e("wasapii","response "+response);

        try {

            JSONObject jsonObject = new JSONObject(response);

            int flag  = jsonObject.getInt("Flag");
            String errorMessage = jsonObject.has("Message") ? jsonObject.getString("Message"):"Invalid email id";

            if(flag==1){

                JSONObject userDetail = jsonObject.getJSONObject("UserDetail");
                String name = userDetail.getString("name");

                tv_username.setText(name);

                final String[] list = userDetail.getString("user_interests").split(",");
                final StringBuilder stringBuilder = new StringBuilder();

                for (int i = 0; i < list.length; i++) {
                    stringBuilder.append("#" + list[i].replace(" ", "") + " ");
                }
                updateInterests(stringBuilder.toString().trim());


                imageList.add(0,userDetail.getString("profile_img"));

                if(userDetail.has("image1") && !userDetail.getString("image1").equalsIgnoreCase("")){
                    imageList.add(1,userDetail.getString("profile_img"));
                }else if(userDetail.has("image2") && !userDetail.getString("image2").equalsIgnoreCase("")){
                    imageList.add(2,userDetail.getString("profile_img"));
                }else if(userDetail.has("image3") && !userDetail.getString("image3").equalsIgnoreCase("")){
                    imageList.add(3,userDetail.getString("profile_img"));
                }

                mPageAdapter = new ImageAdapter();

                viewpager.setAdapter(mPageAdapter);

                indicator.setViewPager(viewpager);

            }else{
                Toast.makeText(ctx,errorMessage,Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    public void requestGetContact(){
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
            parameters.put("user_id", AppSharedPreferences.loadUserIDFromPreference(ctx));
            JsonRequest jsonRequest =  new JsonObjectRequest(Request.Method.POST,url,parameters,new Response.Listener<JSONObject>(){

                @Override
                public void onResponse(JSONObject response) {
                    pDialog.dismiss();

                    parseGetContact(response.toString());

                }

            },new Response.ErrorListener(){

                @Override
                public void onErrorResponse(VolleyError error) {

                    pDialog.dismiss();

                    NetworkResponse response = error.networkResponse;

                    Log.e("com.adisoft.mls.temp","Contact error response "+response);

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

                            Log.e("com.adisoft.mls.temp","Contact error response "+res);

                            parseGetContact(res);

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
    public void parseGetContact(String response){
        Log.e("wasapii","contact "+response);

        try {

            JSONObject jsonObject = new JSONObject(response);

            int flag  = jsonObject.getInt("Flag");
            String errorMessage = jsonObject.has("Message") ? jsonObject.getString("Message"):"Invalid Details";


            if(flag == 1){
                JSONObject userDetails = jsonObject.has("UserDetail") ? jsonObject.getJSONObject("UserDetail"):null;
                Log.e("JSONObject",userDetails+"");
                if(userDetails!=null){
                    Log.e("h","h");
                    String contactNo = userDetails.has("contact_no")?userDetails.getString("contact_no"):"";
                    Log.e("h",contactNo);
                    Intent intent = new Intent(ctx,ChatActivity.class);
                    intent.putExtra("Contact",contactNo);
                    intent.putExtra("AcceptedMeetUserId",userId);
                    startActivity(intent);


                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }


    }

}