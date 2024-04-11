package com.wasapii.adisoftin;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;
//import com.crashlytics.android.Crashlytics;
//


/**
 * Created by adisfot5 on 15/6/16.
 */
public class MyApplication extends Application {

    private  static MyApplication instance;

    private RequestQueue mRequestQueue;
    private static Context context;

    public static final String TAG = MyApplication.class
            .getSimpleName();


    public static synchronized MyApplication getInstance() {
        if (instance == null) {
            throw  new IllegalStateException("Application not craeted");
        }
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context=this;
        instance=this;
        FirebaseApp.initializeApp(context);

    }

    public static Context getContext(){
        return context;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }


    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }


    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }




}
