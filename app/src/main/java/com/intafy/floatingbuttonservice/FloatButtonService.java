package com.intafy.floatingbuttonservice;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;

public class FloatButtonService extends Service {
    private WindowManager windowManager;
    private ImageView wifiHead;
    private WindowManager.LayoutParams params;
    private WifiManager wifiManager;

    @Override
    public void onCreate() {
        super.onCreate();

        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        windowManager=(WindowManager) getSystemService(WINDOW_SERVICE);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);


        Log.d("MyLog","WM is start");
        wifiHead = new ImageView(this);
        wifiHead.setClickable(true);
        wifiHead.setImageResource(R.drawable.wifihead);

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP|Gravity.START;
        params.x=50;
        params.y=50;

        wifiHead.setOnTouchListener(new View.OnTouchListener() {

            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            private boolean shouldCLick;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        shouldCLick=true;
                        initialX= params.x;
                        initialY= params.y;
                        initialTouchX= event.getRawX();
                        initialTouchY= event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        if(shouldCLick){
                            v.performClick();
                            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
                                Intent intent = new Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY);
                                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }else {
                                Toast.makeText(getApplicationContext(),
                                        "Клик по тосту случился",Toast.LENGTH_LONG).show();
                                wifiManager.setWifiEnabled(false);
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        shouldCLick = false;
                        params.x = initialX + (int)(event.getRawX()-initialTouchX);
                        params.y = initialY + (int)(event.getRawY()-initialTouchY);
                        windowManager.updateViewLayout(wifiHead,params);
                       return true;
                }
                return false;
            }
        });
        windowManager.addView(wifiHead,params);
    }
    public int onStartCommand(Intent intent,int flags,int startId){

        return super.onStartCommand(intent,flags,startId);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d("MyLog","wifiHead destroyed");
        if(wifiHead != null)
            windowManager.removeView(wifiHead);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
