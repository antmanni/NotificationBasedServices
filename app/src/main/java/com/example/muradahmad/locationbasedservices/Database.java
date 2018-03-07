package com.example.muradahmad.locationbasedservices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by muradahmad on 20/02/2018.
 */

public class Database extends SQLiteOpenHelper {


    public static final String LOCATION_DATABASE = "database.db";
    public static final String LOCATION_TABLE = "Database_Table";
    public static final String USER_LOCATION = "Location";
    public static final String USER_COORDINATES = "Coordinates";
    public static final String MESSAGE = "Message";


    public static Database instance;


    public static  Database getInstance(Context context) {
        if (instance == null) {
            instance = new Database(context);
        }
        return instance;
    }


    public Database(Context context) {
        super(context, LOCATION_DATABASE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + LOCATION_TABLE + "(Location TEXT, Coordinates TEXT, Message TEXT )");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(" DROP TABLE IF EXISTS " + LOCATION_TABLE);
        onCreate(db);

    }

    public boolean insertData(String location, String coordinates , String message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_LOCATION, location);
        contentValues.put(USER_COORDINATES, coordinates);
        contentValues.put(MESSAGE, message);

        long result = db.insert(LOCATION_TABLE, null, contentValues);
        if (result == -1) {

            return false;
        } else {
            return true;
        }
    }
    public Cursor viewData(){
        String selectQuery= "SELECT * FROM " + LOCATION_TABLE +" ORDER BY column DESC LIMIT 1";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
        // close cursor
        //close database





    }

}
