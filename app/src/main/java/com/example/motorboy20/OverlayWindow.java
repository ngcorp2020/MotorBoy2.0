package com.example.motorboy20;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class OverlayWindow extends Service {
    private WindowManager mWindowManager;
    private String poi_type;
    private View myView;
    private View dialogView;
    boolean isMyViewLoaded=false;
    WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);
    private long thisTouchTime;
    private long previousTouchTime = 0;
    private boolean clickHandled = false;
    private long DOUBLE_CLICK_INTERVAL = ViewConfiguration.getDoubleTapTimeout();


   public OverlayWindow(){}
    @Override
    public void onCreate() {
        super.onCreate();

        //getting the widget layout from xml using layout inflater

        dialogView= LayoutInflater.from(this).inflate(R.layout.overlay_dialog_layout, null);
        //overlayImageView= (ImageView) LayoutInflater.from(this).inflate(R.id.overlayImageView);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }



        //getting windows services and adding the floating view to it
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(dialogView,params);
        dialogView.findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               mWindowManager.removeView(dialogView);
               mWindowManager.addView(myView, params);
               if(poi_type!=null)
                  changeImage(poi_type);
               isMyViewLoaded=true;
           }
       }

        );
        dialogView.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(getApplicationContext(), MainActivity.class);
               intent.putExtra("POI",poi_type);
               intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
               startActivity(intent);
               /*PendingIntent pendingIntent =
                       PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
               try {
                   pendingIntent.send();
               } catch (PendingIntent.CanceledException e) {
                   e.printStackTrace();
               }*/
           }
       }

        );
        // mWindowManager.addView(myView, params);
        //adding an touchlistener to make drag movement of the floating widget
        dialogView.findViewById(R.id.dialog_overlay_layout).setOnTouchListener(new View.OnTouchListener() {

            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        thisTouchTime = System.currentTimeMillis();
                        return true;

                    case MotionEvent.ACTION_UP:

                        //when the drag is ended switching the state of the widget
                        // collapsedView.setVisibility(View.GONE);
                        //expandedView.setVisibility(View.VISIBLE);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        //this code is helping the widget to move around the screen with fingers
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        mWindowManager.updateViewLayout(dialogView, params);
                        return true;

                }
                return false;
            }
        });
       // mWindowManager.addView(myView, params);
       //adding an touchlistener to make drag movement of the floating widget
        myView = LayoutInflater.from(this).inflate(R.layout.overlaylayout, null);

        myView.findViewById(R.id.relativeLayoutParent).setOnTouchListener(new View.OnTouchListener() {

            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        thisTouchTime = System.currentTimeMillis();
                        if (thisTouchTime - previousTouchTime <= DOUBLE_CLICK_INTERVAL) {
                            // double click detected
                            clickHandled = true;
                            onDoubleClick(event);
                        } else {
                            // defer event handling until later
                            clickHandled = false;
                        }
                        previousTouchTime = thisTouchTime;
                        return true;

                    case MotionEvent.ACTION_UP:

                        //when the drag is ended switching the state of the widget
                       // collapsedView.setVisibility(View.GONE);
                        //expandedView.setVisibility(View.VISIBLE);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        //this code is helping the widget to move around the screen with fingers
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        mWindowManager.updateViewLayout(myView, params);
                        return true;

                }
                return false;
            }
        });

    }

    private void onDoubleClick(MotionEvent event) {
        /*Intent start_main_activity = new Intent(".MainActivity");
        start_main_activity.setComponent(new ComponentName(getPackageName(), MainActivity.class.getName()));
        start_main_activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(start_main_activity);*/

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("POI",poi_type);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        /*PendingIntent pendingIntent =
                PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }*/
        //onDestroy();
        //stopSelf();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       //super.onStartCommand(intent,flags,startId);


        Bundle extras = intent.getExtras();

        if(extras == null) {
            Log.d("OverlayService","null");
        } else {
            Log.d("OverlayService","not null");
             poi_type = (String) extras.get("poi_type");

        }
        LocalBroadcastManager.getInstance(OverlayWindow.this).registerReceiver(receiver, new IntentFilter("poi_type"));
        return Service.START_STICKY;
    }
   private void changeImage(String poi_type)
   {
       if (poi_type.equals("50")) {

           myView.findViewById(R.id.speed60_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed70_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed80_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed90_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed100_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed60_radar_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed70_radar_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed60_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed50_layout).setVisibility(View.VISIBLE);


       } else if (poi_type.equals("70")) {

           myView.findViewById(R.id.speed50_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed60_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed80_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed90_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed100_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed60_radar_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed70_radar_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed60_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed70_layout).setVisibility(View.VISIBLE);

       }else if (poi_type.equals("80")) {
           //myView.findViewById(R.id.relativeLayoutParent).setVisibility(View.VISIBLE);

           myView.findViewById(R.id.speed50_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed60_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed70_layout).setVisibility(View.GONE);
           //myView.findViewById(R.id.speed80_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed90_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed100_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed60_radar_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed70_radar_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed60_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed80_layout).setVisibility(View.VISIBLE);

           // myView.setVisibility(View.VISIBLE);
//                mWindowManager.addView(myView, params);
       } else if (poi_type.equals("90")) {

           myView.findViewById(R.id.speed50_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed60_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed70_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed80_layout).setVisibility(View.GONE);
           // myView.findViewById(R.id.speed90_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed100_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed60_radar_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed70_radar_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed60_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed90_layout).setVisibility(View.VISIBLE);

       } else if (poi_type.equals("100")) {

           myView.findViewById(R.id.speed50_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed60_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed70_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed80_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed90_layout).setVisibility(View.GONE);
           //myView.findViewById(R.id.speed100_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed60_radar_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed70_radar_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed60_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed100_layout).setVisibility(View.VISIBLE);

       }else if (poi_type.equals("radar_70")) {

           myView.findViewById(R.id.speed50_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed60_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed70_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed80_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed90_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed100_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed60_radar_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed120_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed60_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed70_radar_layout).setVisibility(View.VISIBLE);

       } else if (poi_type.equals("120")) {

           myView.findViewById(R.id.speed50_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed60_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed70_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed80_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed90_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed100_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed60_radar_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed70_radar_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed60_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed120_layout).setVisibility(View.VISIBLE);

       } else if (poi_type.equals("radar_120")) {

           myView.findViewById(R.id.speed50_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed60_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed70_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed80_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed90_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed100_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed120_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed60_radar_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed70_radar_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed60_layout).setVisibility(View.GONE);
           myView.findViewById(R.id.speed60_radar_layout).setVisibility(View.VISIBLE);

       }



   }
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @SuppressLint("ResourceType")
        @Override
        public void onReceive(Context context, Intent intent) {

             poi_type = intent.getStringExtra("poi_type");
             changeImage(poi_type);
            //longitude=Double.parseDouble(intent.getStringExtra("longitude"));
            //compareCoordinates();


        }

    };
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy() {
        super.onDestroy();
        if (myView != null && isMyViewLoaded )
            mWindowManager.removeView(myView);
        else if(dialogView!=null)
            mWindowManager.removeView(dialogView);
    }
}
