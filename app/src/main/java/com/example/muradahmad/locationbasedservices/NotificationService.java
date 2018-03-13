package com.example.muradahmad.locationbasedservices;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import java.util.Random;

/**
 * Created by antmanni on 3/12/18.
 */

public class NotificationService extends Service {

    private static final String TAG = "NotificationService";

    private boolean isRunning;
    private Context context;
    private Thread backgroundThread;
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;


    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        this.context = this;
        this.isRunning = false;
        this.backgroundThread = new Thread(myTask);
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.e(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.e(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }

    }

    private Runnable myTask = new Runnable() {
        public void run() {
            //Send the notification and reset alarm
            Log.d(TAG, "Running");

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permissions not set!");
            }
            else{
                Location location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    Log.d(TAG, "Latitude: " + latitude);
                    Log.d(TAG, "Longitude: " + longitude);
                    //Check that we are in University of Oulu
                    if(latitude >= 65.056602 && latitude <= 65.06185){
                        Log.d(TAG, "Latitude passed");
                        if(longitude >=25.462981 && longitude <= 25.469574) {
                            Log.d(TAG, "Longitude passed");
                            Notification(getApplicationContext(), latitude, longitude);
                        }
                    }


            }
            resetAlarm();
            stopSelf();


            }

        }
    };

    @Override
    public void onDestroy() {
        this.isRunning = false;
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.e(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!this.isRunning) {
            this.isRunning = true;
            this.backgroundThread.start();
        }
        return START_STICKY;
    }

    public void Notification(Context context, double lat, double lon) {


        Log.d(TAG, "Sending notification");

        String uri = "http://ab3000.net/surroundings/index.php?latitude=" + lat +"&longitude=" + lon;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        PendingIntent pIntent=
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //For newer phones
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Notification notification  = new Notification.Builder(context)
                    .setCategory(Notification.CATEGORY_MESSAGE)
                    .setContentTitle("Location found")
                    .setContentText("Click here to fill the survey")
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setAutoCancel(true)
                    .setContentIntent(pIntent)
                    .setVisibility(Notification.VISIBILITY_PUBLIC).build();


            // Create Notification Manager
            NotificationManager notificationmanager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            // Build Notification with Notification Manager
            notificationmanager.notify(0000, notification);
        }

        //For older phones
        else{
            Log.d(TAG, "Build version older than Lollipop");
            NotificationCompat.Builder builder = new NotificationCompat.Builder(
                    context)
                    // Set Icon
                    .setSmallIcon(R.drawable.ic_stat_name)
                    // Set Ticker Message
                    .setTicker("Test Text")
                    // Set Title
                    .setContentTitle("Location found")
                    // Set Text
                    .setContentText("Click here to fill the survey")
                    // Add an Action Button below Notification
                    .addAction(R.drawable.ic_launcher_background, "Action Button", pIntent)
                    // Set PendingIntent into Notification
                    .setContentIntent(pIntent)
                    // Dismiss Notification
                    .setAutoCancel(true);

            // Create Notification Manager
            NotificationManager notificationmanager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            // Build Notification with Notification Manager
            notificationmanager.notify(0000, builder.build());


        }
    }

    public void resetAlarm(){
        //Resets the alarm and gives it random time
        Log.d(TAG, "Reseting AlarmManager");
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarm = new Intent(getApplicationContext(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarm, 0);
        alarmManager.cancel(pendingIntent);

        Random rn = new Random();
        //Random number between 1 to 3 hours
        int n = rn.nextInt((10800000 - 3600000 + 1)) + 3600000;

        Log.d(TAG, "Alarm time in milliseconds: " + n);


        alarmManager.set(AlarmManager.RTC,System.currentTimeMillis()+n, pendingIntent);
    }




    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }




}
