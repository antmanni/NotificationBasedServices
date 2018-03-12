package com.example.muradahmad.locationbasedservices;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by antmanni on 3/12/18.
 */

public class NotificationReceiver extends BroadcastReceiver{

    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {


        Log.d(TAG, "onReceive");
        Intent background = new Intent(context, NotificationService.class);
        context.startService(background);

    }


}
