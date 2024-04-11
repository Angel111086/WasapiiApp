package com.wasapii.adisoftin;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.wasapii.adisoftin.customview.RoundedCornersTransformation;
import com.wasapii.adisoftin.fragments.NotificationFragment;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import me.relex.circleindicator.CircleIndicator;

public class ViewUserProfileActivity extends AppCompatActivity {

    private Context ctx;
    private ImageAdapter mPageAdapter;
    Toolbar toolbar;
    private ImageView btn_chat, btn_like, btn_meet;
    TextView tv_interests, tv_username,now_at,tv_now_at_view;
    private ArrayList<String> imageList = new ArrayList<>();
    CircleIndicator indicator;
    ViewPager viewpager;
    private String userId = "",nowat;
    private int type = 1;
    //private TextView  title;
    private int fromWhere = 0;
    public static final int req = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_user_profile);

        viewpager = (ViewPager) findViewById(R.id.viewpager);
        indicator = (CircleIndicator) findViewById(R.id.indicator);
        tv_interests = (TextView) findViewById(R.id.tv_interests);
        btn_chat = (ImageView) findViewById(R.id.btn_chat);
        btn_like = (ImageView) findViewById(R.id.btn_like);
        btn_meet = (ImageView) findViewById(R.id.btn_meet);
        tv_username = (TextView) findViewById(R.id.tv_username);
        //now_at = (TextView) findViewById(R.id.now_at);
        tv_now_at_view = (TextView) findViewById(R.id.tv_now_at_view);
        ctx = ViewUserProfileActivity.this;

        userId = getIntent().getStringExtra("user id");

        fromWhere = getIntent().getIntExtra("from where", 0);

        nowat = getIntent().getStringExtra("now_at");

        tv_now_at_view.setText(nowat);
        tv_now_at_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent v_n = new Intent(ctx,SearchActivity.class);
                v_n.putExtra("View_venue",nowat);
                startActivity(v_n);
            }
        });
        initToolBar();

        requestGetDetail();

        btn_chat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent in = new Intent(ctx, ChatActivity.class);
                //Log.e("ViewUser",userId);
                in.putExtra("ViewUserId", userId);
                startActivity(in);

            }
        });


