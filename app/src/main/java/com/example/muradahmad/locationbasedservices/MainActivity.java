package com.example.muradahmad.locationbasedservices;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Random;

public class MainActivity extends AppCompatActivity {



   // public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
   public static final String TAG = "MainActivity";

    private LocationManager locationManager;
    private LocationListener locationListener;
    private FusedLocationProviderClient mFusedLocationClient;
    private CheckBox notificationscheckbox;
    private GeofencingClient mGeofencingClient;
    private PendingIntent mGeofencePendingIntent;
    private final int GEOFENCE_REQ_CODE = 0;


    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    TextView txtLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtLocation = (TextView) findViewById(R.id.txtdisplayLocation);
        notificationscheckbox = (CheckBox) findViewById(R.id.notificationscheckbox);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mGeofencingClient = LocationServices.getGeofencingClient(this);

        Log.d(TAG, "inside the oncreate method before location manager");

        // commented working code which gets user current loaction and update location in every 3

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Log.d(TAG, "inside the oncreate method after location manager");

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Log.d(TAG, "inside onlocationchanged method");

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);


            }

        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling

                Log.d(TAG, "inside the if condition before requestPermissions");
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);

                return;
            }
        } else {

            Log.d(TAG, "inside the else before the  requestLocation Update");

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 3, locationListener);
            Log.d(TAG, "called from Checking Permissions");
        }

        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);

        if (location != null) {

            Log.i(TAG, "Location achieved!");
            Log.d(TAG, "called from not null location");

        } else {

            Log.i(TAG, "No location :(");
            Log.d(TAG, "called from null location ");

        }


        notificationscheckbox.setOnClickListener(new View.OnClickListener() {

             @Override
             public void onClick(View v) {
                 if (notificationscheckbox.isChecked()) {
                     startNotifications();
                 } else {
                     stopNotifications();
                 }
             }
         });

        startLocationMonitoring();


    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 10: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        Log.d(  TAG,"inside the swtich case 10 request Location Update");

                        //Request location updates:
                        locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 0, 3, (LocationListener) this);
                        Log.d(  TAG,"called from onRequestPermissionResult method");
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d(  TAG,"called from on requestPermissionResult else");

                }
                return;
            }

        }
    }


    @Override
    protected void onResume() {
        Log.d(TAG, "onResume called");
        super.onResume();


    }


    @Override
    protected void onStart(){
        Log.d( TAG,"onStart called");
        super.onStart();

    }


    private void startLocationMonitoring(){
        Log.d( TAG,"startLocationMonitoring called");
        try {

            LocationRequest locationRequest = LocationRequest.create()
                    .setInterval(10000)
                    .setFastestInterval(5000)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

          LocationCallback  mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    for (Location location : locationResult.getLocations()) {
                        // Update UI with location data
                        // ...
                        txtLocation.setText("Location:" + location.getLatitude() + " ,  " + location.getLongitude());
                    }
                };
            };
        mFusedLocationClient.requestLocationUpdates(locationRequest,mLocationCallback,null);

        }catch (SecurityException e){
            Log.d(TAG,"Security Exception"+ e.getMessage());
        }
    }

    private void startNotifications(){


        Log.d(TAG, "Started notifications");

        Random rn = new Random();
        //Random number between 1 to 3 hours
        int n = rn.nextInt((10800000 - 3600000 + 1)) + 3600000;


        Log.d(TAG, "First notification time in milliseconds: " + n);
        /*
        Intent alarm = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarm, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager!=null){
            alarmManager.set(AlarmManager.RTC, System.currentTimeMillis()+n, pendingIntent);
        }*/

        //TODO geofences here
        Geofence geofence = new Geofence.Builder()
                .setRequestId("travel") // Geofence ID
                .setCircularRegion(65, 25, 10) // defining fence region
                .setExpirationDuration(-1) //Never expiring
                // Transition types that it should look for
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();


        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getLocationPermission();
        }
        mGeofencingClient.addGeofences(createGeofencingRequest(geofence), createGeofencePendingIntent())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //  your succes code
                        Toast t = Toast.makeText(getApplicationContext(), "Alert added", Toast.LENGTH_SHORT);
                        t.show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // your fail code;
                        Log.e(TAG, "Geofence failure");
                    }
                });


    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void stopNotifications(){
        //TODO
        Log.d(TAG,"Stopped notifications");

        Intent alarm = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarm, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager!=null) {
            alarmManager.cancel(pendingIntent);
        }

    }

    private GeofencingRequest createGeofencingRequest(Geofence geofence){
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(geofence);
        return builder.build();

    }

    private PendingIntent createGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;

    }

}
