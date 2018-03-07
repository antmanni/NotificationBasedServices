package com.example.muradahmad.locationbasedservices;

import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by muradahmad on 20/02/2018.
 */

public class LocationModel {
    String location;
    String coordinates;
    String message;
    public LocationModel(String location, String coordinates, String message) {
        this.location = location;
        this.coordinates = coordinates;
        this.message = message;
    }


}
