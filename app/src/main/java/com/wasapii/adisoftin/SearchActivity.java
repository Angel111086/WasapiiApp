package com.wasapii.adisoftin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.wasapii.adisoftin.model.ChatUser;
import com.wasapii.adisoftin.model.SearchUser;
import com.wasapii.adisoftin.util.AppGlobalConstants;
import com.wasapii.adisoftin.util.AppSharedPreferences;
import com.wasapii.adisoftin.util.CommonUtilities;

import net.steamcrafted.materialiconlib.MaterialIconView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

public class SearchActivity extends AppCompatActivity {

    Toolbar toolbar;
    private Context ctx;
    private ListView listview;
    private ArrayList<SearchUser> list = new ArrayList<>();
    private  MyAdapter adapter;
    private EditText et_search;
    private int from = 0, to = 10;
    String searchkey="";
    String re_id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        listview = (ListView) findViewById(R.id.listview);
        et_search = (EditText) findViewById(R.id.et_search);

        ctx = SearchActivity.this;

        initToolBar();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchUser searchUser = list.get(position);

                Intent intent = new Intent(ctx,ViewUserProfileActivity.class);
                intent.putExtra("user id",searchUser.getId());
                startActivity(intent);
            }
        });


        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(v.getText().toString());
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });

        /*ArrayList<String> ar = getIntent().getStringArrayListExtra("User_Int");
        Log.e("GetIntent",ar+"");
        int i=0;
        String key="";
        for(;i<ar.size();i++)
        {
            System.out.println(" -->"+ar.get(i));
             key = ar.get(i);
        }
        String s[] = key.split(",");
        Log.e("KeyFromIntent",s+"");*/
        String sr = getIntent().getStringExtra("User_Int");
        if(sr!=null) {
            et_search.setText(sr + "");
            performSearch(sr);
        }
        String sd = getIntent().getStringExtra("User_Interest");
        if(sd !=null){
            et_search.setText(sd + "");
            performSearch(sd);
        }

        String psd = getIntent().getStringExtra("User_Interest_Profile");
        if(psd !=null){
            et_search.setText(psd + "");
            performSearch(psd);
        }
        String venue = getIntent().getStringExtra("Venue");
        if(venue !=null){
            et_search.setText(venue + "");
            performSearch(venue);
        }
        String venue_view = getIntent().getStringExtra("View_venue");
        if(venue_view !=null){
            et_search.setText(venue_view + "");
            performSearch(venue_view);
        }

    }

    private void hideKeyboard(){

        if (et_search != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(et_search.getWindowToken(), 0);
        }
    }
    public void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);


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


    private void performSearch(String keyword){

        Log.d("wasapiii","keyword = "+keyword);

        requestSearch(keyword.trim());

    }
    private void requestSearch(final String keyword){

        if(!CommonUtilities.isOnline(ctx)){
            Toast.makeText(ctx,R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
            return;
        }
        searchkey = keyword;
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
            String errorMessage = jsonObject.has("Message") ? jsonObject.getString("Message"):"no notification found";
            Log.e("Flag",flag+"");
            if(flag==1) {

                JSONArray jsonArray = jsonObject.getJSONArray("UserDetail");
                Log.e("JSONArray", jsonArray + "");
                list.clear();

                for(int i=0;i<jsonArray.length();i++) {

                    JSONObject userDetail = jsonArray.getJSONObject(i);
                    list.add(new SearchUser(userDetail.getString("user_id"),userDetail.getString("name") ,userDetail.getString("profile_img")));
                    //re_id = userDetail.getString("user_id");
                    //requestGetNearByUsers();
                }

                adapter = new MyAdapter();
                listview.setAdapter(adapter);

            }else{
                Toast.makeText(ctx,errorMessage,Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    class ViewHolder{
        public ImageView photo;
        public TextView tv_name;

    }
    public class MyAdapter extends BaseAdapter {


        public MyAdapter( ) {
//            super(ctx, layoutResourceId);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public Object getItem(int position) {
            return null;
        }


        @Override
        public int getCount() {
            return list.size();
        }

        @Override
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
                convertView = inflater.inflate(R.layout.search_list_item, parent, false);
                holder = new ViewHolder();

                holder.tv_name=(TextView)convertView.findViewById(R.id.tv_name);
                holder.photo=(ImageView) convertView.findViewById(R.id.profile_pic);

                convertView.setTag(holder);

            }else {
                // View recycled !
                // no need to inflate
                // no need to findViews by id
                holder = (ViewHolder) convertView.getTag();
            }

            final SearchUser item = list.get(position);

            holder.tv_name.setText(item.getName());

            Glide.with(ctx).load(item.getImage())
                    .error(R.drawable.no_photo_available_icon)
                    .placeholder(R.drawable.no_photo_available_icon)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .bitmapTransform(new RoundedCornersTransformation( ctx,10,2, RoundedCornersTransformation.CornerType.TOP))
                    .into(holder.photo);

            return convertView;

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
                    parseNearUsersResponse(response.toString());

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

                            parseNearUsersResponse(res);

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


    private void parseNearUsersResponse(String response) {
        Log.e("Search Activity wasapii", "response " + response);
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
                    Log.e("NearUserParse",id);
                    Log.e("NearUserParseRece_id",re_id);
                    if (!AppSharedPreferences.loadUserIDFromPreference(ctx).equalsIgnoreCase(id)) {
                        Log.e("TestFirst","SearchActivity");
                        //double distance_in_meters = Double.parseDouble(userObject.getString("distance")) * 1609.34;
                        if(searchkey.equalsIgnoreCase(userObject.getString("name"))) {
                            Log.e("Search Test","Search Data");
                            list.add(new SearchUser(userObject.getString("user_id"),  userObject.getString("name"), userObject.getString("profile_img")));
                        }
                        if(searchkey.equalsIgnoreCase(userObject.getString("venu_name"))) {
                            Log.e("Search Test Venue","VenuName Data");
                            list.add(new SearchUser(userObject.getString("user_id"),  userObject.getString("name"), userObject.getString("profile_img")));

                        }

                        final String[] mylist = userObject.getString("user_interests").split(",");
                        final StringBuilder stringBuilder = new StringBuilder();

                        for (int z = 0; z < mylist.length; z++) {
                            stringBuilder.append(mylist[z].replace(" ", "") + " ");
                        }
                        Log.e(stringBuilder.toString().trim(),"Search String Builder");
                        StringTokenizer st = new StringTokenizer(stringBuilder.toString().trim(), " ");
                        Log.e("STTT",st.toString()+"");
                        String str="";
                        while (st.hasMoreTokens()) {
                            System.out.println(st.nextToken());
                            str = st.nextToken();

                        }
                        str.toLowerCase();


                        if(searchkey.matches(str)){
                            Log.e("User Interests",searchkey);
                            list.add(new SearchUser(userObject.getString("user_id"),  userObject.getString("name"), userObject.getString("profile_img")));
                        }


//                        if(searchkey.equalsIgnoreCase(userObject.getString("user_interests"))) {
//                            Log.e("User Interests",searchkey);
//                            list.add(new SearchUser(userObject.getString("user_id"),  userObject.getString("name"), userObject.getString("profile_img")));
//
//                        }

                        //else if(){}
                        //list.add(new SearchUser(userDetail.getString("user_id"),userDetail.getString("name") ,userDetail.getString("profile_img")));
                    }
                }
                adapter = new MyAdapter();
                listview.setAdapter(adapter);

                //gridAdapter = new GridAdapter();

                //gridView.setAdapter(gridAdapter);

            } else {
                Toast.makeText(ctx, errorMessage, Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateInterests(String spaceSeparatedInterestsStr) {
        String definition = spaceSeparatedInterestsStr.trim();

//        tv_interests.setMovementMethod(LinkMovementMethod.getInstance());
//        tv_interests.setText(definition, TextView.BufferType.SPANNABLE);
//        Spannable spans = (Spannable) tv_interests.getText();
        BreakIterator iterator = BreakIterator.getWordInstance(Locale.US);
        iterator.setText(definition);
        int start = iterator.first();
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator
                .next()) {
            String possibleWord = definition.substring(start, end);
            if ((possibleWord.charAt(0) !=  ' ') && possibleWord.charAt(0) !=  ',') {
                ClickableSpan clickSpan = getClickableSpan(possibleWord);
//               ss.setSpan(new ForegroundColorSpan(Color.RED),1,2,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                spans.setSpan(clickSpan, start, end,
//                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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



}
