package com.example.motorboy20;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.motorboy20.App.CHANNEL_ID;

public class GoogleLocationFinder extends Service  implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    public int counter = 0;
    Context context;
    private static final long INTERVAL = 1000 * 2;
    private static final long FASTEST_INTERVAL = 1000 * 1;
    private static final String TAG = "GoogleLocationFinder";
    public static final String NOTIFICATION_ID = "NOTIFICATION_ID";
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;
    String latitude,longitude;
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    @Override
    public void onCreate() {

        super.onCreate();
        //show error dialog if GooglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            //finish();
        }
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        RemoteViews collapsedView = new RemoteViews(getPackageName(),
                R.layout.notification_collapsed);


        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationIntent.putExtra(NOTIFICATION_ID, 0);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.mapbox_compass_icon)
              //  .setCustomContentView(collapsedView)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_close_notif,getString(R.string.close_text), pendingIntent)
                .build();
       // collapsedView.setOnClickPendingIntent(R.id.notification_button_close,pendingIntent);
        startForeground(1, notification);
        mGoogleApiClient.connect();



    return Service.START_NOT_STICKY;
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(),this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
        super.onTaskRemoved(rootIntent);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i("EXIT", "ondestroy!");

    }


    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, (Activity) getApplicationContext(), 0).show();
            return false;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }
    protected void startLocationUpdates() {
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

        Log.d(TAG, "Location update started ..............: ");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateLocation();
    }
    private void updateLocation() {
        Log.d(TAG, "Location update initiated .............");
        if (null != mCurrentLocation) {
            latitude= String.valueOf(mCurrentLocation.getLatitude());
            longitude = String.valueOf(mCurrentLocation.getLongitude());
            Log.d("Broadcast_Longitude",longitude);
            Intent result = new Intent();
            //  result.putExtra("name", "Pothole detected" +"Pothole DeltaX"+ String.valueOf(deltaX)+ " Pothole DeltaY"+ String.valueOf(deltaY)+" Pothole DeltaZ"+ String.valueOf(deltaZ));
            result.putExtra("latitude", latitude);
            result.putExtra("longitude", longitude);

            result.setAction("GoogleLocationFinder");
            LocalBroadcastManager.getInstance(GoogleLocationFinder.this).sendBroadcast(result);


        } else {
            Log.d(TAG, "location is null ...............");
        }
    }

}
