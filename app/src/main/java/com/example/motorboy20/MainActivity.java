package com.example.motorboy20;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import java.util.List;

import static java.lang.Math.cos;

public class MainActivity extends AppCompatActivity {
   // private static final int REQUEST_CODE_PERMISSIONS =101 ;
   private static final int SYSTEM_ALERT_WINDOW_PERMISSION = 2084;
    ImageView mainImageView;
    final String TAG = "MainActivity";
   // LocationManager lm;
    private String poi_type;
    private DBHelper mydb;
    private static final int DRAW_OVER_OTHER_APP_PERMISSION = 123;
    double longitude = 0.0, latitude = 0.0, longitude_database = 0.0, latitude_database = 0.0;
    boolean backButtonPressed=false;
    // The minimum distance to change Updates in meters
  //  private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10

    // The minimum time between updates in milliseconds
   // private static final long MIN_TIME_BW_UPDATES = 0; // 1 minute


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            askPermission();
        }
        locationEnabled();
        //moveTaskToBack(true);
        //requestLocationPermission();
      /*  lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
*/
        mainImageView = (ImageView) findViewById(R.id.mainImageView);
        //mainImageView.setImageResource(R.drawable.vitesse_50);//set the source in java class
        mainImageView.setVisibility(View.INVISIBLE);

            Intent intent = new Intent(getApplicationContext(), GoogleLocationFinder.class);
            //  intent.putExtra("LocationFinder", "gps");
            startService(intent);
            Intent intent_poi = new Intent(getApplicationContext(), LocationComparator.class);

            startService(intent_poi);

            Intent gps_off = new Intent(getApplicationContext(), GPSEnabledControl.class);

            startService(gps_off);

        mainImageView = (ImageView) findViewById(R.id.mainImageView);
        //mainImageView.setImageResource(R.drawable.vitesse_50);//set the source in java class
        mainImageView.setVisibility(View.INVISIBLE);
        mydb = new DBHelper(this);
        mydb.insertCoordinates();
        mydb.getAllCoordinates();








    }
    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(receiver_poi, new IntentFilter("poi_type"));
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(gps_off, new IntentFilter("GPSEnabledControl"));
        locationEnabled();
        Intent overlayIntent = getIntent();
        poi_type=overlayIntent.getStringExtra("POI");
        stopService(new Intent(MainActivity.this, OverlayWindow.class));

        if(poi_type!=null)
            changeImage(poi_type);
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationEnabled();



        }
    @Override
    protected void onPause() {

        /*
         * Wie sich gezeigt hat, verbraucht das GPS Strom wie verrückt, somit
         * ist der Akku wirklich sehr schnell am Ende. Aus diesem Grund wird
         * hier das GPS ausgeschaltet.
         */

          super.onPause();

        if(backButtonPressed==false) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                 //OverlayWindow overlayWindow = new OverlayWindow(this.getApplicationContext(),poi_type);
                Intent mServiceIntent = new Intent(getApplicationContext(), OverlayWindow.class);
                mServiceIntent.putExtra("poi_type",poi_type);
                if (!isMyServiceRunning(OverlayWindow.class)) {

                    getApplicationContext().startService(new Intent(MainActivity.this,OverlayWindow.class));
                }


                //finish();
            } else if (Settings.canDrawOverlays(this)) {
                // startService(new Intent(MainActivity.this, OverlayWindow.class));
                //finish();
                Intent mServiceIntent = new Intent(this, OverlayWindow.class);
                mServiceIntent.putExtra("poi_type",poi_type);
                if (!isMyServiceRunning(OverlayWindow.class)) {

                    startService(new Intent(MainActivity.this,OverlayWindow.class));
                }

            } else {
                askPermission();
                Toast.makeText(this, "You need System Alert Window Permission to do this", Toast.LENGTH_SHORT).show();
            }
        }

        //onResume();
    }
    private void askPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DRAW_OVER_OTHER_APP_PERMISSION) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    //Permission is not available. Display error text.
                    errorToast();
                    finish();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    private void errorToast() {
        Toast.makeText(this, "Draw over other app permission not available. Can't start the application without the permission.", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {

        /*
         * Die Anwendung kann hier einfach beendet werden, da eine Sicherung des
         * Zustands für diese Beispiel-Anwendung nicht nötig ist.
         */
        super.onStop();



    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w(TAG, "App destroyed");
        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(receiver_poi);//  important
        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(gps_off);     //   important

    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.d ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.d("isMyServiceRunning?", false+"");
        return false;
    }

    public boolean onCreateOptionsMenu (Menu menu){
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.activity_main_menu, menu);
            return true;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            int id = item.getItemId();
            switch (id) {
                case R.id.about_item:

                    return true;

                default:
                    return super.onOptionsItemSelected(item);
            }
        }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")
                .setMessage("Are you sure you want to close this activity?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        backButtonPressed=true;
                        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(receiver_poi);//  important
                        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(gps_off);     //   important
                        Intent myService = new Intent(MainActivity.this, LocationComparator.class);
                        stopService(myService);
                        myService = new Intent(MainActivity.this, GoogleLocationFinder.class);
                        stopService(myService);
                        myService = new Intent(MainActivity.this, OverlayWindow.class);
                        stopService(myService);
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           String action = intent.getAction();
            String actionName = "close_app";

            if (actionName.equals(action)) {
                LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(receiver_poi);//  important
                LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(gps_off);     //   important
                Intent myService = new Intent(MainActivity.this, LocationComparator.class);
                stopService(myService);
                myService = new Intent(MainActivity.this, GoogleLocationFinder.class);
                stopService(myService);
                myService = new Intent(MainActivity.this, OverlayWindow.class);
                stopService(myService);
                finish();
            }

        }

    };
    private void changeImage(String poi_type)
    {
        if (poi_type.equals("50")) {
            mainImageView.setImageResource(R.drawable.vitesse_50);//set the source in java class
            mainImageView.setVisibility(View.VISIBLE);
        } else if (poi_type.equals("70")) {
            mainImageView.setImageResource(R.drawable.vitesse_70);//set the source in java class
            mainImageView.setVisibility(View.VISIBLE);
        }else if (poi_type.equals("80")) {
            mainImageView.setImageResource(R.drawable.vitesse_80);//set the source in java class
            mainImageView.setVisibility(View.VISIBLE);
        } else if (poi_type.equals("90")) {
            mainImageView.setImageResource(R.drawable.vitesse_90);//set the source in java class
            mainImageView.setVisibility(View.VISIBLE);
        } else if (poi_type.equals("100")) {
            mainImageView.setImageResource(R.drawable.vitesse_100);//set the source in java class
            mainImageView.setVisibility(View.VISIBLE);
        }else if (poi_type.equals("radar_70")) {
            mainImageView.setImageResource(R.drawable.radar_70);//set the source in java class
            mainImageView.setVisibility(View.VISIBLE);
        } else if (poi_type.equals("120")) {
            mainImageView.setImageResource(R.drawable.vitesse_130);//set the source in java class
            mainImageView.setVisibility(View.VISIBLE);
        } else if (poi_type.equals("radar_120")) {
            mainImageView.setImageResource(R.drawable.radar_130);//set the source in java class
            mainImageView.setVisibility(View.VISIBLE);
        }

    }
    BroadcastReceiver receiver_poi = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //  Toast.makeText(MainActivity.this,intent.getStringExtra("name"),Toast.LENGTH_LONG).show();
            //Log.d("Latitude", intent.getStringExtra("latitude"));
            Log.d("poi_type", intent.getStringExtra("poi_type"));
            poi_type=intent.getStringExtra("poi_type");
            changeImage(poi_type);
            //longitude=Double.parseDouble(intent.getStringExtra("longitude"));
            //compareCoordinates();


        }

    };

    BroadcastReceiver gps_off = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            locationEnabled();

        }

    };



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
            new AlertDialog.Builder(MainActivity. this )
                    .setMessage( "GPS is not enable.Please activate GPS before to continue" )
                    .setPositiveButton( "Enable Gps" , new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick (DialogInterface paramDialogInterface , int paramInt) {
                                    //Intent gpsSettings= new Intent(Settings. ACTION_LOCATION_SOURCE_SETTINGS );
                                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS ),0);

                                }
                            })
                    .setNegativeButton( "Cancel" , null )
                    .show() ;
        }
    }



}

