package com.wasapii.adisoftin.Receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.wasapii.adisoftin.HomeActivity;
import com.wasapii.adisoftin.R;
import com.wasapii.adisoftin.fragments.PeopleFragment;
import com.wasapii.adisoftin.fragments.ProfileFragment;

/**
 * Created by root on 26/6/17.
 */

public class ProximityReceiver extends BroadcastReceiver {

    //    private static final String TAG = "Proximity";
    private static final int NOTIFICATION_ID = 1000;

    @SuppressWarnings("deprecation")
    @Override
    public void onReceive(Context context, Intent intent) {
        String key = LocationManager.KEY_PROXIMITY_ENTERING;
        Boolean entering = intent.getBooleanExtra(key, false);
        if (entering) {
            Log.d(getClass().getSimpleName(), "entering");
        } else {
            Log.d(getClass().getSimpleName(), "exiting");
        }

        intent = new Intent(context, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
        intent.putExtra("Noti",0);
        //context.startActivity(intent);



        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification.Builder nb = new Notification.Builder(context);
        nb.setContentTitle("Wasapii Alert!");
        nb.setContentText("Are You are still at this Location.");
        nb.setContentIntent(pendingIntent);
        nb.setSmallIcon(R.mipmap.ic_launcher);
        nb.setOngoing(true);
        nb.setSound(defaultSoundUri);
        //Notification notification = createNotification();
        Notification notification = nb.build();
        //notification.setLatestEventInfo(context, "Proximity Alert!", "You are near your point of interest.", pendingIntent);

        notificationManager.notify(NOTIFICATION_ID, notification);
    }


//    private Notification createNotification() {
//        Notification notification = new Notification();
//        notification.icon = R.mipmap.ic_launcher;
//
//        notification.when = System.currentTimeMillis();
//        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
//        notification.defaults |= Notification.DEFAULT_VIBRATE;
//        notification.defaults |= Notification.DEFAULT_LIGHTS;
//        notification.ledARGB = Color.WHITE;
//        notification.ledOnMS = 1500;
//        notification.ledOffMS = 1500;
//        return notification;
//
//    }


//    @Override
//    public void onReceive(Context context, Intent intent) {
//
//        if (intent.getData() != null) {
//            Log.v(TAG, intent.getData().toString());
//        }
//        Bundle extras = intent.getExtras();
//        if (extras != null) {
//            Log.v("", "Message: " + extras.getString("message"));
//            Log.v("", "Entering? " + extras.getBoolean(LocationManager.KEY_PROXIMITY_ENTERING));
//            Toast.makeText(context, "Proximity Alert", Toast.LENGTH_LONG).show();
//        }
//    }
}