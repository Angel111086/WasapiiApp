package com.wasapii.adisoftin.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
import com.google.firebase.auth.FirebaseUser;
import com.wasapii.adisoftin.ChatActivity;
import com.wasapii.adisoftin.ChatDetailActivity;
import com.wasapii.adisoftin.LoginActivity;
import com.wasapii.adisoftin.MyApplication;
import com.wasapii.adisoftin.R;
import com.wasapii.adisoftin.adapters.ChatRecyclerAdapter;


import com.wasapii.adisoftin.customview.RoundedCornersTransformation;
import com.wasapii.adisoftin.model.Chat;
import com.wasapii.adisoftin.model.NearUser;
import com.wasapii.adisoftin.model.UserDetail;
import com.wasapii.adisoftin.util.AppGlobalConstants;
import com.wasapii.adisoftin.util.AppSharedPreferences;
import com.wasapii.adisoftin.util.CommonUtilities;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;


public class ChatFragment extends Fragment {

    private ListView listview;
    private ArrayList<Chat> list = new ArrayList<>();
    private Context ctx;
    private MyAdapter adapter;
    private ChatRecyclerAdapter mChatRecyclerAdapter;
    int totalUsers = 0;
    //private ChatPresenter mChatPresenter;
    String re_id="";
    Chat cc=new Chat();
    private boolean isViewShown;
    private int from = 0, to = 10;
    String login_status;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        listview = (ListView) view.findViewById(R.id.listview);

        ctx = getActivity();

        for(int i=0;i<10;i++) {
            list.add(new Chat(""+i,"user name "+i ,"https://igx.4sqi.net/img/general/150x150/42235080_EKxe5_V7cxaI0mg5lW8KJLuSqLLZYK6TO40dK9yKJo8.jpg","",0));
        }
        requestUsers();
        requestUsersByFireBase();
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getView() != null) {
            isViewShown = true;
            // fetchdata() contains logic to show data when page is selected mostly asynctask to fill the data
           requestUsers();
            Log.d("wasapii ", "ChatFragment requestUsers()");
        } else {
            isViewShown = false;
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
                convertView = inflater.inflate(R.layout.chat_list_item, parent, false);
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

            final Chat item = list.get(position);

            holder.tv_name.setText(item.getName());

            Glide.with(ctx).load(item.getImage())
                    .error(R.drawable.gradiant_background)
                    .placeholder(R.drawable.gradiant_background)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .bitmapTransform(new RoundedCornersTransformation( ctx,10,2, RoundedCornersTransformation.CornerType.TOP))
                    .into(holder.photo);

            return convertView;

        }

    }

    public void requestUsers(){
        if (!CommonUtilities.isOnline(ctx)) {
            Toast.makeText(ctx, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            return;
        }
            String url = AppGlobalConstants.WEBSERVICE_BASE_URL+"user_chat_list";

            final ProgressDialog pDialog = new ProgressDialog(ctx);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        JSONObject parameters = new JSONObject();

        try {
            parameters.put("user_id", AppSharedPreferences.loadUserIDFromPreference(ctx));
            //parameters.put("", AppSharedPreferences.loadUserIDFromPreference(ctx));
            Log.e("user_idChatFrag",AppSharedPreferences.loadUserIDFromPreference(ctx));

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

        Log.e("wasapiiChat Response", "response " + response);

        try {

            JSONObject jsonObject = new JSONObject(response);

            int flag = jsonObject.getInt("Flag");
            //String errorMessage = jsonObject.has("Message") ? jsonObject.getString("Message") : "Invalid email id";
            //String re_id="";
            if (flag == 1) {

                JSONArray jsonArray = jsonObject.getJSONArray("Userlist");
                Log.e("JSONArrayUserList", jsonArray + "");
                list.clear();

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject userObject = jsonArray.getJSONObject(i);

                    re_id = userObject.getString("user_id");
                    Log.e("ChatFragmID",re_id);
                    if (!AppSharedPreferences.loadUserIDFromPreference(ctx).equalsIgnoreCase(re_id)) {

                        //list.add(new Chat(userObject.getString("user_id"),userObject.getString("name"), userObject.getString("profile_img"), null,1));
                        requestGetNearByUsersFor100m();
                    }
                }


//                adapter = new MyAdapter();
//
//                listview.setAdapter(adapter);
//
//                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        cc = list.get(position);
//                        Intent intent = new Intent(getActivity(), ChatActivity.class);
//                        intent.putExtra("ChatUserID",cc.getId());
//                        startActivity(intent);
//
//
//                    }
//                });

            } else {
                //Toast.makeText(ctx, errorMessage, Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    public void requestUsersByFireBase(){
        String url = "https://wasapii-faab8.firebaseio.com//users.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                doOnSuccess(s);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(ctx);
        rQueue.add(request);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cc = list.get(position);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("ChatUserID",cc.getId());
                startActivity(intent);


            }
        });
    }

    public void doOnSuccess(String s){
        try {
            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();
            String key = "";

            while(i.hasNext()){
                //key = i.next().toString();
                key = cc.getId();
                if(!key.equals(UserDetail.username)) {
                    //list.add("");
                    Log.e("ChatFragment",UserDetail.username);
                }

                totalUsers++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        if(totalUsers <=1){
//            //noUsersText.setVisibility(View.VISIBLE);
//            listview.setVisibility(View.GONE);
//        }
//        else{
//            //noUsersText.setVisibility(View.GONE);
//            usersList.setVisibility(View.VISIBLE);
//            usersList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, al));
//        }
//
//        pd.dismiss();
    }

    private void requestGetNearByUsersFor100m() {

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
                    parse100mResponse(response.toString());

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

                            parse100mResponse(res);

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


    private void parse100mResponse(String response) {
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

                        if(re_id.equals(userObject.getString("user_id"))){
                            Log.e("Chat FirstIf","Testing");
                            if(distance_in_meters<=100) {
                                Log.e("Chat SecondIf","Testing");
                                list.add(new Chat(userObject.getString("user_id"), userObject.getString("name"), userObject.getString("profile_img"), null, 1));
                            }
                            else{
                                listview.setEnabled(false);
                                //list.clear();
                            }
                        }
                        //list.add(new NearUser(userObject.getString("name"), userObject.getString("venu_name"), String.valueOf(distance_in_meters), userObject.getString("profile_img"), userObject.getString("user_id")));
                        adapter = new MyAdapter();

                        listview.setAdapter(adapter);

                        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                cc = list.get(position);
                                Intent intent = new Intent(getActivity(), ChatActivity.class);
                                intent.putExtra("ChatUserID",cc.getId());
                                startActivity(intent);


                            }
                        });

                    }
                }



            } else {
                Toast.makeText(ctx, errorMessage, Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}




