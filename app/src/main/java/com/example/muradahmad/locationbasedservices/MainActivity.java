package com.example.muradahmad.locationbasedservices;

import android.Manifest;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {



   // public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
   public static final String TAG = "MainActivity";

    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final String GEOFENCE_REQ_ID = "Linnanma";
    private static final float GEOFENCE_RADIUS = 100; // in meters
    private static final Double LONGITUDE = 25.5;
    private static final Double LATITUDE = 65.05;

    private PendingIntent mGeofencePendingIntent;


    private GeofencingClient mGeofencingClient;
    GoogleApiClient googleApiClient = null;
    private FusedLocationProviderClient mFusedLocationClient;


    TextView txtLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtLocation = (TextView) findViewById(R.id.txtdisplayLocation);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mGeofencingClient = LocationServices.getGeofencingClient(this);
        //mGeofencingClient = LocationServices.getGeofencingClient(this);


/*

       Geofence geofence = new Geofence.Builder()
                .setRequestId(GEOFENCE_REQ_ID)
                .setCircularRegion(LATITUDE, LONGITUDE, GEOFENCE_RADIUS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
               .setNotificationResponsiveness(1000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();


        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build();
*/



        Log.d(TAG, "inside the oncreate method before location manager");

        // commented working code which gets user current loaction and update location in every 3 m



        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Log.d(TAG, "inside the oncreate method after location manager");

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

               // txtLocation.append("Location:" + location.getLatitude() + " ,  " + location.getLongitude());
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



        // create GoogleApiClient
       //createGoogleApi();

        startGeofenceMonitoring();
        startLocationMonitoring();
        getGeofencePendingIntent();
        stopGeoFenceMonitoring();



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
    protected void onResume(){
        Log.d( TAG,"onResume called");
        super.onResume();
        int response = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if(response != ConnectionResult.SUCCESS){
            Log.d( TAG,"GooglePlay services not available");
            GoogleApiAvailability.getInstance().getErrorDialog(this,response,1).show();
        }else {
            Log.d( TAG,"GooglePlay services available");
        }

    }



    // for Battery Effiency USe the onStart and onStop methods



    @Override
    protected void onStart(){
        Log.d( TAG,"onStart called");
        super.onStart();
      // googleApiClient.reconnect();

    }

    @Override
    protected void onStop(){
        Log.d( TAG,"onStop called");
        super.onStop();
      //  googleApiClient.disconnect();

    }


    private PendingIntent getGeofencePendingIntent() {
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
                        txtLocation.append("Location:" + location.getLatitude() + " ,  " + location.getLongitude());
                    }
                };
            };
mFusedLocationClient.requestLocationUpdates(locationRequest,mLocationCallback,null);
           /* LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, new com.google.android.gms.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.d(TAG,"Location update LAT/long" + location.getLatitude() +"  "+ location.getLongitude());
                }
            });*/




        }catch (SecurityException e){
            Log.d(TAG,"Security Exception"+ e.getMessage());
        }
        }

    private void startGeofenceMonitoring(){
        Log.d( TAG,"startGeofenceMonitoring called");
        try {

            Geofence geofence = new Geofence.Builder()
                    .setRequestId(GEOFENCE_REQ_ID)
                    .setCircularRegion(LATITUDE, LONGITUDE, GEOFENCE_RADIUS)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setNotificationResponsiveness(1000)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();


            GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                    .addGeofence(geofence)
                    .build();


            Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
            // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
            // calling addGeofences() and removeGeofences().
           PendingIntent pendingIntent = PendingIntent.getService(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);


           /*if(!googleApiClient.isConnected()) {
               Log.d(TAG, "googleApiClient is not Connected");
           }else{*/

               mGeofencingClient.addGeofences( geofencingRequest, pendingIntent).addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void aVoid) {
                       Log.d(TAG, "Successfully added geofence");
                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Log.d(TAG, "Failed to add geofence");
                   }
               });

                      /* .setResultCallback(new ResultCallback<Status>() {
                           @Override
                           public void onResult(@NonNull Status status) {
                               if (status.isSuccess()) {
                                   Log.d(TAG, "Successfully added geofence");
                               } else{
                                   Log.d(TAG, "Failed to add geofence");
                               }
                           }
                       });*/



           //}

        }catch (SecurityException e){
            Log.d(TAG,"Security Exception"+ e.getMessage());
        }
        }







        private void stopGeoFenceMonitoring(){

        Log.d(TAG,"stopMonitoring is called");
            ArrayList<String> geoFenceIds = new ArrayList<String>();
        geoFenceIds.add(GEOFENCE_REQ_ID);
        mGeofencingClient.removeGeofences(geoFenceIds);
        //LocationServices.GeofencingApi.removeGeofences(googleApiClient, geoFenceIds);

        }





    // Create GoogleApiClient instance
    private void createGoogleApi() {
        Log.d(TAG, "createGoogleApi()");
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {

                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        }
                    })
                    .addApi(LocationServices.API)
                    .build();
        }
    }





}
