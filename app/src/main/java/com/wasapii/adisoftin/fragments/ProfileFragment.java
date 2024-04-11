package com.wasapii.adisoftin.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.wasapii.adisoftin.EditMyProfileActivity;
import com.wasapii.adisoftin.LoginActivity;
import com.wasapii.adisoftin.MyApplication;
import com.wasapii.adisoftin.R;
import com.wasapii.adisoftin.SearchActivity;
import com.wasapii.adisoftin.customview.RoundedCornersTransformation;
import com.wasapii.adisoftin.db.DataHelper;
import com.wasapii.adisoftin.model.NearUser;
import com.wasapii.adisoftin.model.User;
import com.wasapii.adisoftin.util.AppGlobalConstants;
import com.wasapii.adisoftin.util.AppSharedPreferences;
import com.wasapii.adisoftin.util.CommonUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.text.BreakIterator;

import me.relex.circleindicator.CircleIndicator;


public class ProfileFragment extends Fragment {

    private Context ctx;
    private ImageAdapter mPageAdapter;
    private TextView btn_edit, tv_username, tv_now_at, tv_interests;
    private ImageView btn_more;
    private DataHelper dh;
    private ArrayList<String> imageList = new ArrayList<>();
    private ViewPager viewpager;
    CircleIndicator indicator;
    private boolean isViewShown = false;
    private int from = 0, to = 10;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        viewpager = (ViewPager) view.findViewById(R.id.viewpager);
        indicator = (CircleIndicator) view.findViewById(R.id.indicator);
        btn_edit = (TextView) view.findViewById(R.id.btn_edit);
        tv_username = (TextView) view.findViewById(R.id.tv_username);
        tv_now_at = (TextView) view.findViewById(R.id.tv_now_at);
        tv_interests = (TextView) view.findViewById(R.id.tv_interests);
        //btn_more = (ImageView) view.findViewById(R.id.btn_more);

        ctx = getActivity();

        dh = DataHelper.getInstance(ctx);

        btn_edit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), EditMyProfileActivity.class);
                startActivity(intent);

            }
        });

        if (!isViewShown) {
            showUserInfo();
            Log.d("wasapii ", "oncreateView showUserInfo");
        }

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getView() != null) {
            isViewShown = true;
            // fetchdata() contains logic to show data when page is selected mostly asynctask to fill the data
            showUserInfo();
            Log.d("wasapii ", "setUserVisibleHint showUserInfo");
        } else {
            isViewShown = false;
        }


    }

    private void showUserInfo() {
        imageList.clear();
        final User user = dh.getUserInfo();
        if (user != null) {
            tv_username.setText(user.getName());

            final String[] list = user.getInterests().split(",");
            final StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < list.length; i++) {
                stringBuilder.append("#" + list[i].replace(" ", "") + " ");
            }
            updateInterests(stringBuilder.toString().trim());
            try {

//                tv_interests.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
////                for(String alist : list) {
////                    System.out.println(" -->" + alist);
////
////                }
//                        String s;
//                        for (int i = 0;i<list.length;i++){
//                            s = list[i];
//                            System.out.println(" -->" + s);
//
//                        }
//
//                    Random random = new Random();
//                    int maxIndex = list.length;
//                    int generatedIndex = random.nextInt(maxIndex);
//                    //int generatedIndex = random.nextInt((maxIndex - minIndex) + 1) + minIndex;
//
//                    Log.e("GI", generatedIndex + "");
//                    Log.e("Data At Index", list[generatedIndex]);
//                    String ds = list[generatedIndex];
//                    if (view.isClickable()) {
//                        Log.e("SingleValue", ds);
//                        Intent in = new Intent(ctx, SearchActivity.class);
//                        in.putExtra("User_Interest_Profile", list[generatedIndex]);
//                        startActivity(in);
//                    }
//                }
//
//            });

            } catch (Exception e) {
                e.printStackTrace();
            }

            imageList.add(user.getProfileImgUrl());
            if (!user.getImg1().equalsIgnoreCase("")) {
                imageList.add(user.getImg1());
            }
            if (!user.getImg2().equalsIgnoreCase("")) {
                imageList.add(user.getImg2());
            }
            if (!user.getImg3().equalsIgnoreCase("")) {
                imageList.add(user.getImg3());
            }
            mPageAdapter = new ImageAdapter();
            viewpager.setAdapter(mPageAdapter);
            indicator.setViewPager(viewpager);
            requestGetNearByUsers();
        }
    }

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
                    .bitmapTransform(new RoundedCornersTransformation(ctx, 15, 2, RoundedCornersTransformation.CornerType.TOP))
                    .into(img);
            container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            //container.addView(img, new LinearLayout.LayoutParams(60,60));
            //container.addView(img, new LinearLayout.LayoutParams(60,60));
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

    private void requestGetNearByUsers() {

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
            parameters.put("user_id", AppSharedPreferences.loadUserIDFromPreference(ctx));

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

        Log.e("wasapii", "response " + response);

        try {

            JSONObject jsonObject = new JSONObject(response);

            int flag = jsonObject.getInt("Flag");
            String errorMessage = jsonObject.has("Message") ? jsonObject.getString("Message") : "Invalid email id";

            if (flag == 1) {

                JSONObject userDetail = jsonObject.getJSONObject("UserDetail");
                String name = userDetail.getString("name");
                final String v_name = userDetail.getString("venu_name");
                //title.setText(name);
                tv_now_at.setText(v_name);
                tv_now_at.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        requestSearchByVenue(v_name);
                    }
                });

            } else {
                Toast.makeText(ctx, errorMessage, Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void requestSearchByVenue(final String keyword){

        if(!CommonUtilities.isOnline(ctx)){
            Toast.makeText(ctx,R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
            return;
        }

        String url= AppGlobalConstants.WEBSERVICE_BASE_URL+"user_search_by_keyword";

        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject parameters = new JSONObject();

        try {

            parameters.put("keyword", keyword);
            parameters.put("user_id",AppSharedPreferences.loadUserIDFromPreference(ctx));

            JsonRequest jsonRequest =  new JsonObjectRequest(Request.Method.POST,url,parameters,new Response.Listener<JSONObject>(){

                @Override
                public void onResponse(JSONObject response) {
                    pDialog.dismiss();
                    parseVenueResponse(response.toString());

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

                            parseVenueResponse(res);

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


    private void parseVenueResponse(String response){

        Log.e("wasapii","response "+response);

        try {

            JSONObject jsonObject = new JSONObject(response);

            int flag  = jsonObject.getInt("Flag");
            String errorMessage = jsonObject.has("Message") ? jsonObject.getString("Message"):"no notification found";
            Log.e("Flag",flag+"");
            if(flag==1) {

                JSONArray jsonArray = jsonObject.getJSONArray("UserDetail");
                Log.e("JSONArray", jsonArray + "");

                String v_n="";
                for(int i=0;i<jsonArray.length();i++) {

                    JSONObject userDetail = jsonArray.getJSONObject(i);
                    v_n = userDetail.getString("venu_name");

                }

                Intent in = new Intent(ctx,SearchActivity.class);
                in.putExtra("Venue",v_n);
                startActivity(in);


            }else{
                Toast.makeText(ctx,errorMessage,Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


}



