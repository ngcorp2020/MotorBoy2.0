package com.example.motorboy20;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.LocationResult;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.cos;

public class LocationComparator extends Service {
    public int counter = 0;
    Context context;
    private DBHelper mydb;
    private double latitude=0.0,longitude=0.0,longitude_database = 0.0, latitude_database = 0.0;
    private String detected_Poi;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {

        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LocalBroadcastManager.getInstance(LocationComparator.this).registerReceiver(receiver, new IntentFilter("GoogleLocationFinder"));

        mydb = new DBHelper(this);
        mydb.insertCoordinates();
        mydb.getAllCoordinates();
        return Service.START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(),this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
        super.onTaskRemoved(rootIntent);
    }
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //  Toast.makeText(MainActivity.this,intent.getStringExtra("name"),Toast.LENGTH_LONG).show();
            latitude=Double.parseDouble(intent.getStringExtra("latitude"));
            longitude=Double.parseDouble(intent.getStringExtra("longitude"));
          /*  Log.d("Latitude", intent.getStringExtra("latitude"));
            Log.d("Longitude", intent.getStringExtra("longitude"));
            latitude=Double.parseDouble(intent.getStringExtra("latitude"));
            longitude=Double.parseDouble(intent.getStringExtra("longitude"));
           */
          detected_Poi=compareCoordinates(longitude,latitude);
            if(!detected_Poi.contains("noPoi"))
            {
                Intent result_Poi = new Intent();
                //  result.putExtra("name", "Pothole detected" +"Pothole DeltaX"+ String.valueOf(deltaX)+ " Pothole DeltaY"+ String.valueOf(deltaY)+" Pothole DeltaZ"+ String.valueOf(deltaZ));
                result_Poi.putExtra("poi_type", detected_Poi);

                result_Poi.setAction("poi_type");
                LocalBroadcastManager.getInstance(LocationComparator.this).sendBroadcast(result_Poi);


            }

        }

    };
    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i("EXIT", "ondestroy!");

    }

    private double getDistanceFromLatLonInMeters ( double lat1, double lon1, double lat2,
                                                   double lon2){
        double deg2rad = 0.017453292519943295; // === Math.PI / 180
        lat1 *= deg2rad;
        lon1 *= deg2rad;
        lat2 *= deg2rad;
        lon2 *= deg2rad;
        double a = (
                (1 - cos(lat2 - lat1)) +
                        (1 - cos(lon2 - lon1)) * cos(lat1) * cos(lat2)
        ) / 2;

        return 1000 * 12742 * Math.asin(Math.sqrt(a)); // Distance in meters
    }

    private String compareCoordinates(double longitude,double latitude)
    {
        List<PoiCoordinates> poiCoordinates = mydb.coordinates;
        for (int i = 0; i < poiCoordinates.size(); i++) {
            latitude_database = Double.parseDouble(poiCoordinates.get(i).getLatitude());
            longitude_database = Double.parseDouble(poiCoordinates.get(i).getLongitude());
            String poiType = poiCoordinates.get(i).getPoi_Type();
        }

        for (int i = 0; i < poiCoordinates.size(); i++) {
            latitude_database = Double.parseDouble(poiCoordinates.get(i).getLatitude());
            longitude_database = Double.parseDouble(poiCoordinates.get(i).getLongitude());
            String poiType = poiCoordinates.get(i).getPoi_Type();
            double distance = getDistanceFromLatLonInMeters(latitude, longitude, latitude_database, longitude_database);
            Log.d("MainActivity", "Distance: " + distance);
            Log.d("MainActivity", "Latitude: " + latitude);
            Log.d("MainActivity", "Longitude: " + longitude);
            Log.d("MainActivity", "Pothole: " + poiType);
            if (distance < 400) {

                return poiType;
            }
        }

        return "noPoi";
    }

    }
