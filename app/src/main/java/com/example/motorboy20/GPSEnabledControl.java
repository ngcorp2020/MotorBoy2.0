package com.example.motorboy20;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class GPSEnabledControl extends Service {
    private static final String TAG = "GPSEnabledControl";

    @Nullable
    @Override
    public void onCreate()
    {
        // do something long
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                doWork();
                locationEnabled();
                Log.w(TAG, "WordThread iterated");


            }
        };
        new Thread(runnable).start();

    }
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void locationEnabled () {
        LocationManager lm = (LocationManager)
                getSystemService(Context. LOCATION_SERVICE ) ;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager. GPS_PROVIDER ) ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager. NETWORK_PROVIDER ) ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        if (!gps_enabled && !network_enabled) {
            Intent result = new Intent();
             result.putExtra("gpsoff", "gpsoff");


            result.setAction("GPSEnabledControl");
            LocalBroadcastManager.getInstance(GPSEnabledControl.this).sendBroadcast(result);

        }
    }
    private void doWork() {
        SystemClock.sleep(8000);
    }

}
