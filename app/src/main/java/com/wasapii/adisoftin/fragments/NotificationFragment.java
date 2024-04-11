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
import android.widget.AbsListView;
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
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hudomju.swipe.SwipeToDismissTouchListener;
import com.hudomju.swipe.adapter.ListViewAdapter;
import com.wasapii.adisoftin.AcceptedMeetUserProfileActivity;
import com.wasapii.adisoftin.MyApplication;
import com.wasapii.adisoftin.R;
import com.wasapii.adisoftin.SentMeetRequestActivity;
import com.wasapii.adisoftin.model.Chat;
import com.wasapii.adisoftin.util.AppGlobalConstants;
import com.wasapii.adisoftin.util.AppSharedPreferences;
import com.wasapii.adisoftin.util.CommonUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    private ListView listview;
    private ArrayList<Chat> list = new ArrayList<>();
    private Context ctx;
    private MyAdapter adapter;
    private static final int TIME_TO_AUTOMATICALLY_DISMISS_ITEM = 3000;
    private boolean isViewShown;
    int pos;
    Chat cu;
    private int mNotificationsCount = 0;
    private int mnotificationsCount = 0;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        listview = (ListView) view.findViewById(R.id.listview);

        ctx = getActivity();

        final SwipeToDismissTouchListener<ListViewAdapter> touchListener =

                new SwipeToDismissTouchListener<>(

                        new ListViewAdapter(listview),

                        new SwipeToDismissTouchListener.DismissCallbacks<ListViewAdapter>() {

                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onPendingDismiss(ListViewAdapter recyclerView, int position) {

                            }

                            @Override
                            public void onDismiss(ListViewAdapter view, int position) {
                                adapter.remove(position);
                            }
                        });

        touchListener.setDismissDelay(TIME_TO_AUTOMATICALLY_DISMISS_ITEM);
        listview.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        listview.setOnScrollListener((AbsListView.OnScrollListener) touchListener.makeScrollListener());

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (touchListener.existPendingDismisses()) {
                    touchListener.undoPendingDismiss();
                }

                cu = list.get(position);
                Log.e("ListData",cu.toString());
                if(cu.getType() == 2){
                    Intent in  = new Intent(ctx, SentMeetRequestActivity.class);
                    in.putExtra("user id",cu.getId());
                    Log.e("NotiFragsent",cu.getId());
                    startActivity(in);
                    requestNotificationsRemove();
                }
                else if(cu.getType() == 3){
                    Intent in  = new Intent(ctx, AcceptedMeetUserProfileActivity.class);
                    in.putExtra("user id",cu.getId());
                    Log.e("NotiFragaccept",cu.getId());
                    startActivity(in);
                    requestNotificationsRemove();
                }
                long x = adapter.getItemId(position);
                Log.e("Adapter Position",x+"");
                int y = (int)x;
                Chat c = list.get(y);
                Log.e("Data",c.getName());
                pos = (int)x;

            }
        });

        if (!isViewShown) {
            requestGetNotifications();
        }

        return view;
    }


    private void checkException()  throws JSONException{

        JSONObject jsonObject = new JSONObject("sdd");
    }



    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getView() != null) {
            isViewShown = true;
            // fetchdata() contains logic to show data when page is selected mostly asynctask to fill the data

        } else {
            isViewShown = false;
        }

    }



    private void requestGetNotifications(){

        if(!CommonUtilities.isOnline(ctx)){
            Toast.makeText(ctx,R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
            return;
        }

        String url= AppGlobalConstants.WEBSERVICE_BASE_URL+"get_notification_detail";

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

        Log.e("wasapii","Notification response "+response);

        try {

            JSONObject jsonObject = new JSONObject(response);

            int flag  = jsonObject.getInt("Flag");
            String errorMessage = jsonObject.has("Message") ? jsonObject.getString("Message"):"no notification found";

            if(flag==1){

                JSONArray jsonArray = jsonObject.getJSONArray("Detail");

                list.clear();

                for (int i=0;i<jsonArray.length();i++){
                    JSONObject userObject = jsonArray.getJSONObject(i);

                    int type = 0;//Integer.parseInt(userObject.getString("notification_type"));
                    String noti_type = userObject.getString("notification_type");
                    int x = Integer.parseInt(noti_type);
                    Log.e("Noti_type",noti_type);
                    if(!userObject.getString("notification_type").equalsIgnoreCase("")) {

                        type = Integer.parseInt(userObject.getString("notification_type"));
                    }
                    //list.add(new ChatUser(userObject.getString("userid_from"),userObject.getString("userid_fromname"),userObject.getString("userid_fromimg"),userObject.getString("notification_timestamp"),type));
                    String name_cons = "";
                    if(type == 1){
                        name_cons = " liked your picture";
                        name_cons = userObject.getString("userid_fromname").concat(name_cons);
                        mNotificationsCount++;

                    }
                    if(type == 2){
                        name_cons = " sent meet request";
                        name_cons = userObject.getString("userid_fromname").concat(name_cons);
                        mNotificationsCount++;

                    }
                    if(type == 3){
                        name_cons = " accepted meet request";
                        name_cons = userObject.getString("userid_fromname").concat(name_cons);
                        mNotificationsCount++;
                    }

                    if(type == 4){
                        name_cons = " rejected meet request";
                        name_cons = userObject.getString("userid_fromname").concat(name_cons);
                        mNotificationsCount++;
                    }


                    Log.e("Constant",name_cons);
                    list.add(new Chat(userObject.getString("userid_from"),name_cons,userObject.getString("userid_fromimg"),userObject.getString("notification_timestamp"),type));
                    //requestGetNotifications();
                    // Update LayerDrawable's BadgeDrawable

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
        public TextView tv_name,tv_unread;

    }

    public class MyAdapter extends BaseAdapter {


        public MyAdapter( ) {
//            super(ctx, layoutResourceId);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        public void remove(int position) {
            list.remove(position);
            notifyDataSetChanged();
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
            LayoutInflater inflater = ((Activity) ctx).getLayoutInflater();
            convertView = inflater.inflate(R.layout.notification_list_item, parent, false);
            holder = new ViewHolder();

            holder.tv_name=(TextView)convertView.findViewById(R.id.tv_name);
            holder.photo=(CircleImageView) convertView.findViewById(R.id.profile_pic);
            holder.tv_unread = (TextView) convertView.findViewById(R.id.tv_unread);
            final Chat item = list.get(position);

            holder.tv_name.setText(item.getName());
            holder.tv_unread.setText(item.getTimestamp());
            Glide.with(ctx).load(item.getImage())
                    .error(R.drawable.no_photo_available_icon)
                    .placeholder(R.drawable.no_photo_available_icon)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    //.bitmapTransform(new RoundedCornersTransformation( ctx,15,2, RoundedCornersTransformation.CornerType.TOP))
                    .into(holder.photo);
            return convertView;
        }

    }

    private void requestNotificationsRemove(){

        if(!CommonUtilities.isOnline(ctx)){
            Toast.makeText(ctx,R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
            return;
        }

        String url= AppGlobalConstants.WEBSERVICE_BASE_URL+"user_request";

        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();


        JSONObject parameters = new JSONObject();

        try {

            parameters.put("userid_from", AppSharedPreferences.loadUserIDFromPreference(ctx));
            parameters.put("userid_to", cu.getId());
            parameters.put("type", 1);
            Log.e("nottype",cu.getType()+"");
            parameters.put("nottype", cu.getType());

            JsonRequest jsonRequest =  new JsonObjectRequest(Request.Method.POST,url,parameters,new Response.Listener<JSONObject>(){

                @Override
                public void onResponse(JSONObject response) {
                    pDialog.dismiss();
                    parseNotificationRemoveResponse(response.toString());

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

                            parseNotificationRemoveResponse(res);

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


    private void parseNotificationRemoveResponse(String response){

        Log.e("wasapii","Notification response "+response);

        try {

            JSONObject jsonObject = new JSONObject(response);

            int flag  = jsonObject.getInt("Flag");
            //String errorMessage = jsonObject.has("Message") ? jsonObject.getString("Message"):"no notification found";

            if(flag==1){
                    int type = 0;//Integer.parseInt(userObject.getString("notification_type"));

                    if(type == 2){
                        adapter.remove(pos);
                        adapter.notifyDataSetChanged();
                    }
                    else {
                        adapter.remove(pos);
                        adapter.notifyDataSetChanged();
                    }
                adapter = new MyAdapter();
                listview.setAdapter(adapter);
            }else{
                //Toast.makeText(ctx,errorMessage,Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
