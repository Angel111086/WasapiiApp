package com.wasapii.adisoftin;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.wasapii.adisoftin.db.DataHelper;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by adisoft3 on 9/12/16.
 */

public class SplashActivity extends Activity {


    private Context ctx;
    private DataHelper dh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.splash);

        ctx = SplashActivity.this;

        isAllPermissionsGranted(ctx);

        dh = DataHelper.getInstance(ctx);

//        test();

    }

    private void test(){

        // Original text
        String theTestText = "This is just a simple test";
        Log.d("wsa","\n[ORIGINAL]:\n" + theTestText + "\n");

        // Set up secret key spec for 128-bit AES encryption and decryption
        SecretKeySpec sks = null;
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed("any data used as random seed".getBytes());
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(128, sr);
            sks = new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");

        } catch (Exception e) {
            Log.e("sa", "AES secret key spec error");
        }

        // Encode the original data with AES
        byte[] encodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, sks);
            encodedBytes = c.doFinal(theTestText.getBytes());
        } catch (Exception e) {
            Log.e("sa", "AES encryption error");
        }

        Log.e("wasapii","[ENCODED]:\n" +
                Base64.encodeToString(encodedBytes, Base64.DEFAULT) + "\n");

        // Decode the encoded data with AES
        byte[] decodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, sks);
            decodedBytes = c.doFinal(encodedBytes);
        } catch (Exception e) {
            Log.e("sa", "AES decryption error");
        }
        Log.e("was","[DECODED]:\n" + new String(decodedBytes) + "\n");
    }



     public void isAllPermissionsGranted(Context ctx) {

        if (Build.VERSION.SDK_INT >= 23) {

            if (ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {

                runHandler();

            } else {

                ArrayList<String> unGrantedPermissionsList = new ArrayList<>();

                if(ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    unGrantedPermissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }


                if (ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    unGrantedPermissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
                }

                String[] permissionsArray = new String[unGrantedPermissionsList.size()];
                permissionsArray = unGrantedPermissionsList.toArray(permissionsArray);

                Log.v("com.adisoft.mls.temp", "Permission is revoked");

                ActivityCompat.requestPermissions(SplashActivity.this, permissionsArray, 101);

            }

        } else { //permission is automatically granted on sdk<23 upon installati
            Log.v("com.adisoft.mls.temp", "Permission is granted");
            runHandler();
        }


    }

    private void runHandler(){

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                actionNext();
            }

        }, 3000);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Map<String, Integer> perms = new HashMap<String, Integer>();
        // Initial

        // perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
        perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);

        perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

        // Fill with result
        for (int i = 0; i < permissions.length; i++)
            perms.put(permissions[i], grantResults[i]);

        // Check for ACCESS_FINE_LOCATION
//        if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//            // All Permissions Granted
//            runHandler();
//
//        }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                actionNext();
            }

        }, 500);

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void actionNext(){
        Intent i ;
        if(dh.checkUserInfo()  ) {
             i = new Intent(SplashActivity.this, ChosePlaceActivity.class);

        }else{
             i = new Intent(SplashActivity.this, LoginActivity.class);

        }
        startActivity(i);
        finish();
    }

}