//        btn_meet.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                    btn_meet.setImageResource(R.drawable.meetbtn_2);
//                    actionLikeMeet(2);
//
//                }
//        });

        btn_meet.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                btn_meet.setImageResource(R.drawable.meetbtn_2);
                actionLikeMeet(2);
                btn_meet.setPressed(false);
                return false;
            }
        });

        btn_like.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                btn_like.setImageResource(R.drawable.like_2);
                actionLikeMeet(1);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        btn_meet.setEnabled(true);
        btn_meet.setImageResource(R.drawable.meetbtn_2);

    }

    private void showBlockDialog() {

        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.setContentView(R.layout.dialog_block_user);

        //    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); // In this Case keyboard not appear automatically

        //  dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button btn_no = (Button) dialog.findViewById(R.id.btn_no);
        Button btn_yes = (Button) dialog.findViewById(R.id.btn_yes);
        MaterialIconView btn_cancel = (MaterialIconView) dialog.findViewById(R.id.btn_cancel);


        btn_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        btn_no.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        btn_yes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                requestBlockUser();
                finish();
                dialog.dismiss();
            }
        });

        dialog.show();
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
                    .placeholder(R.drawable.gradiant_background)
                    .error(R.drawable.gradiant_background)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .bitmapTransform(new RoundedCornersTransformation(ctx, 15, 2, RoundedCornersTransformation.CornerType.TOP))
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.user_profile_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.block) {

            showBlockDialog();

            return true;
        }

        if(id == R.id.report){
            //getWindow().setBackgroundDrawableResource(R.style.Theme_Transparent);
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
            //getWindow().setTitle("Abuse Reported");
            requestReportUser();
            finish();

        }

        return super.onOptionsItemSelected(item);
    }


    private void actionLikeMeet(final int type) {

        if (!CommonUtilities.isOnline(ctx)) {
            Toast.makeText(ctx, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            return;
        }

        String url = AppGlobalConstants.WEBSERVICE_BASE_URL + "user_like_request";

        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject parameters = new JSONObject();

        try {

            String timestamp = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").format(new Date());
            parameters.put("userid_from", AppSharedPreferences.loadUserIDFromPreference(ctx));
            parameters.put("userid_to", userId);
            parameters.put("type", type);
            parameters.put("user_timestamp", timestamp);

            JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    pDialog.dismiss();

                    parseResponseRequests(response.toString(), type);

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

                            parseResponseRequests(res, type);

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


    private void parseResponseRequests(String response, int type) {

        Log.e("wasapii", "response " + response);

        try {

            JSONObject jsonObject = new JSONObject(response);

            int flag = jsonObject.getInt("Flag");
            String errorMessage = jsonObject.has("Message") ? jsonObject.getString("Message") : "Failed try again";

            if (flag == 1) {

                if (type == 1) {
                    Toast.makeText(ctx, "Like", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(ctx, "Sent request", Toast.LENGTH_SHORT).show();
                }


            } else {
                Toast.makeText(ctx, errorMessage, Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void requestGetDetail() {

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

            parameters.put("user_id", userId);

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

            int flag = jsonObject.getInt("Flag");
            String errorMessage = jsonObject.has("Message") ? jsonObject.getString("Message") : "Invalid email id";

            if (flag == 1) {

                JSONObject userDetail = jsonObject.getJSONObject("UserDetail");
                String name = userDetail.getString("name");
                //title.setText(name);
                tv_username.setText(name);
                tv_interests.setText(userDetail.getString("user_interests"));
                Log.e("UserInterests", userDetail.getString("user_interests"));
                final String user_data = userDetail.getString("user_interests");
                tv_interests.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e("View", view + "");

                        String[] alist = user_data.split(",");
                        for (String list : alist) {
                            //System.out.println(" -->"+alist.toString());
                            System.out.println(" -->" + list);
                        }
                        Random random = new Random();
                        int maxIndex = alist.length;
                        int generatedIndex = random.nextInt(maxIndex);
                        Log.e("GI", generatedIndex + "");
                        Log.e("Data At Index", alist[generatedIndex]);
                        String s = alist[generatedIndex];
                        if (view.isClickable()) {
                            Log.e("SingleValue", s);
                            Intent in = new Intent(ctx, SearchActivity.class);
                            in.putExtra("User_Int", alist[generatedIndex]);
                            startActivity(in);
                        }
                    }
                });


                imageList.add(0, userDetail.getString("profile_img"));

                if (userDetail.has("image1") && !userDetail.getString("image1").equalsIgnoreCase("")) {
                    imageList.add(1, userDetail.getString("profile_img"));
                } else if (userDetail.has("image2") && !userDetail.getString("image2").equalsIgnoreCase("")) {
                    imageList.add(2, userDetail.getString("profile_img"));
                } else if (userDetail.has("image3") && !userDetail.getString("image3").equalsIgnoreCase("")) {
                    imageList.add(3, userDetail.getString("profile_img"));
                }

                mPageAdapter = new ImageAdapter();

                viewpager.setAdapter(mPageAdapter);

                indicator.setViewPager(viewpager);

            } else {
                Toast.makeText(ctx, errorMessage, Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void requestBlockUser() {
        if (!CommonUtilities.isOnline(ctx)) {
            Toast.makeText(ctx, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            return;
        }

        String url = AppGlobalConstants.WEBSERVICE_BASE_URL + "user_block";

        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject parameters = new JSONObject();

        try {

            parameters.put("user_id_blockby", AppSharedPreferences.loadUserIDFromPreference(ctx));
            parameters.put("user_id_blockto", userId);
            Log.e("Blockto", userId);
            Log.e("BlockBy", AppSharedPreferences.loadUserIDFromPreference(ctx));

            JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    pDialog.dismiss();

                    parseBlockResponse(response.toString());

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

                            parseBlockResponse(res);

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


    private void parseBlockResponse(String response) {

        Log.e("wasapii", "response " + response);

        try {

            JSONObject jsonObject = new JSONObject(response);

            int flag = jsonObject.getInt("Flag");
            String errorMessage = jsonObject.has("Message") ? jsonObject.getString("Message") : "Cannot Block User";

            if (flag == 1) {
                Toast.makeText(ctx, "User Blocked", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ctx, errorMessage, Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void requestReportUser(){

            if (!CommonUtilities.isOnline(ctx)) {
                Toast.makeText(ctx, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                return;
            }

            String url = AppGlobalConstants.WEBSERVICE_BASE_URL + "user_abuse";

            final ProgressDialog pDialog = new ProgressDialog(ctx);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

            JSONObject parameters = new JSONObject();

            try {
                parameters.put("user_id", AppSharedPreferences.loadUserIDFromPreference(ctx));
                parameters.put("user_abuse_id", userId);


                JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();

                        parseReportUserRequests(response.toString());

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

                                parseReportUserRequests(res);

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

    public void parseReportUserRequests(String response){

        Log.e("wasapii", "response " + response);

        try {

            JSONObject jsonObject = new JSONObject(response);

            int flag = jsonObject.getInt("Flag");
            //String errorMessage = jsonObject.has("Message") ? jsonObject.getString("Message") : "Cannot Block User";
            String message = jsonObject.getString("Message");

            if (flag == 1) {
                Toast.makeText(ctx, "Abuse Reported", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
        
    }




