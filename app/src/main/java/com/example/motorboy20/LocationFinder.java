package com.example.motorboy20;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;

import org.jetbrains.annotations.Nullable;

import android.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class LocationFinder extends Service implements LocationListener {
    private static final String TAG = "Service";
    public static final int timer = 2000; // 2 seconds
    public static Boolean isRunning = false;
    private static final int TODO = 0;
    Context context;
    boolean isGPSEnabled = false;// flag for network status
    boolean isNetworkEnabled = false;// flag for GPS status
    boolean canGetLocation = false;
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude// The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;//5; // 5 meters// The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 0;//10 * 10 * 1; // 0.1 seconds// Declaring a Location Manager
    protected LocationManager locationManager;

    /* public LocationFinder(Context context) {
         this.context = context;
         getLocation();
     }*/

    @Override
    public void onCreate() {

        super.onCreate();
        locationManager = (LocationManager) this
                .getSystemService(LOCATION_SERVICE);// getting GPS status

        final Handler mHandler = new Handler();
        final Runnable mHandlerTask = new Runnable(){
            @Override
            public void run() {
                if (!isRunning) {
                    startListening();
                }
              //  mHandler.postDelayed(mHandlerTask, timer);
            }
        };

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);// getting network status
        /*isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);*/
        if (!isGPSEnabled) {// && !isNetworkEnabled) {
            // no network provider is enabled
            showSettingsAlert();
            Log.e("Network-GPS", "Disable");
        } else {
            this.canGetLocation = true;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return TODO;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        if (isGPSEnabled) {
            if (locationManager == null) {

            }

            //Log.e(“GPS Enabled”, “GPS Enabled”);
         else if (locationManager != null) {
                location = locationManager
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }

        }


        return Service.START_STICKY;
    }

    @androidx.annotation.Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.d("Broadcast_Longitude", String.valueOf(longitude));
            Intent result = new Intent();
            //  result.putExtra("name", "Pothole detected" +"Pothole DeltaX"+ String.valueOf(deltaX)+ " Pothole DeltaY"+ String.valueOf(deltaY)+" Pothole DeltaZ"+ String.valueOf(deltaZ));
            result.putExtra("latitude", String.valueOf(latitude));
            result.putExtra("longitude", String.valueOf(longitude));

            result.setAction("LocationFinder");
            LocalBroadcastManager.getInstance(LocationFinder.this).sendBroadcast(result);
            locationManager.removeUpdates(this);
            stopListening();
        }
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        /*
         * Diese Methode wird immer dann aufgerufen, wenn sich der GPS-Status
         * verändert
         */
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                Log.v(TAG, "Statusänderung: Außer Betrieb");
                Toast.makeText(this, "Statusänderung: Außer Betrieb",
                        Toast.LENGTH_SHORT).show();
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.v(TAG, "Statusänderung: vorübergehend nicht verfügbar");
                Toast.makeText(this, "Statusänderung: vorübergehend nicht verfügbar",
                        Toast.LENGTH_SHORT).show();
                break;
            case LocationProvider.AVAILABLE:
                Log.v(TAG, "Statusänderung: GPS Verfügbar");
                Toast.makeText(this, "Statusänderung: GPS Verfügbar",
                        Toast.LENGTH_SHORT).show();
                break;
        }
    }
    @Override
    public void onProviderEnabled(String provider) {
    }@Override
    public void onProviderDisabled(String provider) {
        /*
         * Diese Methode wird aufgerufen wenn/falls das GPS in den Einstellungen
         * deaktiviert ist.
         */
        Log.v(TAG, "Deaktiviert");

        /* Aufrufen der GPS-Einstellungen */
       /* new AlertDialog.Builder(MainActivity. this )
                .setMessage( "GPS is not enable.Please activate GPS before to continue" )
                .setPositiveButton( "Settings" , new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick (DialogInterface paramDialogInterface , int paramInt) {
                                startActivity( new Intent(Settings. ACTION_LOCATION_SOURCE_SETTINGS )) ;
                            }
                        })
                .setNegativeButton( "Cancel" , null )
                .show() ;*/

    }@Nullable

    @SuppressLint("MissingPermission")
 /*   private Location getLocation() {
        try {
            locationManager = (LocationManager) context
                    .getSystemService(LOCATION_SERVICE);// getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);// getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled){// && !isNetworkEnabled) {
                // no network provider is enabled
                showSettingsAlert();
                 Log.e("Network-GPS", "Disable");
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
              /*  if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    // Log.e(“Network”, “Network”);
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                    // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        //Log.e(“GPS Enabled”, “GPS Enabled”);
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }return location;
    }*/
    private void startListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

            if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        isRunning = true;
    }
    private void stopListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);
        }
        isRunning = false;
    }
    private double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }
    private double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }
    private boolean canGetLocation() {
        return this.canGetLocation;
    }
    private void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS settings");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }
}
