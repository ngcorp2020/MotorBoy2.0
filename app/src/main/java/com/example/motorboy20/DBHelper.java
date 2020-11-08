package com.example.motorboy20;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.jar.Attributes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.os.Debug;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "GPS_Coordinates.sql";
    public static final String TABLE_NAME = "poi";
    public static final String ID = "id";
    public static final String LONGITUDE = "longitude";
    public static final String LATITUDE = "latitude";
    public static final String Poi_Type = "poi_type";
    public List<PoiCoordinates> coordinates=new ArrayList <PoiCoordinates>();

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
       /* db.execSQL(
                "create table if not exists poi " +
                        "(longitude text,latitude text, poi_type text)"
        );*/
        db.execSQL("DROP TABLE IF EXISTS poi");
        db.execSQL(
                "create table  poi " +
                        "(longitude text,latitude text, poi_type text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       /* // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS coordinates");
        onCreate(db);*/
    }

    public boolean insertCoordinates() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("longitude","7.564126");
        contentValues.put("latitude", "50.338098");
        contentValues.put("poi_type", "80");
        db.insert("poi", null, contentValues);


        contentValues.put("longitude","7.570049");
        contentValues.put("latitude", "50.342130");
        contentValues.put("poi_type", "100");
        db.insert("poi", null, contentValues);

        contentValues.put("longitude","7.573686");
        contentValues.put("latitude", "50.343089");
        contentValues.put("poi_type", "70");
        db.insert("poi", null, contentValues);

        contentValues.put("longitude","7.578771");
        contentValues.put("latitude", "50.342699");
        contentValues.put("poi_type", "50");
        db.insert("poi", null, contentValues);

        contentValues.put("longitude","7.583280");
        contentValues.put("latitude", "50.346735");
        contentValues.put("poi_type", "radar_70");
        db.insert("poi", null, contentValues);

        contentValues.put("longitude","7.583033");
        contentValues.put("latitude", "50.349740");
        contentValues.put("poi_type", "90");
        db.insert("poi", null, contentValues);

        contentValues.put("longitude","7.588301");
        contentValues.put("latitude", "50.353307");
        contentValues.put("poi_type", "120");
        db.insert("poi", null, contentValues);

        contentValues.put("longitude","7.589052");
        contentValues.put("latitude", "50.356778");
        contentValues.put("poi_type", "pothole");
        db.insert("poi", null, contentValues);

        contentValues.put("longitude","7.588226");
        contentValues.put("latitude", "50.360591");
        contentValues.put("poi_type", "50");
        db.insert("poi", null, contentValues);

        contentValues.put("longitude","7.588065");
        contentValues.put("latitude", "50.364821");
        contentValues.put("poi_type", "radar_50");
        db.insert("poi", null, contentValues);

        contentValues.put("longitude","7.587475");
        contentValues.put("latitude", "50.369646");
        contentValues.put("poi_type", "radar_70");


        db.insert("poi", null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from coordinates where id=" + id + "", null);
        return res;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return numRows;
    }

    public boolean updateCoordinates(Integer id, String longitude, String latitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("longitude", longitude);
        contentValues.put("latitude", latitude);
        db.update("coordinates", contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public Integer deleteCoordinates(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("coordinates",
                "id = ? ",
                new String[]{Integer.toString(id)});
    }

    public void getAllCoordinates() {

        // ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from poi", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            PoiCoordinates poi =new PoiCoordinates();
            poi.setLatitude(res.getString(res.getColumnIndex(LATITUDE)));
            poi.setLongitude(res.getString(res.getColumnIndex(LONGITUDE)));
            poi.setPoi_Type(res.getString(res.getColumnIndex(Poi_Type)));
           coordinates.add(poi);
            //Log.d("MainActivity", "POi: " + res.getString(res.getColumnIndex(Poi_Type)));
            //Log.d("MainActivity", "Longitude: " + res.getString(res.getColumnIndex(LONGITUDE)));

            res.moveToNext();
        }

    }
}